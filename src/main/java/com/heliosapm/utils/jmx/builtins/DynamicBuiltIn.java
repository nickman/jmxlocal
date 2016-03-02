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
package com.heliosapm.utils.jmx.builtins;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServerConnection;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaFileObject.Kind;

import com.heliosapm.shorthand.attach.vm.VirtualMachineBootstrap;
import com.heliosapm.utils.jmx.Command;




/**
 * <p>Title: DynamicBuiltIn</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.utils.jmx.builtins.DynamicBuiltIn</code></p>
 */

public class DynamicBuiltIn extends AbstractBuiltIn {
	private static final AtomicLong serial = new AtomicLong();
	
	public static final String PACKAGE_NAME = "com.heliosapm.utils.jmx.builtins.dynamic";
	public static final String SIMPLE_CLASS_NAME = "Dynamic_%s";
	public static final String CLASS_NAME = PACKAGE_NAME + "." + SIMPLE_CLASS_NAME; 
	/**
	 * Creates a new DynamicBuiltIn
	 */
	public DynamicBuiltIn() {

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.utils.jmx.builtins.AbstractBuiltIn#doExecute(javax.management.MBeanServerConnection, java.lang.String[])
	 */
	@Override
	protected Object doExecute(final MBeanServerConnection conn, final String... args) throws Exception {
		if(args==null || args.length==0) throw new IllegalArgumentException("No code supplied");
		final long id = serial.incrementAndGet();
		String code = null;
		File f = new File(args[0]);
		if(f.exists()) {
			// full class file
			code = getFileText(f.getAbsolutePath());
		} else {
			// in line java 
			code = buildCode(args[0], id);
		}
		try {			
			final ClassLoader cl = VirtualMachineBootstrap.getToolsClassLoader();
			final ClassLoader current = Thread.currentThread().getContextClassLoader();
			Class<?> clazz = null;
			try {
				Thread.currentThread().setContextClassLoader(cl);
				
//				System.out.println("\n" + code + "\n");
				clazz = compile(code, String.format(CLASS_NAME, id));
				Method m = clazz.getDeclaredMethod("execute", MBeanServerConnection.class);
				return m.invoke(null, conn);
			} finally {
				Thread.currentThread().setContextClassLoader(current);
				File delFile = new File(String.format(SIMPLE_CLASS_NAME, id) + ".class");
				if(delFile.exists()) {
					if(!delFile.delete()) {
						delFile.deleteOnExit();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			System.exit(-1);
		}

		return null;
	}
	
	public static String buildCode(final String command, final long id) throws Exception {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String template = null;
		try {
			URL url = Command.class.getClassLoader().getResource("command-template.java");
			if(url==null) throw new Exception("Failed to read template");
			is = url.openStream();
			if(is==null) throw new Exception("Failed to read template");
			isr = new InputStreamReader(is, Charset.forName("UTF-8"));
			br = new BufferedReader(isr);
			StringBuilder b = new StringBuilder();
			String line = null;
			while((line = br.readLine())!=null) {
				b.append(line).append("\n");
			}
			template = b.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read template", ex);
		} finally {
			if(br!=null) try { br.close(); } catch (Exception x) {/* No Op */}
			if(isr!=null) try { isr.close(); } catch (Exception x) {/* No Op */}
			if(is!=null) try { is.close(); } catch (Exception x) {/* No Op */}
		}
		return template.replace("###c###", command).replace("###id###", "" + id);	
	}

	public static Class<?> compile(final String code, final String className) throws Exception {
		StandardJavaFileManager fileManager = null;
		try {
			final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if(compiler==null) throw new Exception("No compiler available. Suggestion: Use JDK if you are using JRE.");
			final Iterable<? extends JavaFileObject> compilationUnits  = getJavaSourceFromString(code, className);
			
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
			
			final DefiningClassLoader cc = new DefiningClassLoader(compiler.getClass().getClassLoader());
			return cc.gitEm(className, jfo[0].openInputStream());
			
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

	static Iterable<JavaSourceFromString> getJavaSourceFromString(String code, final String className) {
		final JavaSourceFromString jsfs;
		jsfs = new JavaSourceFromString(className, code);		
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
	


