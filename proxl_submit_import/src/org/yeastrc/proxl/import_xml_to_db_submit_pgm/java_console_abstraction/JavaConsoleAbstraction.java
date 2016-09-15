package org.yeastrc.proxl.import_xml_to_db_submit_pgm.java_console_abstraction;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;


/**
 * This exists for supporting running in IDE and possible test environments
 *
 */
public abstract class JavaConsoleAbstraction {

	public abstract String readLine() throws JavaConsoleAbstractionException, IOException;

	public abstract char[] readPassword() throws JavaConsoleAbstractionException, IOException;

	public abstract Reader reader() throws JavaConsoleAbstractionException;

	public abstract PrintWriter writer() throws JavaConsoleAbstractionException;
}
