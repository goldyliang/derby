/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.catalog
   (C) Copyright IBM Corp. 1997, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.catalog;

/**
	
  * A Dependable is an in-memory representation of an object managed
  *	by the Dependency System.
  * 
  * There are two kinds of Dependables:
  * Providers and Dependents. Dependents depend on Providers and
  *	are responsible for executing compensating logic when their
  *	Providers change.
  * <P>
  * The fields represent the known Dependables.
  * <P>
  * Persistent dependencies (those between database objects) are
  * stored in SYS.SYSDEPENDS.
  *
  * @see org.apache.derby.catalog.DependableFinder
  */
public interface Dependable
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1997_2004;
	/*
	  *	Universe of known Dependables. 
	  */

	public static final String ALIAS						= "Alias";
	public static final String CONGLOMERATE					= "Conglomerate";
	public static final String CONSTRAINT					= "Constraint";
	public static final String DEFAULT						= "Default";
	public static final String HEAP							= "Heap";
	public static final String INDEX						= "Index";
	public static final String PREPARED_STATEMENT 			= "PreparedStatement";
	public static final String FILE                         = "File";
	public static final String STORED_PREPARED_STATEMENT	= "StoredPreparedStatement";
	public static final String TABLE						= "Table";
	public static final String COLUMNS_IN_TABLE				= "ColumnsInTable";
	public static final String TRIGGER						= "Trigger";
	public static final String VIEW							= "View";
	public static final String SCHEMA						= "Schema";


	/**
	  *	Get an object which can be written to disk and which,
	  *	when read from disk, will find or reconstruct this in-memory
	  * Dependable.
	  *
	  *	@return		A Finder object that can be written to disk if this is a
	  *					Persistent Dependable.
	  *				Null if this is not a persistent dependable.
	  */
	public	DependableFinder	getDependableFinder();


	/**
	  *	Get the name of this Dependable OBJECT. This is useful
	  *	for diagnostic messages.
	  *
	  *	@return	Name of Dependable OBJECT.
	  */
	public	String	getObjectName();


	/**
	  *	Get the UUID of this Dependable OBJECT.
	  *
	  *	@return	UUID of this OBJECT.
	  */
	public	UUID	getObjectID();


	/**
	  *	Return whether or not this Dependable is persistent. Persistent
	  *	dependencies are stored in SYS.SYSDEPENDS.
	  *
	  *	@return	true if this Dependable is persistent.
	  */
	public	boolean	isPersistent();


	/**
	  * Get the unique class id for the Dependable.
	  *	Every Dependable belongs to a class of Dependables. 
	  *
	  *	@return	type of this Dependable.
	  */
	public	String	getClassType();
}
