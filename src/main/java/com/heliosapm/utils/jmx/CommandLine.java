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

import java.util.Arrays;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.EnumOptionHandler;
import org.kohsuke.args4j.spi.StopOptionHandler;

/**
 * <p>Title: CommandLine</p>
 * <p>Description: Command line parser bean</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.CommandLine</code></p>
 */

public class CommandLine {
	/** The JMX URL */
	@Option(name="-jmxurl", required=true, usage="The JMXServiceURL to connect with. e.g. -jmxurl service:jmx:attach///<PID>")
	public String jmxUrl = null;
	/** The optional user name */
	@Option(name="-u", depends="-p", usage="The optional JMX user name. e.g. -u <user>")
	public String user = null;
	/** The optional password */
	@Option(name="-p", depends="-u", usage="The optional JMX password. e.g. -p <password>")
	public String password = null;
	
	/** The command name and arguments */
	//@Option(name="-c", metaVar="<command name> <args>", handler=RestOfArgumentsHandler.class, usage="The command name and arguments. e.g. -c hdump /tmp/heap.dump true")
	@Argument
	@Option(name="--", metaVar="<command args>", handler=StopOptionHandler.class, usage="The command name and arguments. e.g. /tmp/heap.dump true")
	public String[] commands = null;

	/** The command name*/
	@Option(name="-c", handler=EnumOptionHandler.class,  usage="The command name and arguments. e.g. -c hdump")
	public String command = null;

	
	
	
	/**
	 * Creates a new CommandLine
	 */
	public CommandLine() {
		// TODO Auto-generated constructor stub
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
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append(password);
		builder.append(", command=");
		builder.append(command);
		builder.append(", args=");
		builder.append(Arrays.toString(commands));
		
		builder.append("]");
		return builder.toString();
	}

}
