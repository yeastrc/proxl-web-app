package org.yeastrc.proxl.import_xml_to_db_submit_pgm.java_console_abstraction;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;

public class JavaConsoleAbstractionFactory {

	private static JavaConsoleAbstraction DEFAULT = null;
	
	static {
		DEFAULT = (System.console() == null) ? streamBasedIO(
			System.in, System.out)
			: new SystemConsole(System.console());
	}
	

	public static JavaConsoleAbstraction defaultConsoleIO() {
		return DEFAULT;
	}


	public static JavaConsoleAbstraction streamBasedIO(InputStream in, OutputStream out) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		PrintWriter writer = new PrintWriter(out, true);
		return new SystemIO(reader, writer);
	}


	private static class SystemConsole extends JavaConsoleAbstraction {

		Console console;
		
		public SystemConsole(Console console) {
			super();
			this.console = console;
		}

		@Override
		public String readLine() throws JavaConsoleAbstractionException, IOException {
			
			return console.readLine();
		}

		@Override
		public char[] readPassword() throws JavaConsoleAbstractionException, IOException {

			return console.readPassword();
		}

		@Override
		public Reader reader() throws JavaConsoleAbstractionException {

			return console.reader();
		}

		@Override
		public PrintWriter writer() throws JavaConsoleAbstractionException {

			return console.writer();
		}
	}
	

	private static class SystemIO extends JavaConsoleAbstraction {
		
		BufferedReader reader;
		PrintWriter writer;
		
		public SystemIO(BufferedReader reader, PrintWriter writer) {
			super();
			this.reader = reader;
			this.writer = writer;
		}

		
		@Override
		public String readLine() throws JavaConsoleAbstractionException, IOException {

			return reader.readLine();
		}
		@Override
		public char[] readPassword() throws JavaConsoleAbstractionException, IOException {

			String line = reader.readLine();
			
			return line.toCharArray();
		}
		@Override
		public Reader reader() throws JavaConsoleAbstractionException {

			return reader;
		}
		@Override
		public PrintWriter writer() throws JavaConsoleAbstractionException {

			return writer;
		}


		
	}

}
