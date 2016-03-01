package MyPackage;

import javax.management.*;

public class MyCommand {
	
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