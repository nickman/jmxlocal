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

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * <p>Title: GCBuiltIn</p>
 * <p>Description: Triggers GC or queries GC stats</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.builtins.GCBuiltIn</code></p>
 */

public class GCBuiltIn extends AbstractBuiltIn {
	/** GC MXBean ObjectName filter */
	public static final ObjectName GC_PATTERN = on(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*");
	/** The Memory MXBean object name to call gc() on */
	public static final ObjectName MEM_MXBEAN = on("java.lang:type=Memory");
	/** The GC Attribute Names */
	private static final String[] ATTR_NAMES = {"CollectionCount", "CollectionTime"};
	/**
	 * Creates a new GCBuiltIn
	 */
	public GCBuiltIn() {

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.utils.jmx.builtins.AbstractBuiltIn#doExecute(javax.management.MBeanServerConnection, java.lang.String[])
	 */
	@Override
	protected Object doExecute(final MBeanServerConnection conn, final String... args) throws Exception {
		if(args==null || args.length==0) {
			return getGCStats(conn);			
		} else {
			if(!"-invoke".equals(args[0])) {
				throw new IllegalArgumentException("Unrecognized parameter: [" + args[0] + "]");
			}
			conn.invoke(MEM_MXBEAN, "gc", EMPTY_PARAMS, EMPTY_SIG);
		}
		return null;
	}
	
	protected String getGCStats(final MBeanServerConnection conn) throws Exception {
		
		final StringBuilder b = new StringBuilder();
		final Set<ObjectName> gcMXBeans = conn.queryNames(GC_PATTERN, null);
		
		for(ObjectName on: gcMXBeans) {
			final String name = on.getKeyProperty("name");
			final AttributeList attrs = conn.getAttributes(on, ATTR_NAMES);
			for(Attribute a: attrs.asList()) {
				b.append("\t[").append(name).append("] ").append(a.getName().replace("Collection",  "")).append(":").append(a.getValue()).append("\n");
			}
		}
		return b.toString();
		
	}

}
