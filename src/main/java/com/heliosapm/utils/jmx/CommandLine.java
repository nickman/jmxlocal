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
package com.heliosapm.utils.jmx;

import java.io.Closeable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StopOptionHandler;

import com.heliosapm.utils.jmx.arghandler.JMXServiceURLOptionHandler;
import com.heliosapm.utils.jmx.builtins.BuiltIn;

/**
 * <p>Title: CommandLine</p>
 * <p>Description: Command line parser bean</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.CommandLine</code></p>
 */

public class CommandLine implements Closeable {
	/** The JMX URL */
	@Option(name="-jmxurl", handler=JMXServiceURLOptionHandler.class, required=true, usage="The JMXServiceURL to connect with. e.g. -jmxurl service:jmx:attach:///<PID>")
	private JMXServiceURL jmxUrl = null;
	/** The print stack trace option */
	@Option(name="-psx", required=false, usage="If specified, any error will print a stack trace")
	private boolean psx = false;
	
	/** The optional user name */
	@Option(name="-u", depends="-p", usage="The optional JMX user name. e.g. -u <user>")
	private String user = null;
	/** The optional password */
	@Option(name="-p", depends="-u", usage="The optional JMX password. e.g. -p <password>")
	private String password = null;
	/** The command name and arguments */
	@Argument
	@Option(name="", hidden=true, metaVar="<command args>", handler=StopOptionHandler.class, usage="The command name and arguments. e.g. /tmp/heap.dump true")
	private String[] commands = null;
	/** The command name*/
	@Option(name="-c", usage="The command name and arguments. e.g. -c hdump /tmp/heap.dump true")
	private BuiltIn builtIn = null;

	private MBeanServerConnection conn = null;
	private JMXConnector jmxConnector = null;
	
	/**
	 * Creates a new CommandLine
	 */
	public CommandLine() {

	}
	
	/**
	 * Executes the configured command
	 * @return the result of the command
	 */
	public Object execute() throws Exception {
		if(jmxUrl==null) throw new IllegalStateException("The command line has not been parsed");
		connect();
		return builtIn.execute(conn, commands);
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() {
		if(jmxConnector!=null) try { jmxConnector.close(); } catch (Exception x) {/* No Op */}
	}
	
	private void connect() {
		try {
			final Map<String, Object> env = new HashMap<String, Object>();
			if(user!=null) {
				env.put(JMXConnector.CREDENTIALS, new String[]{user, password});
			}
			jmxConnector = JMXConnectorFactory.connect(jmxUrl, env);			
			conn = jmxConnector.getMBeanServerConnection();
		} catch (Exception ex) {
			if(jmxConnector!=null) try { jmxConnector.close(); } catch (Exception x) {/* No Op */}
			throw new RuntimeException("Failed to connect to [" + jmxUrl + "]", ex);
		}
	}
	
	/**
	 * Indicates if a stack trace should be printed on any exception
	 * @return true if a stack trace should be printed on any exception, false otherwise
	 */
	public boolean isPrintStackTrace() {
		return psx;
	}
	
	/**
	 * Returns the JMXServiceURL
	 * @return the jmxUrl
	 */
	public JMXServiceURL getJmxUrl() {
		return jmxUrl;
	}

	/**
	 * Returns command arguments 
	 * @return the command arguments
	 */
	public String[] getCommandArgs() {
		return commands.clone();
	}




	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommandLine [jmxUrl=");
		builder.append(jmxUrl);
		builder.append(", psx=");
		builder.append(psx);
		
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append(password);
		builder.append(", command=");
		builder.append(builtIn);
		builder.append(", args=");
		builder.append(Arrays.toString(commands));
		
		builder.append("]");
		return builder.toString();
	}

}
