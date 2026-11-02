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
package org.yeastrc.xlink.www.user_mgmt_webapp_access;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton instance
 *
 * Periodic re-check of the User Mgmt 'enabled' flag for signed in users, cached per User Mgmt user id.
 *
 * The re-check interval is received from User Mgmt on its responses (see receivedIntervalFromUserMgmt(...)).
 * When no interval has been received (User Mgmt embedded in this webapp), there is no re-check.
 *
 * Per user:  At most one call to User Mgmt per interval, shared by all of that user's sessions and parallel requests.
 *
 * If the call to User Mgmt fails:  The last known value is used (the user stays signed in),
 * and all re-checks for all users skip the call for BACKOFF_MILLIS.  After that, a single call (probe) is made
 * and the other users keep using their last known value until that probe succeeds.
 */
public class UserMgmt_UserEnabled_RecheckCache {

	private static final Logger log = LoggerFactory.getLogger( UserMgmt_UserEnabled_RecheckCache.class );

	/**
	 * Timeout for the re-check call to User Mgmt
	 */
	static final int RECHECK_CALL_TOTAL_TIMEOUT_MILLIS = 1500;

	static final int INTERVAL_SECONDS_MIN = 30;
	static final int INTERVAL_SECONDS_MAX = 3600;

	/**
	 * After a failed call, no re-check calls for this long, for all users
	 */
	static final long BACKOFF_MILLIS = 60 * 1000;

	/**
	 * The call to User Mgmt, an interface so it can be replaced for testing
	 */
	interface Recheck_Caller {
		UserMgmtGetAccountEnabledResponse getAccountEnabled_ForRecheck( int userMgmtUserId, int totalTimeoutMillis ) throws Exception;
	}

	private static final UserMgmt_UserEnabled_RecheckCache instance =
			new UserMgmt_UserEnabled_RecheckCache( ( int userMgmtUserId, int totalTimeoutMillis ) -> {

				UserMgmtGetAccountEnabledRequest userMgmtGetAccountEnabledRequest = new UserMgmtGetAccountEnabledRequest();
				userMgmtGetAccountEnabledRequest.setUserId( userMgmtUserId );
				return UserMgmtCentralWebappWebserviceAccess.getInstance().getAccountEnabled_ForRecheck( userMgmtGetAccountEnabledRequest, totalTimeoutMillis );
			} );

	/**
	 * @return Singleton instance
	 */
	public static UserMgmt_UserEnabled_RecheckCache getInstance() {
		return instance;
	}

	/**
	 * Package private for testing
	 *
	 * @param recheck_Caller
	 */
	UserMgmt_UserEnabled_RecheckCache( Recheck_Caller recheck_Caller ) {
		this.recheck_Caller = recheck_Caller;
	}

	/**
	 * Cache entry per User Mgmt user id.  The entry object is also the lock for that user.
	 */
	private static class Entry {

		volatile boolean enabled;

		/**
		 * 0 = never checked
		 */
		volatile long lastCheckedMillis;
	}

	private final Recheck_Caller recheck_Caller;

	private final ConcurrentHashMap<Integer, Entry> entries_Key_UserMgmtUserId = new ConcurrentHashMap<>();

	/**
	 * 0 = No interval received so no re-check
	 */
	private volatile int intervalSeconds = 0;

	/**
	 * The last value received, before clamping.  Used to only log a WARN when the received value changes.
	 */
	private volatile Integer intervalSeconds_LastReceived_BeforeClamp;

	/**
	 * Back-off:  No re-check calls before this time
	 */
	private volatile long dontCallBeforeMillis = 0;

	/**
	 * true after a failed call, until a call succeeds
	 */
	private volatile boolean lastCallFailed = false;

	/**
	 * After the back-off expires, only one thread may make the call (the probe)
	 */
	private final AtomicBoolean probeInFlight = new AtomicBoolean( false );

	private final Object backoff_Start_Lock = new Object();


	/**
	 * Called with the value on every response from User Mgmt.
	 *
	 * @param intervalSeconds_Received - null when User Mgmt did not send a value (current value kept)
	 */
	public void receivedIntervalFromUserMgmt( Integer intervalSeconds_Received ) {

		if ( intervalSeconds_Received == null ) {
			return; // EARLY RETURN
		}

		if ( intervalSeconds_Received.equals( intervalSeconds_LastReceived_BeforeClamp ) ) {
			//  Same as last received so already processed
			return; // EARLY RETURN
		}

		synchronized ( this ) {

			if ( intervalSeconds_Received.equals( intervalSeconds_LastReceived_BeforeClamp ) ) {
				//  Another thread just processed it
				return; // EARLY RETURN
			}

			int intervalSeconds_New = intervalSeconds_Received;

			if ( intervalSeconds_New < INTERVAL_SECONDS_MIN ) {
				intervalSeconds_New = INTERVAL_SECONDS_MIN;
			} else if ( intervalSeconds_New > INTERVAL_SECONDS_MAX ) {
				intervalSeconds_New = INTERVAL_SECONDS_MAX;
			}
			if ( intervalSeconds_New != intervalSeconds_Received.intValue() ) {
				log.warn( "User enabled re-check interval received from User Mgmt is outside the allowed range of "
						+ INTERVAL_SECONDS_MIN + " to " + INTERVAL_SECONDS_MAX + " seconds.  Received: " + intervalSeconds_Received
						+ ", using: " + intervalSeconds_New );
			}

			this.intervalSeconds = intervalSeconds_New;
			this.intervalSeconds_LastReceived_BeforeClamp = intervalSeconds_Received;
		}
	}

