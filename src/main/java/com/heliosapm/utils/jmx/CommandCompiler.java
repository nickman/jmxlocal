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
package com.heliosapm.utils.jmx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * <p>Title: CommandCompiler</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.CommandCompiler</code></p>
 */

public class CommandCompiler {
	
	public static Class<?> compile(final String code) throws Exception {
		StandardJavaFileManager fileManager = null;
		try {
			final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if(compiler==null) throw new Exception("No compiler available. Suggestion: Use JDK if you are using JRE.");
			final Iterable<? extends JavaFileObject> compilationUnits  = getJavaSourceFromString(code);
			
			final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			
			fileManager = compiler.getStandardFileManager(diagnostics, null, Charset.forName("UTF8"));
			final JavaFileManager.Location[] loc = new JavaFileManager.Location[1];
			final JavaFileObject[] jfo = new JavaFileObject[1];
			JavaFileManager fFileManager = new ForwardingJavaFileManager<JavaFileManager>(fileManager) {
		        @Override
		        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind, FileObject sibling) throws IOException {
		        	loc[0] = location;
		        	jfo[0] = super.getJavaFileForOutput(location, className, kind, sibling);		        	
		        	return jfo[0];
		        }		           
		       };							    
			final StringWriter sw = new StringWriter();
			final boolean success = compiler.getTask(sw, fFileManager, diagnostics, null, null, compilationUnits).call();
			System.out.println(sw.toString());
			if(!success) throw new Exception("Failed to compile code:" + diagnostics.getDiagnostics() + "\n" + sw.toString());
			
			final DefiningClassLoader cc = new DefiningClassLoader(CommandCompiler.class.getClassLoader());
			new File("MyCommand.class").deleteOnExit();
			return cc.gitEm("MyPackage.MyCommand", jfo[0].openInputStream());
			
		} finally {
			if(fileManager!=null) try { fileManager.close(); } catch (Exception x) {/* No Op */}
		}
	}
	
	public static class DefiningClassLoader extends ClassLoader {
		
		public DefiningClassLoader(final ClassLoader parent) {
			super(parent);
		}
		
		public Class<?> gitEm(final String name, final InputStream is) {
			final byte[] buff = new byte[1024];
			final ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
			int bytesRead = -1;
			try {
				while((bytesRead = is.read(buff))!=-1) {
					baos.write(buff, 0, bytesRead);
				}
				byte[] byteCode = baos.toByteArray();
				return super.defineClass(name, byteCode, 0, byteCode.length);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		
	}

	static Iterable<JavaSourceFromString> getJavaSourceFromString(String code) {
		final JavaSourceFromString jsfs;
		jsfs = new JavaSourceFromString("MyPackage.MyCommand", code);		
		return new Iterable<JavaSourceFromString>() {
			public Iterator<JavaSourceFromString> iterator() {
				return new Iterator<JavaSourceFromString>() {
					boolean isNext = true;

					public boolean hasNext() {
						return isNext;
					}

					public JavaSourceFromString next() {
						if (!isNext)
							throw new NoSuchElementException();
						isNext = false;
						return jsfs;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}	
class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}	


