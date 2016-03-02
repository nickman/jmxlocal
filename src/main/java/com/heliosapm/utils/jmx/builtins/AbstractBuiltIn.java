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
	
	public Object execute(final MBeanServerConnection conn, final String... args) {
		try {
			return doExecute(conn, args);
		} catch (Throwable t) {
			return "FAILED: " + t.getMessage();
		}
	}
	
	protected abstract Object doExecute(final MBeanServerConnection conn, final String... args) throws Exception;
		
	


}
