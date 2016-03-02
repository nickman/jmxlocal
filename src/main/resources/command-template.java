package com.heliosapm.utils.jmx.builtins.dynamic;

import javax.management.*;

public class Dynamic_###id### {
	
	public static Object execute(final MBeanServerConnection conn) throws Exception {
		return ###c###;
	}
	
	public static ObjectName on(final String name) {
		try {
			return new ObjectName(name);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to build ObjectName from [" + name + "]", ex);
		}
	}
	
}