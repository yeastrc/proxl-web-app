/*
* Original author: Daniel Jaschob <djaschob .at. uw.edu>
*
* Copyright 2026 University of Washington - Seattle, WA
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.yeastrc.xlink.www.signin_limits;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.LongSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton instance
 *
 * Sign-in limits.  Used by the sign-in web service, in this order:
 *
 *  C. One sign-in at a time per username.
 *  A. Wrong passwords on an existing account, counted per username:  USER_MAX_FAILURES within USER_WINDOW_MINUTES
 *     blocks that username for USER_BLOCK_MINUTES.  The password check of user-mgmt accepting the password clears the count.
 *  D. At most MAX_CONCURRENT sign-ins at the same time (waits up to CONCURRENT_WAIT_MILLIS for a free permit).
 *  E. Alert only (no blocking):  An ERROR when ALERT_FAILURES or more failed sign-ins (any username, found or not)
 *     happen within ALERT_WINDOW_MINUTES, and an ERROR when D refuses a sign-in.  Each at most once per 60 minutes.
 *
 * Usernames are used in normalized form (see normalizeUsername(...)).  No usernames, passwords or IPs are logged.
 *
 * Configured from environment variables, read once at startup.
 */
public class SignIn_Limits {

	private static final Logger log = LoggerFactory.getLogger( SignIn_Limits.class );

	static final String ENV_VAR_NAME__LIMITS_ENABLED = "PROXL_SIGNIN_LIMITS_ENABLED";
	static final String ENV_VAR_NAME__USER_MAX_FAILURES = "PROXL_SIGNIN_USER_MAX_FAILURES";
	static final String ENV_VAR_NAME__USER_WINDOW_MINUTES = "PROXL_SIGNIN_USER_WINDOW_MINUTES";
	static final String ENV_VAR_NAME__USER_BLOCK_MINUTES = "PROXL_SIGNIN_USER_BLOCK_MINUTES";
	static final String ENV_VAR_NAME__RETRY_SECONDS = "PROXL_SIGNIN_RETRY_SECONDS";
	static final String ENV_VAR_NAME__MAX_CONCURRENT = "PROXL_SIGNIN_MAX_CONCURRENT";
	static final String ENV_VAR_NAME__CONCURRENT_WAIT_MILLIS = "PROXL_SIGNIN_CONCURRENT_WAIT_MILLIS";
	static final String ENV_VAR_NAME__ALERT_FAILURES = "PROXL_SIGNIN_ALERT_FAILURES";
	static final String ENV_VAR_NAME__ALERT_WINDOW_MINUTES = "PROXL_SIGNIN_ALERT_WINDOW_MINUTES";

	static final int DEFAULT__USER_MAX_FAILURES = 7;
	static final int DEFAULT__USER_WINDOW_MINUTES = 15;
	static final int DEFAULT__USER_BLOCK_MINUTES = 8;
	static final int DEFAULT__RETRY_SECONDS = 30;
	static final int DEFAULT__MAX_CONCURRENT = 10;
	static final int DEFAULT__CONCURRENT_WAIT_MILLIS = 2000;
	static final int DEFAULT__ALERT_FAILURES = 50;
	static final int DEFAULT__ALERT_WINDOW_MINUTES = 5;

	/**
	 * Each alert ERROR (and the WARN for reaching USER_ENTRIES_MAX) at most once in this time
	 */
	static final long ALERT_ERROR_MIN_INTERVAL_MILLIS = 60 * 60 * 1000;

	/**
	 * At most this many usernames tracked for A
	 */
	static final int USER_ENTRIES_MAX = 10000;

	/**
	 * Remove stale A entries every this many calls
	 */
	private static final int USER_ENTRIES_CLEANUP_EVERY_N_CALLS = 100;

	private static final long MILLIS_PER_MINUTE = 60 * 1000;

	/**
	 * Configuration values in effect
	 */
	static class Config {
		final boolean enabled;
		final int userMaxFailures;
		final int userWindowMinutes;
		final int userBlockMinutes;
		final int retrySeconds;
		final int maxConcurrent;
		final int concurrentWaitMillis;
		final int alertFailures;
		final int alertWindowMinutes;

