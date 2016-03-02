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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.heliosapm.shorthand.attach.vm.VirtualMachineBootstrap;

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
		CmdLineParser parser = new CmdLineParser(cl);
		try {
			parser.parseArgument(args);
			System.out.println(cl);  					
		} catch (CmdLineException ex) {
			System.err.println(ex.getMessage());
      parser.printUsage(System.err);			
		}
//		// JMXService URL, [user name, password] command
//		String jmxUrl = null;
//		String userName = null;
//		String password = null;
//		String command = null;
//		String[] commandArgs = null;
//		Class<?> clazz = null;
//		JMXConnector connector = null;
//		MBeanServerConnection conn = null;
//		for(int i = 0; i < args.length; i++) {
//			if(args[i].equals("-j")) {
//				jmxUrl = args[++i];
//			} else if(args[i].equals("-u")) {
//				userName = args[++i];
//			} else if(args[i].equals("-p")) {
//				password = args[++i];
//			} else if(args[i].equals("-c")) {
//				int remaining = args.length-1;
//				commandArgs = new String[remaining];
//				System.arraycopy(args, remaining, commandArgs, 0, remaining);
//				System.out.println("Command: " + Arrays.toString(commandArgs));
//				break;
//			}
//		}
//		try {			
//			VirtualMachineBootstrap.getInstance();
//			JMXServiceURL surl = new JMXServiceURL(jmxUrl);
//			Map<String, Object> env = new HashMap<String, Object>();
//			if(userName!=null) {
//				env.put(JMXConnector.CREDENTIALS, new String[]{userName, password});
//			}
//			connector = JMXConnectorFactory.connect(surl, env);
//			conn = connector.getMBeanServerConnection();
//			// public static Object execute(final MBeanServerConnection conn) 
//			Method m = clazz.getDeclaredMethod("execute", MBeanServerConnection.class);
//			Object result = m.invoke(null, conn);
//			System.out.println("Command Executed. Result [" + result + "]");
//		} catch (Exception ex) {
//			ex.printStackTrace(System.err);
//			System.exit(-1);
//		} finally {
//			if(connector!=null) try { connector.close(); } catch (Exception x) {/* No Op */}
//		}
		
		
	}
	
//	protected static void 
	

}
