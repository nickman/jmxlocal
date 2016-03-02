/*
 * Copyright 2015 the original author or authors.
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
package com.heliosapm.utils.jmx;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

/**
 * <p>Title: Command</p>
 * <p>Description: Accepts a command line jmx invocation</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.Command</code></p>
 */

public class Command {

	/**
	 * Accepts command line directives.
	 * @param args Parsed via args4j. See {@link CommandLine}
	 */
	public static void main(String[] args) {
		CommandLine cl = new CommandLine();
		ParserProperties properties = ParserProperties.defaults();
		properties.withOptionSorter(null);
		CmdLineParser parser = new CmdLineParser(cl, properties);		
		try {
			parser.parseArgument(args);			 				
		} catch (CmdLineException ex) {
			System.err.println(ex.getMessage());
			parser.printUsage(System.err);
			System.exit(1);
		}
		try {
			final Object result = cl.execute();
			System.out.println("Completed Successfully. Result:" + (result==null ? "" : result));
			System.exit(0);
		} catch (Exception ex) {
			System.err.println("FAILED.:" + ex);
			if(cl.isPrintStackTrace()) {
				ex.printStackTrace(System.err);
			}
			System.exit(-1);
		} finally {
			cl.close();
		}
	}
	
	

}
