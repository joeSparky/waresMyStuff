package com.errorLogging;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
//import java.org.apache.catalina.loader.WebappClassLoader;

public class DumpLoader {
//	public static void webAppDump() {
//		jdk.internal.loader.ClassLoaders.AppClassLoader al = new AppClassLoader();
//	}

	public static void dumpLoader() {
		System.out.println("Here we go");
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		System.out.println("class loader canonical name " + cl.getClass().getCanonicalName());
		// class loader canonical name jdk.internal.loader.ClassLoaders.AppClassLoader
		Package[] ps = cl.getDefinedPackages();
		for (Package p : ps) {
			System.out.println("package:" + p.getClass().getCanonicalName());
			System.out.println("name:" + p.getName());
			
//			package:java.lang.Package
//			name:org.junit.internal.runners.statements
//			package:java.lang.Package
//			name:org.junit.runners
//			package:java.lang.Package
//			name:org.junit.runner.notification
//			package:java.lang.Package
//			name:org.junit.internal.runners.rules
//			package:java.lang.Package
//			name:org.junit.internal.requests
//			package:java.lang.Package
//			name:org.junit.internal.runners.model
//			package:java.lang.Package
//			name:org.junit.runner.manipulation
//			package:java.lang.Package
//			name:org.junit.internal.builders
//			package:java.lang.Package
//			name:org.junit.rules
//			package:java.lang.Package
//			name:org.junit.runners.model
//			package:java.lang.Package
//			name:org.junit.internal
//			package:java.lang.Package
//			name:org.eclipse.jdt.internal.junit.runner
//			package:java.lang.Package
//			name:comTest.errorLogging
//			package:java.lang.Package
//			name:org.junit.runner
//			package:java.lang.Package
//			name:org.junit
//			package:java.lang.Package
//			name:org.hamcrest
//			package:java.lang.Package
//			name:org.junit.validator
//			package:java.lang.Package
//			name:org.junit.internal.runners
//			package:java.lang.Package
//			name:junit.framework
//			package:java.lang.Package
//			name:org.eclipse.jdt.internal.junit4.runner
//			package:java.lang.Package
//			name:com.errorLogging
			
			// while running under tomcat
//			package:java.lang.Package
//			name:org.apache.catalina.webresources.war
//			package:java.lang.Package
//			name:org.apache.catalina.startup
//			package:java.lang.Package
//			name:org.apache.catalina.security
//			package:java.lang.Package
//			name:org.apache.tomcat.util.buf
//			package:java.lang.Package
//			name:org.apache.juli.logging
//			package:java.lang.Package
//			name:org.apache.juli
			
		}

		if (cl instanceof URLClassLoader) {
			System.out.println("cl instanceof URLClassLoader");
			URLClassLoader urlClassLoader = (URLClassLoader) cl;
			URL[] urls = urlClassLoader.getURLs();

			for (URL url : urls) {
				if (url.getProtocol().equals("file") && url.getFile().endsWith(".jar")) {
					try (JarFile jarFile = new JarFile(url.getFile())) {
						System.out.println("JAR File: " + url.getFile());
						Enumeration<JarEntry> entries = jarFile.entries();
						while (entries.hasMoreElements()) {
							JarEntry entry = entries.nextElement();
							if (entry.getName().endsWith(".class")) {
								System.out.println("   Class: " + entry.getName());
							}
						}
					} catch (Exception e) {
						System.err.println("Error reading JAR: " + url.getFile());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