		Config( boolean enabled, int userMaxFailures, int userWindowMinutes, int userBlockMinutes, int retrySeconds,
				int maxConcurrent, int concurrentWaitMillis, int alertFailures, int alertWindowMinutes ) {
			this.enabled = enabled;
			this.userMaxFailures = userMaxFailures;
			this.userWindowMinutes = userWindowMinutes;
			this.userBlockMinutes = userBlockMinutes;
			this.retrySeconds = retrySeconds;
			this.maxConcurrent = maxConcurrent;
			this.concurrentWaitMillis = concurrentWaitMillis;
			this.alertFailures = alertFailures;
			this.alertWindowMinutes = alertWindowMinutes;
		}
	}

	private static final SignIn_Limits instance =
			new SignIn_Limits( readConfig( System::getenv ), System::currentTimeMillis );

	/**
	 * @return Singleton instance
	 */
	public static SignIn_Limits getInstance() {
		return instance;
	}

	/**
	 * @param username - not null
	 * @return The username trimmed and lower-cased, the key for the per-username limits
	 */
	public static String normalizeUsername( String username ) {
		return username.trim().toLowerCase( Locale.ROOT );
	}

	/**
	 * A:  Per username
	 */
	private static class UserEntry {

		/**
		 * Wrong-password times in milliseconds, oldest first.  At most userMaxFailures entries.
		 */
		final ArrayDeque<Long> failureTimes = new ArrayDeque<>();

		/**
		 * 0 = not blocked
		 */
		long blockedUntilMillis = 0;
	}

	private final Config config;
	private final LongSupplier clock;

	/**
	 * C:  Usernames with a sign-in in progress
	 */
	private final Set<String> signInInProgress_Usernames = ConcurrentHashMap.newKeySet();

	/**
	 * A:  Per username.  In access order (least recently active first).  Access only when synchronized on this.
	 */
	private final LinkedHashMap<String, UserEntry> userEntries = new LinkedHashMap<>( 16, 0.75f, true /* accessOrder */ );

	/**
	 * A:  Calls since the last cleanup.  Access only when synchronized on this.
	 */
	private int userEntries_CallsSinceCleanup = 0;

	/**
	 * A:  When the WARN for reaching USER_ENTRIES_MAX was last logged (at most once per 60 minutes).  null = never.
	 * Access only when synchronized on this.
	 */
	private Long userEntries_MaxReached_WarnLoggedMillis = null;

	/**
	 * D
	 */
	private final Semaphore concurrentSignIns;

	/**
	 * E:  Failed sign-in times in milliseconds, oldest first.  At most alertFailures entries.  Access only when synchronized on this.
	 */
	private final ArrayDeque<Long> alert_FailureTimes = new ArrayDeque<>();

	/**
	 * E:  When the ERRORs were last logged.  null = never.  Access only when synchronized on this.
	 */
	private Long alert_FailureRate_ErrorLoggedMillis = null;
	private Long alert_ConcurrentLimit_ErrorLoggedMillis = null;

	/**
	 * Package private for testing
	 *
	 * @param config
	 * @param clock - current time in milliseconds
	 */
	SignIn_Limits( Config config, LongSupplier clock ) {
		this.config = config;
		this.clock = clock;
		this.concurrentSignIns = new Semaphore( config.maxConcurrent, true /* fair */ );

		if ( config.enabled ) {
			log.warn( "Sign-in limits: enabled.  Per username: " + config.userMaxFailures + " wrong passwords within "
					+ config.userWindowMinutes + " minutes block that username for " + config.userBlockMinutes + " minutes."
					+ "  At most " + config.maxConcurrent + " sign-ins at the same time (wait up to " + config.concurrentWaitMillis + " ms)."
					+ "  Retry message: " + config.retrySeconds + " seconds."
					+ "  Alert (ERROR) at " + config.alertFailures + " failed sign-ins within " + config.alertWindowMinutes + " minutes." );
		} else {
			log.warn( "Sign-in limits: disabled (" + ENV_VAR_NAME__LIMITS_ENABLED + "=false)." );
		}
	}

	/**
	 * @return RETRY_SECONDS, for the messages of C and D
	 */
	public int getRetrySeconds() {
		return config.retrySeconds;
	}

	///////////////////////////

	//  C.  One sign-in at a time per username

