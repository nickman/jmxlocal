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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

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
	 * Creates a new Command
	 */
	public Command() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// JMXService URL, [user name, password] command
		String jmxUrl = null;
		String userName = null;
		String password = null;
		String command = null;
		Class<?> clazz = null;
		JMXConnector connector = null;
		MBeanServerConnection conn = null;
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-j")) {
				jmxUrl = args[++i];
			} else if(args[i].equals("-u")) {
				userName = args[++i];
			} else if(args[i].equals("-p")) {
				password = args[++i];
			} else if(args[i].equals("-c")) {
				command = args[++i];
			}
		}
		try {			
			final ClassLoader cl = VirtualMachineBootstrap.getToolsClassLoader();
			final ClassLoader current = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				final String code = buildCode(command);
//				System.out.println("\n" + code + "\n");
				clazz = CommandCompiler.compile(code);
			} finally {
				Thread.currentThread().setContextClassLoader(current);
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			System.exit(-1);
		}
		try {			
			VirtualMachineBootstrap.getInstance();
			JMXServiceURL surl = new JMXServiceURL(jmxUrl);
			Map<String, Object> env = new HashMap<String, Object>();
			if(userName!=null) {
				env.put(JMXConnector.CREDENTIALS, new String[]{userName, password});
			}
			connector = JMXConnectorFactory.connect(surl, env);
			conn = connector.getMBeanServerConnection();
			// public static Object execute(final MBeanServerConnection conn) 
			Method m = clazz.getDeclaredMethod("execute", MBeanServerConnection.class);
			Object result = m.invoke(null, conn);
			System.out.println("Command Executed. Result [" + result + "]");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			System.exit(-1);
		} finally {
			if(connector!=null) try { connector.close(); } catch (Exception x) {/* No Op */}
		}
		
		
	}
	
	
	public static String buildCode(final String command) throws Exception {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String template = null;
		try {
			URL url = Command.class.getClassLoader().getResource("command-template.java");
			if(url==null) throw new Exception("Failed to read template");
			is = url.openStream();
			if(is==null) throw new Exception("Failed to read template");
			isr = new InputStreamReader(is, Charset.forName("UTF-8"));
			br = new BufferedReader(isr);
			StringBuilder b = new StringBuilder();
			String line = null;
			while((line = br.readLine())!=null) {
				b.append(line).append("\n");
			}
			template = b.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read template", ex);
		} finally {
			if(br!=null) try { br.close(); } catch (Exception x) {/* No Op */}
			if(isr!=null) try { isr.close(); } catch (Exception x) {/* No Op */}
			if(is!=null) try { is.close(); } catch (Exception x) {/* No Op */}
		}
		return template.replace("###c###", command);		
	}

}
