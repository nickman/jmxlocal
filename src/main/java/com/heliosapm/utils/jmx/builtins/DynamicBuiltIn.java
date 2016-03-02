/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.utils.jmx.builtins;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;

import javax.management.MBeanServerConnection;

import com.heliosapm.shorthand.attach.vm.VirtualMachineBootstrap;
import com.heliosapm.utils.jmx.Command;
import com.heliosapm.utils.jmx.CommandCompiler;

/**
 * <p>Title: DynamicBuiltIn</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.builtins.DynamicBuiltIn</code></p>
 */

public class DynamicBuiltIn extends AbstractBuiltIn {

	/**
	 * Creates a new DynamicBuiltIn
	 */
	public DynamicBuiltIn() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.utils.jmx.builtins.AbstractBuiltIn#doExecute(javax.management.MBeanServerConnection, java.lang.String[])
	 */
	@Override
	protected Object doExecute(final MBeanServerConnection conn, final String... args) throws Exception {
		try {			
			final ClassLoader cl = VirtualMachineBootstrap.getToolsClassLoader();
			final ClassLoader current = Thread.currentThread().getContextClassLoader();
			Class<?> clazz = null;
			try {
				Thread.currentThread().setContextClassLoader(cl);
				final String code = buildCode(args[0]);
//				System.out.println("\n" + code + "\n");
				clazz = CommandCompiler.compile(code);
				Method m = clazz.getDeclaredMethod("execute", MBeanServerConnection.class);
				Object result = m.invoke(null, conn);
				System.out.println("Command Executed. Result [" + result + "]");
			} finally {
				Thread.currentThread().setContextClassLoader(current);
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			System.exit(-1);
		}

		return null;
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