	/**
	 * @param usernameNormalized
	 * @return false if a sign-in for this username is already in progress (refuse).
	 *         true:  marked in progress; the caller must call signInInProgress_Remove(...) in a finally.
	 */
	public boolean signInInProgress_TryAdd( String usernameNormalized ) {
		if ( ! config.enabled ) {
			return true;
		}
		return signInInProgress_Usernames.add( usernameNormalized );
	}

	/**
	 * Only after signInInProgress_TryAdd(...) returned true
	 *
	 * @param usernameNormalized
	 */
	public void signInInProgress_Remove( String usernameNormalized ) {
		if ( ! config.enabled ) {
			return;
		}
		signInInProgress_Usernames.remove( usernameNormalized );
	}

	///////////////////////////

	//  A.  Wrong passwords per username

	/**
	 * @param usernameNormalized
	 * @return null if not blocked.  Otherwise the remaining block time in seconds, rounded up to whole minutes (at least 60).
	 */
	public synchronized Integer userBlocked_RemainingSeconds( String usernameNormalized ) {

		if ( ! config.enabled ) {
			return null;
		}

		long now = clock.getAsLong();

		userEntries_CleanupIfDue( now );

		UserEntry userEntry = userEntries.get( usernameNormalized );
		if ( userEntry == null || userEntry.blockedUntilMillis == 0 ) {
			return null;
		}
		if ( now >= userEntry.blockedUntilMillis ) {
			//  Block over:  The count starts from zero
			userEntry.blockedUntilMillis = 0;
			userEntry.failureTimes.clear();
			return null;
		}
		long remainingMillis = userEntry.blockedUntilMillis - now;
		long remainingMinutes = ( remainingMillis + MILLIS_PER_MINUTE - 1 ) / MILLIS_PER_MINUTE;
		if ( remainingMinutes < 1 ) {
			remainingMinutes = 1;
		}
		return (int) ( remainingMinutes * 60 );
	}

	/**
	 * User-mgmt answered "password invalid" (the account exists).  Counts for A and for E.
	 *
	 * @param usernameNormalized
	 */
	public synchronized void recordPasswordInvalid( String usernameNormalized ) {

		if ( ! config.enabled ) {
			return;
		}

		long now = clock.getAsLong();

		alert_RecordFailure( now );

		userEntries_CleanupIfDue( now );

		UserEntry userEntry = userEntries.get( usernameNormalized );
		if ( userEntry == null ) {
			if ( userEntries.size() >= USER_ENTRIES_MAX ) {
				//  Drop the least recently active
				Iterator<Map.Entry<String, UserEntry>> iterator = userEntries.entrySet().iterator();
				iterator.next();
				iterator.remove();
				if ( userEntries_MaxReached_WarnLoggedMillis == null 
						|| now - userEntries_MaxReached_WarnLoggedMillis >= ALERT_ERROR_MIN_INTERVAL_MILLIS ) {
					userEntries_MaxReached_WarnLoggedMillis = now;
					log.warn( "Sign-in limits: per-username tracking reached its limit of " + USER_ENTRIES_MAX
							+ " usernames.  Dropping the least recently active." );
				}
			}
			userEntry = new UserEntry();
			userEntries.put( usernameNormalized, userEntry );
		}

		if ( userEntry.blockedUntilMillis != 0 ) {
			if ( now < userEntry.blockedUntilMillis ) {
				//  Blocked:  not counted (and the block is not extended)
				return;
			}
			//  Block over:  The count starts from zero
			userEntry.blockedUntilMillis = 0;
			userEntry.failureTimes.clear();
		}

		long windowStart = now - ( config.userWindowMinutes * MILLIS_PER_MINUTE );
		while ( ! userEntry.failureTimes.isEmpty() && userEntry.failureTimes.peekFirst() <= windowStart ) {
			userEntry.failureTimes.pollFirst();
		}
		userEntry.failureTimes.addLast( now );
		while ( userEntry.failureTimes.size() > config.userMaxFailures ) {
			userEntry.failureTimes.pollFirst();
		}

		if ( userEntry.failureTimes.size() >= config.userMaxFailures ) {

			userEntry.blockedUntilMillis = now + ( config.userBlockMinutes * MILLIS_PER_MINUTE );
			userEntry.failureTimes.clear();

			log.warn( "Sign-in limits: a username reached " + config.userMaxFailures + " failed sign-ins within "
					+ config.userWindowMinutes + " minutes; blocked for " + config.userBlockMinutes + " minutes." );
		}
	}

