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
package com.heliosapm.utils.jmx.protocol.local;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXAddressable;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

/**
 * <p>Title: LocalJMXConnector</p>
 * <p>Description: A JMXConnector implementation that support attaching to a local MBeanServer via a standard JMXConnector and JMXServiceURL</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.protocol.local.LocalJMXConnector</code></p>
 */

public class LocalJMXConnector implements JMXConnector, JMXAddressable {
	/** The MBeanServer the connector connects to */
	protected MBeanServer mbeanServer = null;
	/** The provided JMXServiceURL */
	protected JMXServiceURL localURL = null;
	/** The JMXServiceURL provided default domain name */
	protected String domain = null;
	/** The faux connection ID assigned to this imaginary connection */
	protected String connectionId = null;
	/** A connection ID serial number generator */
	protected static final AtomicLong serial = new AtomicLong(0);
	
	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#connect()
	 */
	@Override
	public void connect() throws IOException {
		String urlPath = localURL.getURLPath().trim();
		if(urlPath.startsWith("/")) {
			urlPath = urlPath.substring(1);			
		}
		domain = urlPath;
		if(domain==null || domain.trim().isEmpty()) {
			//throw new IOException("Invalid domain. Was empty or null", new Throwable());
			domain = "DefaultDomain";
		}
		domain = domain.trim();
		mbeanServer = getLocalMBeanServer(domain, false);
		connectionId = "local:" + domain + ":" + serial.incrementAndGet();
	}

	/**
	 * Returns an MBeanConnection for an in-vm MBeanServer that has the specified default domain.
	 * @param domain The default domain of the requested MBeanServer.
	 * @param returnNullIfNotFound If true, returns a null if a matching MBeanServer cannot be found. Otherwise, throws a RuntimeException. 
	 * @return The located MBeanServerConnection or null if one cannot be found and returnNullIfNotFound is true. 
	 */
	public static MBeanServer getLocalMBeanServer(String domain, boolean returnNullIfNotFound) {
		if(domain==null || domain.equals("") || domain.equalsIgnoreCase("DefaultDomain") || domain.equalsIgnoreCase("Default")) {
			return ManagementFactory.getPlatformMBeanServer();
		}
		List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
		for(MBeanServer server: servers) {
			if(server.getDefaultDomain().equals(domain)) return server;
		}
		if(returnNullIfNotFound) {
			return null;
		}
		throw new RuntimeException("No MBeanServer located for domain [" + domain + "]");
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
	 * @see javax.management.remote.JMXConnector#getMBeanServerConnection()
	 */
	@Override
	public MBeanServerConnection getMBeanServerConnection() throws IOException {
		return mbeanServer;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#getMBeanServerConnection(javax.security.auth.Subject)
	 */
	@Override
	public MBeanServerConnection getMBeanServerConnection(Subject delegationSubject) throws IOException {
		return mbeanServer;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#close()
	 */
	@Override
	public void close() throws IOException {
		// No Op
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#addConnectionNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	@Override
	public void addConnectionNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
		// No Op
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#removeConnectionNotificationListener(javax.management.NotificationListener)
	 */
	@Override
	public void removeConnectionNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
		// No Op
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#removeConnectionNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	@Override
	public void removeConnectionNotificationListener(NotificationListener l, NotificationFilter f, Object handback) throws ListenerNotFoundException {
		// No Op
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXConnector#getConnectionId()
	 */
	@Override
	public String getConnectionId() throws IOException {
		return connectionId;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.remote.JMXAddressable#getAddress()
	 */
	@Override
	public JMXServiceURL getAddress() {
		return localURL;
	}

}
