/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.services.bytecode
   (C) Copyright IBM Corp. 1998, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.impl.services.bytecode;

import java.lang.reflect.*;
import org.apache.derby.iapi.services.classfile.VMOpcode;
import org.apache.derby.iapi.services.sanity.SanityManager;
import java.util.Hashtable;
import org.apache.derby.iapi.services.loader.*;
import org.apache.derby.iapi.services.context.*;

/**
 * Validate BC calls.
 *
 * @author jamie
 */
class d_BCValidate
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1998_2004;

	private static final String[] csPackages = {
		"java",
		"org.apache.derby.exe.",
		"org.apache.derby.iapi.",
		"org.apache.derby.jdbc.",
		"org.apache.derby.iapi.",
		"org.apache.derby.impl.",
		"org.apache.derby.authentication.",
		"org.apache.derby.catalog.",
		"org.apache.derby.iapi.db.",
		"org.apache.derby.iapi.types.",
		"org.apache.derby.iapi.types.",
		"org.apache.derby.catalog.types.",
		};


	private static final Class[] NO_PARAMS = new Class[0];

	static void checkMethod(short opcode, Type dt, String methodName, String[] debugParameterTypes, Type rt) {


		if (SanityManager.DEBUG) {
			String reason;
			try {

				String declaringClass = dt.javaName();
				if (declaringClass.startsWith("org.apache.derby.exe."))
					return;

				// only validate against Cloudscape engine or Java classes. Not user defined classes
				int p;
				for (p = 0; p < csPackages.length; p++) {
					if (declaringClass.startsWith(csPackages[p]))
						break;
				}
				if (p == csPackages.length)
					return;

				Class[] params = NO_PARAMS;

				Class declaring = loadClass(declaringClass);

				if (debugParameterTypes != null) {
					params = new Class[debugParameterTypes.length];
					for (int i = 0; i < debugParameterTypes.length; i++) {
						params[i] = loadClass(debugParameterTypes[i]);
					}

				}

				String actualReturnType;

				if (methodName.equals("<init>")) {
					Constructor c = declaring.getDeclaredConstructor(params);
					actualReturnType = "void";
				} else {
					Method m = declaring.getDeclaredMethod(methodName, params);
					actualReturnType = m.getReturnType().getName();
				}

				Class requestedReturnType = loadClass(rt.javaName());

				// check the return type
				if (actualReturnType.equals(requestedReturnType.getName())) {

					// check the inteface match
					if (opcode != VMOpcode.INVOKEINTERFACE)
						return;

					if (declaring.isInterface())
						return;

					reason = "declaring class is not an interface";

				} else {
					reason = "return type is " + actualReturnType;
				}


			} catch (Exception e) {
				reason = e.toString();
				e.printStackTrace(System.out);
			}

			String sig = dt.javaName() + " >> " + rt.javaName() + " " + methodName + "(";
			if (debugParameterTypes != null) {
				for (int i = 0; i < debugParameterTypes.length; i++) {
					if (i != 0)
						sig = sig + ", ";
					sig = sig + debugParameterTypes[i];
				}
			}
			sig = sig + ")";

			String msg = "Invalid method " + sig + " because " + reason;

			System.out.println(msg);
			SanityManager.THROWASSERT(msg);
		}
	}

	private static Hashtable primitives;

	static {
		if (SanityManager.DEBUG) {
			primitives = new Hashtable();
			primitives.put("boolean", Boolean.TYPE);
			primitives.put("byte", Byte.TYPE);
			primitives.put("char", Character.TYPE);
			primitives.put("double", Double.TYPE);
			primitives.put("float", Float.TYPE);
			primitives.put("int", Integer.TYPE);
			primitives.put("long", Long.TYPE);
			primitives.put("short", Short.TYPE);
			primitives.put("void", Void.TYPE);
		}

	}
	

	private static Class loadClass(String name) throws ClassNotFoundException {

		if (SanityManager.DEBUG) {

			Class c = (Class) primitives.get(name);
			if (c != null)
				return c;

			if (name.endsWith("[]")) {
				Class baseClass = loadClass(name.substring(0, name.length() - 2));
				return Array.newInstance(baseClass, 0).getClass();
			}
			
			return Class.forName(name);
		}

		return null;
	}
}