	/**
	 * User-mgmt accepted the password.  Clears the count of A for this username.
	 *
	 * @param usernameNormalized
	 */
	public synchronized void recordPasswordCorrect( String usernameNormalized ) {

		if ( ! config.enabled ) {
			return;
		}
		userEntries.remove( usernameNormalized );
	}

	/**
	 * Must be called when synchronized on this
	 *
	 * @param now
	 */
	private void userEntries_CleanupIfDue( long now ) {

		userEntries_CallsSinceCleanup++;
		if ( userEntries_CallsSinceCleanup < USER_ENTRIES_CLEANUP_EVERY_N_CALLS ) {
			return;
		}
		userEntries_CallsSinceCleanup = 0;

		long windowStart = now - ( config.userWindowMinutes * MILLIS_PER_MINUTE );

		Iterator<UserEntry> iterator = userEntries.values().iterator();
		while ( iterator.hasNext() ) {
			UserEntry userEntry = iterator.next();
			if ( userEntry.blockedUntilMillis != 0 && now < userEntry.blockedUntilMillis ) {
				continue;  //  Block active
			}
			Long lastFailure = userEntry.failureTimes.peekLast();
			if ( lastFailure == null || lastFailure <= windowStart || userEntry.blockedUntilMillis != 0 ) {
				//  No failure inside the window and no active block
				iterator.remove();
			}
		}
	}

	/**
	 * Package private for testing
	 *
	 * @return number of usernames tracked for A
	 */
	synchronized int userEntries_Size() {
		return userEntries.size();
	}

	///////////////////////////

	//  D.  Concurrent sign-ins

	/**
	 * Waits up to CONCURRENT_WAIT_MILLIS for a permit.
	 *
	 * @return false if no permit (refuse).
	 *         true:  permit acquired; the caller must call concurrentSignIn_Release() in a finally.
	 */
	public boolean concurrentSignIn_TryAcquire() {

		if ( ! config.enabled ) {
			return true;
		}
		boolean acquired;
		try {
			acquired = concurrentSignIns.tryAcquire( config.concurrentWaitMillis, TimeUnit.MILLISECONDS );
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			acquired = false;
		}
		if ( ! acquired ) {
			alert_ConcurrentLimitReached();
		}
		return acquired;
	}

	/**
	 * Only after concurrentSignIn_TryAcquire() returned true
	 */
	public void concurrentSignIn_Release() {

		if ( ! config.enabled ) {
			return;
		}
		concurrentSignIns.release();
	}

	///////////////////////////

	//  E.  Alerts (ERROR only, no blocking)

	/**
	 * User-mgmt answered "username not found".  Counts for E only.
	 */
	public synchronized void recordUsernameNotFound() {

		if ( ! config.enabled ) {
			return;
		}
		alert_RecordFailure( clock.getAsLong() );
	}

	/**
	 * Must be called when synchronized on this
	 *
	 * @param now
	 */
	private void alert_RecordFailure( long now ) {

		long windowStart = now - ( config.alertWindowMinutes * MILLIS_PER_MINUTE );
		while ( ! alert_FailureTimes.isEmpty() && alert_FailureTimes.peekFirst() <= windowStart ) {
			alert_FailureTimes.pollFirst();
		}
		alert_FailureTimes.addLast( now );
		while ( alert_FailureTimes.size() > config.alertFailures ) {
			alert_FailureTimes.pollFirst();
		}

		if ( alert_FailureTimes.size() >= config.alertFailures ) {
			if ( alert_FailureRate_ErrorLoggedMillis == null
					|| now - alert_FailureRate_ErrorLoggedMillis >= ALERT_ERROR_MIN_INTERVAL_MILLIS ) {

				alert_FailureRate_ErrorLoggedMillis = now;

				log.error( "Sign-in: " + config.alertFailures + " or more failed sign-ins within "
						+ config.alertWindowMinutes + " minutes (possible password guessing)." );
			}
		}
	}

