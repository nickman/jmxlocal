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

/**
 * <p>Title: BuiltIn</p>
 * <p>Description: Enumerates the built in JMX commands</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.builtins.BuiltIn</code></p>
 */

public enum BuiltIn implements IBuiltIn {
	HDUMP("Executes a heap dump. Args: <file name> [<include live refs(true/false)>]", new HeapDumpBuiltIn()),
	JAVA("Compiles the passed code and executes, passing in the MBeanServerConnection. Args: <code>", new DynamicBuiltIn());
	
	private BuiltIn(final String help, final IBuiltIn builtIn) {
		this.help = help;
		this.builtIn = builtIn;
	}
	
	public final String help;
	public final IBuiltIn builtIn;
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.utils.jmx.builtins.IBuiltIn#execute(javax.management.MBeanServerConnection, java.lang.String[])
	 */
	@Override
	public Object execute(final MBeanServerConnection conn, final String... args) throws Exception {
		return builtIn.execute(conn, args);
	}
}
