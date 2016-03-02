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
 * <p>Title: HeapDumpBuiltIn</p>
 * <p>Description: Triggers a heap dump</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.builtins.HeapDumpBuiltIn</code></p>
 */

public class HeapDumpBuiltIn extends AbstractBuiltIn {

	/** The HotSpot diagnostic MBean */
	public static final ObjectName HOTSPOT_MBEAN = on("com.sun.management:type=HotSpotDiagnostic");
	/** The heap dump op */
	public static final String OP = "heapDump";
	/** The heap dump sig */
	private final String[] SIG = {String.class.getName(), boolean.class.getName()};
	

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.utils.jmx.builtins.AbstractBuiltIn#doExecute(javax.management.MBeanServerConnection, java.lang.String[])
	 */
	@Override
	protected Object doExecute(final MBeanServerConnection conn, final String... args) throws Exception {
		if(args.length<2) {
			conn.invoke(HOTSPOT_MBEAN, OP, new Object[]{args[0], true}, SIG);
		} else {
			conn.invoke(HOTSPOT_MBEAN, OP, new Object[]{args[0], Boolean.parseBoolean(args[1])}, SIG);
		}
		return "success";
	}

}
