# jmxlocal
A set of JMXConnector implementations to provide standard remoting connectivity to in VM MBeanServers and local JVMs

## Intro
Sometimes, you want to be able to reference an MBeanServer using a standard JMXServiceURL syntax. For remoting, this works, but sometimes you may want to reference a local in-VM MBeanServer, or connect transparently to an MBeanServer in another JVM on the same host for which you don't have a full RMI/IIOP/JMXMP/WS JMXSevriceURL. Or possibly you don't have the remoting server installed and configured on the target JVM. At base, **jmxlocal** allows you to transparently connect to local MBeanServers using a JMXServiceURL, just like any other remoting URL.

## Protocols
 * local: Connects to an in-vm MBeanServer. By default, this will be the JVM's management MBeanServer (i.e. the one returned by java.lang.management.ManagementFactory.getPlatformMBeanServer(), but provides an option to connect to other MBeanServers in the same JVM.
 * attach: Uses the Attach API to connect to MBeanServers in other JVMs on the same host by **PID**, display name or a regex that matches the display name of the target JVM.
 
## Examples:

* Connect to the default in-vm MBeanServer:  **service:jmx:local:///**
* Connect to a different in-vm MBeanServer using the domain:  **service:jmx:local:///jboss**
* Connect to a local JVM by pid: **service:jmx:attach:///23474**
* Connect to a local JVM by display name regex: **service:jmx:attach:///[GroovyStarter.*]**
