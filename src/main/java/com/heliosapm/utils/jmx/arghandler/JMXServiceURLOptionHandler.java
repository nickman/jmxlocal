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
package com.heliosapm.utils.jmx.arghandler;

import java.io.File;

import javax.management.remote.JMXServiceURL;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

/**
 * <p>Title: JMXServiceURLOptionHandler</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.arghandler.JMXServiceURLOptionHandler</code></p>
 */

public class JMXServiceURLOptionHandler extends OneArgumentOptionHandler<JMXServiceURL> {
	final CmdLineParser parser;
    public JMXServiceURLOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super JMXServiceURL> setter) {
        super(parser, option, setter);
        this.parser = parser;
    }

    @Override
    protected JMXServiceURL parse(final String argument) throws CmdLineException {
    	try {
    		return new JMXServiceURL(argument);
    	} catch (Exception ex) {
    		throw new CmdLineException(parser, "Invalid JMXServiceURL [" + argument + "]: " + ex, ex);
    	}
    }

    @Override
    public String getDefaultMetaVariable() {
        return "JMXServiceURL";
    }
}