	private synchronized void alert_ConcurrentLimitReached() {

		long now = clock.getAsLong();

		if ( alert_ConcurrentLimit_ErrorLoggedMillis == null
				|| now - alert_ConcurrentLimit_ErrorLoggedMillis >= ALERT_ERROR_MIN_INTERVAL_MILLIS ) {

			alert_ConcurrentLimit_ErrorLoggedMillis = now;

			log.error( "Sign-in: the concurrent sign-in limit of " + config.maxConcurrent + " was reached; sign-ins were refused." );
		}
	}

	///////////////////////////

	//  Configuration from environment variables

	/**
	 * Package private for testing
	 *
	 * @param getenv - environment variable lookup
	 * @return
	 */
	static Config readConfig( Function<String, String> getenv ) {

		boolean enabled = readEnabled( getenv );
		int userMaxFailures = readPositiveInt( getenv, ENV_VAR_NAME__USER_MAX_FAILURES, DEFAULT__USER_MAX_FAILURES );
		int userWindowMinutes = readPositiveInt( getenv, ENV_VAR_NAME__USER_WINDOW_MINUTES, DEFAULT__USER_WINDOW_MINUTES );
		int userBlockMinutes = readPositiveInt( getenv, ENV_VAR_NAME__USER_BLOCK_MINUTES, DEFAULT__USER_BLOCK_MINUTES );
		int retrySeconds = readPositiveInt( getenv, ENV_VAR_NAME__RETRY_SECONDS, DEFAULT__RETRY_SECONDS );
		int maxConcurrent = readPositiveInt( getenv, ENV_VAR_NAME__MAX_CONCURRENT, DEFAULT__MAX_CONCURRENT );
		int concurrentWaitMillis = readPositiveInt( getenv, ENV_VAR_NAME__CONCURRENT_WAIT_MILLIS, DEFAULT__CONCURRENT_WAIT_MILLIS );
		int alertFailures = readPositiveInt( getenv, ENV_VAR_NAME__ALERT_FAILURES, DEFAULT__ALERT_FAILURES );
		int alertWindowMinutes = readPositiveInt( getenv, ENV_VAR_NAME__ALERT_WINDOW_MINUTES, DEFAULT__ALERT_WINDOW_MINUTES );

		return new Config( enabled, userMaxFailures, userWindowMinutes, userBlockMinutes, retrySeconds,
				maxConcurrent, concurrentWaitMillis, alertFailures, alertWindowMinutes );
	}

	/**
	 * Unset, empty or "true" (any case) -> on.  "false" (any case) -> off.  Any other value -> ERROR and on.
	 *
	 * @param getenv
	 * @return
	 */
	private static boolean readEnabled( Function<String, String> getenv ) {

		String raw = getenv.apply( ENV_VAR_NAME__LIMITS_ENABLED );
		if ( raw == null || raw.trim().isEmpty() ) {
			return true;
		}
		String trimmed = raw.trim();
		if ( "false".equalsIgnoreCase( trimmed ) ) {
			return false;
		}
		if ( "true".equalsIgnoreCase( trimmed ) ) {
			return true;
		}
		log.error( "Invalid value for env var '" + ENV_VAR_NAME__LIMITS_ENABLED + "': '" + trimmed
				+ "' is not 'true' or 'false'.  The sign-in limits are enabled." );
		return true;
	}

	/**
	 * Unset or empty -> default.  Not an integer or <= 0 -> ERROR and default.
	 *
	 * @param getenv
	 * @param envVarName
	 * @param defaultValue
	 * @return
	 */
	private static int readPositiveInt( Function<String, String> getenv, String envVarName, int defaultValue ) {

		String raw = getenv.apply( envVarName );
		if ( raw == null || raw.trim().isEmpty() ) {
			return defaultValue;
		}
		String trimmed = raw.trim();
		int value;
		try {
			value = Integer.parseInt( trimmed );
		} catch ( NumberFormatException e ) {
			log.error( "Invalid value for env var '" + envVarName + "': '" + trimmed
					+ "' is not an integer.  Using the default: " + defaultValue );
			return defaultValue;
		}
		if ( value <= 0 ) {
			log.error( "Invalid value for env var '" + envVarName + "': '" + trimmed
					+ "' is not a positive integer.  Using the default: " + defaultValue );
			return defaultValue;
		}
		return value;
	}
}
