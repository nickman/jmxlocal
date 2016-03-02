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

/**
 * <p>Title: IBuiltIn</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.builtins.IBuiltIn</code></p>
 */

public interface IBuiltIn {
	/**
	 * Defines a built in
	 * @param conn The MBeanServerConnection to issue the command against
	 * @param args The arguments to this built in
	 * @return the response from the built in
	 */
	public Object execute(final MBeanServerConnection conn, final String...args);
}
