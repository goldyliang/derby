/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.iapi.services.loader
   (C) Copyright IBM Corp. 1998, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.iapi.services.loader;

import org.apache.derby.iapi.error.StandardException;

import org.apache.derby.iapi.util.ByteArray;

import java.io.ObjectStreamClass;


/**
	A class factory module to handle application classes
	and generated classes.
*/

public interface ClassFactory { 

	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1998_2004;

	/**
		Add a generated class to the class manager's class repository.

		@exception 	StandardException	Standard Cloudscape error policy

	*/
	public GeneratedClass loadGeneratedClass(String fullyQualifiedName, ByteArray classDump)
		throws StandardException;

	/**
		Return a ClassInspector object
	*/
	public ClassInspector	getClassInspector();

	/**
		Load an application class, or a class that is potentially an application class.

		@exception ClassNotFoundException Class cannot be found
	*/
	public Class loadApplicationClass(String className)
		throws ClassNotFoundException;

	/**
		Load an application class, or a class that is potentially an application class.

		@exception ClassNotFoundException Class cannot be found
	*/
	public Class loadApplicationClass(ObjectStreamClass classDescriptor)
		throws ClassNotFoundException;

	/**
		Was the passed in class loaded by a ClassManager.

		@return true if the class was loaded by a Cloudscape class manager,
		false it is was loaded by the system class loader, or another class loader.
	*/
	public boolean isApplicationClass(Class theClass);

	/**
		Notify the class manager that a jar file has been modified.
		@param reload Restart any attached class loader

		@exception StandardException thrown on error
	*/
	public void notifyModifyJar(boolean reload) throws StandardException ;

	/**
		Notify the class manager that the classpath has been modified.

		@exception StandardException thrown on error
	*/
	public void notifyModifyClasspath(String classpath) throws StandardException ;

	/**
		Return the in-memory "version" of the class manager. The version
		is bumped everytime the classes are re-loaded.
	*/
	public int getClassLoaderVersion();
}
