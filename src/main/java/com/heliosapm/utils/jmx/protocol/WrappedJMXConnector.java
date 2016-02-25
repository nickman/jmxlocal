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
package com.heliosapm.utils.jmx.protocol;

import java.io.IOException;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXAddressable;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;


/**
 * <p>Title: WrappedJMXConnector</p>
 * <p>Description: A wrapper for JMXConnectors that provides an implementation of {@link JMXAddressable} if a connector does not support it natively</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.protocol.WrappedJMXConnector</code></p>
 */

public class WrappedJMXConnector implements JMXConnector, JMXAddressable {
	/** The wrapped JMXConnector */
	final JMXConnector jmxConnector; 
	/** The connector's known JMXServiceURL */
	final JMXServiceURL jmxServiceURL;
	
	
	/**
	 * Returns an addressable connector. If the passed connector implements {@link JMXAddressable}, it is simple returned.
	 * Otherwise it is wrapped and the {@link JMXAddressable} interface is implemented using the passed service URL.
	 * @param jmxConnector The JMXConnector to wrap
	 * @param jmxServiceURL The connector's known JMXServiceURL
	 * @return a {@link JMXAddressable} implementing JMXConnector
	 */
	public static JMXConnector addressable(final JMXConnector jmxConnector, final JMXServiceURL jmxServiceURL) {
		if(jmxConnector==null) throw new IllegalArgumentException("The passed JMXConnector was null");
		if(jmxConnector instanceof JMXAddressable) return jmxConnector;
		if(jmxServiceURL==null) throw new IllegalArgumentException("The passed JMXConnector does not implement JMXAddressable, but the passed JMXServiceURL was null");
		return new WrappedJMXConnector(jmxConnector, jmxServiceURL);
	}
	
	
	/**
	 * Creates a new WrappedJMXConnector
	 * @param jmxConnector The JMXConnector to wrap
	 * @param jmxServiceURL The connector's known JMXServiceURL
	 */
	private WrappedJMXConnector(final JMXConnector jmxConnector, final JMXServiceURL jmxServiceURL) {		
		this.jmxConnector = jmxConnector;
		this.jmxServiceURL = jmxServiceURL;
	}


	/**
	 * @throws IOException
	 * @see javax.management.remote.JMXConnector#connect()
	 */
	public void connect() throws IOException {
		jmxConnector.connect();
	}


	/**
	 * @param env
	 * @throws IOException
	 * @see javax.management.remote.JMXConnector#connect(java.util.Map)
	 */
	public void connect(Map<String, ?> env) throws IOException {
		jmxConnector.connect(env);
	}


	/**
	 * Returns the JMXConnector's {@link MBeanServerConnection} 
	 * @return the JMXConnector's {@link MBeanServerConnection}
	 * @throws IOException
	 * @see javax.management.remote.JMXConnector#getMBeanServerConnection()
	 */
	public MBeanServerConnection getMBeanServerConnection() throws IOException {
		return jmxConnector.getMBeanServerConnection();
	}


	/**
	 * Returns the JMXConnector's {@link MBeanServerConnection} using the passed {@link Subject} for auth.
	 * @param delegationSubject The authentication/authorization Subject
	 * @return the MBeanServerConnection
	 * @throws IOException
	 * @see javax.management.remote.JMXConnector#getMBeanServerConnection(javax.security.auth.Subject)
	 */
	public MBeanServerConnection getMBeanServerConnection(final Subject delegationSubject) throws IOException {
		return jmxConnector.getMBeanServerConnection(delegationSubject);
	}


	/**
	 * @throws IOException
	 * @see javax.management.remote.JMXConnector#close()
	 */
	public void close() throws IOException {
		jmxConnector.close();
	}


	/**
	 * @param listener
	 * @param filter
	 * @param handback
	 * @see javax.management.remote.JMXConnector#addConnectionNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void addConnectionNotificationListener(
			NotificationListener listener, NotificationFilter filter,
			Object handback) {
		jmxConnector.addConnectionNotificationListener(listener, filter,
				handback);
	}


	/**
	 * @param listener
	 * @throws ListenerNotFoundException
	 * @see javax.management.remote.JMXConnector#removeConnectionNotificationListener(javax.management.NotificationListener)
	 */
	public void removeConnectionNotificationListener(
			NotificationListener listener) throws ListenerNotFoundException {
		jmxConnector.removeConnectionNotificationListener(listener);
	}


	/**
	 * @param l
	 * @param f
	 * @param handback
	 * @throws ListenerNotFoundException
	 * @see javax.management.remote.JMXConnector#removeConnectionNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void removeConnectionNotificationListener(NotificationListener l,
			NotificationFilter f, Object handback)
			throws ListenerNotFoundException {
		jmxConnector.removeConnectionNotificationListener(l, f, handback);
	}


	/**
	 * Returns the JMXConnector's Connection ID
	 * @return the JMXConnector's Connection ID
	 * @throws IOException
	 * @see javax.management.remote.JMXConnector#getConnectionId()
	 */
	public String getConnectionId() throws IOException {
		return jmxConnector.getConnectionId();
	}


	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXAddressable#getAddress()
	 */
	@Override
	public JMXServiceURL getAddress() {
		return jmxServiceURL;
	}

}