	/**
	 * Call after a successful sign in or create account, so no re-check is made until the interval has passed.
	 *
	 * @param userMgmtUserId
	 */
	public void setUserEnabled_CheckedNow( int userMgmtUserId ) {

		Entry entry = entries_Key_UserMgmtUserId.computeIfAbsent( userMgmtUserId, key -> new Entry() );

		synchronized ( entry ) {
			entry.enabled = true;
			entry.lastCheckedMillis = System.currentTimeMillis();
		}
	}

	/**
	 * Is the user enabled in User Mgmt?
	 *
	 * Calls User Mgmt only when no interval has been received (returns true), or the user's last check is older than the interval.
	 *
	 * @param userMgmtUserId
	 * @return false if User Mgmt reports the user disabled or not found.  true otherwise, including when the call fails.
	 */
	public boolean isUserEnabled( int userMgmtUserId ) {

		int intervalSeconds_Local = this.intervalSeconds;

		if ( intervalSeconds_Local <= 0 ) {
			//  No interval received from User Mgmt so no re-check
			return true; // EARLY RETURN
		}

		long intervalMillis = intervalSeconds_Local * 1000L;

		Entry entry = entries_Key_UserMgmtUserId.computeIfAbsent( userMgmtUserId, key -> new Entry() );

		if ( System.currentTimeMillis() - entry.lastCheckedMillis < intervalMillis ) {
			//  Checked recently, no lock
			return entry.enabled; // EARLY RETURN
		}

		synchronized ( entry ) {

			if ( System.currentTimeMillis() - entry.lastCheckedMillis < intervalMillis ) {
				//  Another thread just checked
				return entry.enabled; // EARLY RETURN
			}

			if ( System.currentTimeMillis() < dontCallBeforeMillis ) {
				//  Back-off active, use last known value
				return entry_Enabled_LastKnown( entry ); // EARLY RETURN
			}

			boolean isProbe = false;

			if ( lastCallFailed ) {
				//  Back-off expired after a failed call.  Only one thread makes the call (the probe).
				if ( ! probeInFlight.compareAndSet( false, true ) ) {
					//  Another thread is making the probe call.  Treat as back-off.
					return entry_Enabled_LastKnown( entry ); // EARLY RETURN
				}
				isProbe = true;
			}

			boolean callSuccessful = false;

			try {
				UserMgmtGetAccountEnabledResponse response =
						recheck_Caller.getAccountEnabled_ForRecheck( userMgmtUserId, RECHECK_CALL_TOTAL_TIMEOUT_MILLIS );

				if ( response.isUserIdNotFound() ) {

					entry.enabled = false;
					entry.lastCheckedMillis = System.currentTimeMillis();
					callSuccessful = true;

				} else if ( response.isSuccess() ) {

					entry.enabled = response.isEnabled();
					entry.lastCheckedMillis = System.currentTimeMillis();
					callSuccessful = true;

					if ( response.isEnabled() ) {
						if ( log.isDebugEnabled() ) {
							log.debug( "User enabled re-check:  User is enabled in User Mgmt.  userMgmtUserId: " + userMgmtUserId );
						}
					}
				} else {
					startBackoff( "Response not success.  response: " + response );
				}
			} catch ( Exception e ) {
				startBackoff( "Exception: " + e.toString() );
			} finally {
				if ( callSuccessful ) {
					lastCallFailed = false;
				}
				if ( isProbe ) {
					probeInFlight.set( false );
				}
			}

			return entry_Enabled_LastKnown( entry );
		}
	}

	/**
	 * @param entry
	 * @return Last known value.  true if never checked.
	 */
	private boolean entry_Enabled_LastKnown( Entry entry ) {

		if ( entry.lastCheckedMillis == 0 ) {
			//  Never checked so not known to be disabled
			return true;
		}
		return entry.enabled;
	}

	/**
	 * Start the back-off after a failed call.  WARN logged once per back-off.
	 *
	 * @param failureDescription
	 */
	private void startBackoff( String failureDescription ) {

		synchronized ( backoff_Start_Lock ) {

			lastCallFailed = true;

			long now = System.currentTimeMillis();

			if ( now < dontCallBeforeMillis ) {
				//  Back-off already active (another thread's call failed at the same time)
				return; // EARLY RETURN
			}

			dontCallBeforeMillis = now + BACKOFF_MILLIS;

			String msg = "User enabled re-check:  Call to User Mgmt failed.  Users stay signed in with their last known value."
					+ "  No re-check calls for " + ( BACKOFF_MILLIS / 1000 ) + " seconds.  " + failureDescription;
			log.warn( msg );
		}
	}
}
