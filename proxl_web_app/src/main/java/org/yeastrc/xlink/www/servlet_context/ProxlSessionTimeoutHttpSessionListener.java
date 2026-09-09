package org.yeastrc.xlink.www.servlet_context;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * Optionally overrides the HTTP session inactivity timeout, per-deployment, via an
 * environment variable, so a PULLED prebuilt image can be tuned without editing web.xml.
 *
 * Environment variable: PROXL_SESSION_TIMEOUT_MINUTES
 *   - A positive integer number of MINUTES (e.g. 2880 for 48 hours).
 *   - Unset or blank => feature is inactive: no override is applied, so the existing
 *     web.xml / servlet-container default (Tomcat's default is 30 minutes) is used.
 *
 * The env var is read + validated ONCE at class load and cached in a static final field
 * (no per-session re-read, no memoization helper).
 */
public class ProxlSessionTimeoutHttpSessionListener implements HttpSessionListener {

	private static final Logger log = LoggerFactory.getLogger( ProxlSessionTimeoutHttpSessionListener.class );

	private static final String SESSION_TIMEOUT_MINUTES_ENV_VAR_NAME = "PROXL_SESSION_TIMEOUT_MINUTES";

	/**
	 * The session max-inactive-interval to apply, in SECONDS, or null if no override is configured.
	 * Computed ONCE at class load.
	 */
	private static final Integer CONFIGURED_MAX_INACTIVE_INTERVAL_SECONDS = computeConfiguredMaxInactiveIntervalSeconds();

	/**
	 * Read and validate the env var once. Returns the timeout in seconds, or null if the
	 * feature is inactive (unset/blank/invalid).
	 */
	private static Integer computeConfiguredMaxInactiveIntervalSeconds() {

		String raw = System.getenv( SESSION_TIMEOUT_MINUTES_ENV_VAR_NAME );

		if ( raw == null || raw.trim().isEmpty() ) {
			log.info( "No session-timeout override configured (env var '" + SESSION_TIMEOUT_MINUTES_ENV_VAR_NAME
					+ "' is not set). Using the web.xml / servlet-container default session timeout." );
			return null;
		}

		String trimmed = raw.trim();

		int minutes;
		try {
			minutes = Integer.parseInt( trimmed );
		} catch ( NumberFormatException e ) {
			log.error( "Invalid value for env var '" + SESSION_TIMEOUT_MINUTES_ENV_VAR_NAME
					+ "': '" + trimmed + "' is not an integer. No session-timeout override will be applied;"
					+ " using the web.xml / servlet-container default session timeout." );
			return null;
		}

		if ( minutes <= 0 ) {
			log.error( "Invalid value for env var '" + SESSION_TIMEOUT_MINUTES_ENV_VAR_NAME
					+ "': '" + trimmed + "'. Value must be a positive integer number of minutes."
					+ " No session-timeout override will be applied;"
					+ " using the web.xml / servlet-container default session timeout." );
			return null;
		}

		int seconds = minutes * 60;

		log.info( "Session-timeout override configured via env var '" + SESSION_TIMEOUT_MINUTES_ENV_VAR_NAME
				+ "': effective session inactivity timeout is " + minutes + " minutes (" + seconds + " seconds)." );

		return seconds;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpSessionListener#sessionCreated(jakarta.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated( HttpSessionEvent se ) {

		if ( CONFIGURED_MAX_INACTIVE_INTERVAL_SECONDS != null ) {
			se.getSession().setMaxInactiveInterval( CONFIGURED_MAX_INACTIVE_INTERVAL_SECONDS );
		}
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpSessionListener#sessionDestroyed(jakarta.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed( HttpSessionEvent se ) {
		//  no-op
	}
}
