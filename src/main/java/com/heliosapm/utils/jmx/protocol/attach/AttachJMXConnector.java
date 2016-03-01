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
package com.heliosapm.utils.jmx.protocol.attach;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import com.heliosapm.shorthand.attach.vm.VirtualMachine;
import com.heliosapm.shorthand.attach.vm.VirtualMachineDescriptor;


/**
 * <p>Title: AttachJMXConnector</p>
 * <p>Description: JMXConnector provider for acquiring an MBeanServer connection 
 * through the <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/attach/index.html">Java Attach API</a>.</p>
 * <p>The {@link JMXServiceURL} syntax is one of the following:<ul>
 * 	<li><b><code>service:jmx:attach://&lt;PID&gt;</code></b> where the PID is the Java virtual machine ID or usually, the OS process ID.</li>
 *  <li><b><code>service:jmx:attach:///&lt;DISPLAY NAME&gt;</code></b> where the DISPLAY NAME is an expression matching the Java virtual machine's display name. or a single display name matching regex.</li>
 *  <li><b><code>service:jmx:attach:///&lt;[REGEX]&gt;</code></b> where the REGEX is a regular expression that will match one and only one JVM's display name</li>
 * </ul></p>
 * <p><b>NOTE:</b> Note that the second two examples above have <b>3</b> slashes after the <b>attach</b>.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.protocol.attach.AttachJMXConnector</code></p>
 */

public class AttachJMXConnector implements JMXConnector {
	/** The target JVM id */
	protected String jvmId = null;
	/** The target JVM display name  */
	protected String jvmDisplayName = null;
	/** The target JVM display name matching pattern */
	protected Pattern displayNamePattern = null;
	/** The JMXServiceURL sap */
	protected String urlPath = null;
	/** The attached vm */
	protected VirtualMachine vm = null;
	/** The JMXConnector to the attached JVM */
	protected JMXConnector jmxConnector = null;
	/** The attached VM's system properties */
	protected Properties vmSystemProperties = null;
	/** The attached VM's agent properties */
	protected Properties vmAgentProperties = null;
	
	/** The PID of this JVM */
	public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	/**
	 * Creates a new AttachJMXConnector
	 * @param jvmIdentifier The target JVM identifier or display name match expression
	 */
	public AttachJMXConnector(String jvmIdentifier) {
		if(jvmIdentifier==null || jvmIdentifier.trim().isEmpty()) throw new IllegalArgumentException("The passed JVM identifier was null or empty");
		urlPath = jvmIdentifier.trim();
		if(urlPath.startsWith("/")) urlPath = new StringBuilder(urlPath).deleteCharAt(0).toString();
		if(isNumber(urlPath)) {
			jvmId = urlPath;
		} else {			 
			if(urlPath.startsWith("[") && urlPath.endsWith("]")) {
				StringBuilder b = new StringBuilder(urlPath);
				b.deleteCharAt(0);
				b.deleteCharAt(b.length()-1);
				displayNamePattern = Pattern.compile(b.toString());
			} else {
				jvmDisplayName = urlPath;
			}
		}		
	}
	
	
	/**
	 * Returns a list of JVMs that can be attached to
	 * @return a list of JVMs that can be attached to
	 */
	protected String getAvailableJVMs() {
		StringBuilder b = new StringBuilder("\n");
		for(VirtualMachineDescriptor vmd :VirtualMachineDescriptor.getVirtualMachineDescriptors()) {
			b.append("\n\t").append(vmd.id()).append(" : ").append(vmd.displayName());
		}
		return b.toString();
	}
	/**
	 * Connects to the target virtual machine
	 */
	protected void attach() {
		if(jvmId!=null) {
			vm = VirtualMachine.attach(jvmId);
			return;
		}
		List<VirtualMachineDescriptor> machines = VirtualMachine.list();
		for(VirtualMachineDescriptor vmd: machines) {
			if(PID.equals(vmd.id())) {
//				System.err.println("Skipped PID:" + vmd.id() + " with display [" + vmd.displayName() + "]");
				continue;  // this avoids connecting to self
			}
			String displayName = vmd.displayName();
			if(jvmDisplayName!=null) {
				if(jvmDisplayName.equals(displayName)) {
//					System.err.println("Exact Match: Attaching to JVM:" + vmd.id() + " with display [" + vmd.displayName() + "]");
					vm = VirtualMachine.attach(vmd.id());
					return;
				}
			} else {
				Matcher m = displayNamePattern.matcher(displayName);
				if(m.matches()) {
//					System.err.println("Pattern Match: Attaching to JVM:" + vmd.id() + " with display [" + vmd.displayName() + "]");
					vm = VirtualMachine.attach(vmd.id());
					return;
				}
			}
		}
		throw new RuntimeException("Failed to find any matching JVMs for [" + urlPath + "]. Available JVMs to connect to are:" + getAvailableJVMs());
	}
	
	
	/**
	 * Determines if the passed value is a number in which case it can be assumed the JVM identifier is the PID
	 * @param value The jvm identifier to test
	 * @return true for a number, false otherwise
	 */
	protected static boolean isNumber(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch (Exception ex) {
			return false;
		}
		
	}
	

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#connect()
	 */
	@Override
	public void connect() throws IOException {
		attach();
		jmxConnector = vm.getJMXConnector();
		jvmId = vm.id();
		vmSystemProperties = vm.getSystemProperties();
		vmAgentProperties = vm.getAgentProperties();
		try { vm.detach(); } catch (Exception x) {/* No Op */}		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#connect(java.util.Map)
	 */
	@Override
	public void connect(Map<String, ?> env) throws IOException {
		connect();
	}


	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#getConnectionId()
	 */
	@Override
	public String getConnectionId() throws IOException {		
		return String.format("[Attached:%s] %s", jvmId, jmxConnector.getConnectionId());
	}

	public MBeanServerConnection getMBeanServerConnection() throws IOException {
		return jmxConnector.getMBeanServerConnection();
	}

	public MBeanServerConnection getMBeanServerConnection(
			Subject delegationSubject) throws IOException {
		return jmxConnector.getMBeanServerConnection(delegationSubject);
	}

	public void close() throws IOException {
		jmxConnector.close();
	}

	public void addConnectionNotificationListener(
			NotificationListener listener, NotificationFilter filter,
			Object handback) {
		jmxConnector.addConnectionNotificationListener(listener, filter,
				handback);
	}

	public void removeConnectionNotificationListener(
			NotificationListener listener) throws ListenerNotFoundException {
		jmxConnector.removeConnectionNotificationListener(listener);
	}

	public void removeConnectionNotificationListener(NotificationListener l,
			NotificationFilter f, Object handback)
			throws ListenerNotFoundException {
		jmxConnector.removeConnectionNotificationListener(l, f, handback);
	}

	public Properties getVmSystemProperties() {
		return vmSystemProperties;
	}

	public Properties getVmAgentProperties() {
		return vmAgentProperties;
	}

}
