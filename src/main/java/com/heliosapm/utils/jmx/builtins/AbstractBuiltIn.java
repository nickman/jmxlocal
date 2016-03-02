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
package com.heliosapm.utils.jmx.builtins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * <p>Title: AbstractBuiltIn</p>
 * <p>Description: Base built in abstract class, put here to support some basic JMX shortcuts.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.builtins.AbstractBuiltIn</code></p>
 */

public abstract class AbstractBuiltIn implements IBuiltIn {
	/** The runtime mx bean ObjectName so we can get the pid */
	public static final ObjectName RUNTIMEMX = on(ManagementFactory.RUNTIME_MXBEAN_NAME);
	/** GC MXBean ObjectName filter */
	public static final ObjectName GC_PATTERN = on(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*");
	/** The Memory MXBean object name */
	public static final ObjectName MEM_MXBEAN = on("java.lang:type=Memory");
	
	
	/**Empty Object arr const  */
	public static final Object[] EMPTY_PARAMS = {};
	/**Empty String arr const  */
	public static final String[] EMPTY_SIG = {};


	/**
	 * Creates a JMX ObjectName from the passed name
	 * @param name the name to compile into an ObjectName
	 * @return the ObjectName
	 */
	public static ObjectName on(final String name) {
		try {
			return new ObjectName(name);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to build ObjectName from [" + name + "]", ex);
		}
	}
	
	/**
	 * Returns the value for a named attribute in the specified MBean
	 * @param conn The MBeanServerConnection to retrieve the value from
	 * @param objectName The ObjectName of the target MBean 
	 * @param attributeName The attribute name
	 * @return the attribute value
	 */
	@SuppressWarnings("unchecked")
	public static <T> T attr(final MBeanServerConnection conn, final ObjectName objectName, final String attributeName) {
		try {
			return (T)conn.getAttribute(objectName, attributeName);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get attribute [" + attributeName + "] from MBean [" + objectName + "]", ex);
		}		
	}
	
	/**
	 * Returns the value for a named attribute in the specified MBean
	 * @param conn The MBeanServerConnection to retrieve the value from
	 * @param objectName The ObjectName of the target MBean 
	 * @param attributeName The attribute name
	 * @return the attribute value
	 */
	public static <T> T attr(final MBeanServerConnection conn, final String objectName, final String attributeName) {
		return attr(conn, on(objectName), attributeName);
	}
	
	/**
	 * Returns the PID of the JVM where the passed connection is connected to
	 * @param conn The MBeanServerConnection to retrieve the PID from 
	 * @return the PID
	 */
	public String pid(final MBeanServerConnection conn) {
		return attr(conn, RUNTIMEMX, "Name").toString().split("@")[0];
	}
	
	/**
	 * @param conn The MBeanServerConnection to invoke the op on
	 * @param objectName The ObjectName of the target MBean
	 * @param opName The operation name to invoke
	 * @param params The invocation parameters
	 * @param signature The operation parameter signature
	 * @return the return value from the op invocation (possibly null)
	 */
	public Object invoke(final MBeanServerConnection conn, final ObjectName objectName, final String opName, final Object[] params, final String[] signature) {
		try {
			return conn.invoke(objectName, opName, params, signature);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to invoke op [" + opName + "] on MBean [" + objectName + "]", ex);
		}		
	}
	
	/**
	 * @param conn The MBeanServerConnection to invoke the op on
	 * @param objectName The ObjectName of the target MBean
	 * @param opName The operation name to invoke
	 * @param params The invocation parameters
	 * @param signature The operation parameter signature
	 * @return the return value from the op invocation (possibly null)
	 */
	public Object invoke(final MBeanServerConnection conn, final String objectName, final String opName, final Object[] params, final String[] signature) {
		return invoke(conn, on(objectName), opName, params, signature);
	}

	/**
	 * Reads in a file as UTF8 text and returns the value in a string
	 * @param fileName The file name
	 * @return the text
	 */
	public static String getFileText(final String fileName) {
		final File f = new File(fileName);
		if(!f.exists()) throw new IllegalArgumentException("File [" + fileName + "] does not exist");
		if(!f.canRead()) throw new IllegalArgumentException("File [" + fileName + "] is not readable");
		StringBuilder b = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileInputStream is = null;		
		try {
			is = new FileInputStream(f);
			isr = new InputStreamReader(is, Charset.forName("UTF8"));
			br = new BufferedReader(isr);
			String line = null;
			while((line=br.readLine())!=null) {
				b.append(line).append("\n");
			}
			return b.toString();			
		} catch (Exception e) {
			throw new RuntimeException("Failed to read file [" + fileName + "]", e);
		} finally {
			if(br!=null) try { br.close(); } catch (Exception e) {/* No Op */}
			if(isr!=null) try { isr.close(); } catch (Exception e) {/* No Op */}
			if(is!=null) try { is.close(); } catch (Exception e) {/* No Op */}
		}
		
	}
	
	
	public Object execute(final MBeanServerConnection conn, final String... args) throws Exception {
		return doExecute(conn, args);
	}
	
	
	protected abstract Object doExecute(final MBeanServerConnection conn, final String... args) throws Exception;
		
	


}
