/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.iapi.sql.execute
   (C) Copyright IBM Corp. 1997, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.services.loader.GeneratedClass;

import org.apache.derby.iapi.error.StandardException;

import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

import org.apache.derby.iapi.sql.PreparedStatement;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;

/**
 * Execution extends prepared statement to add methods it needs
 * for execution purposes (that should not be on the Database API).
 *
 *	@author ames
 */
public interface ExecPreparedStatement 
	extends PreparedStatement { 

	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1997_2004;

	/**
	 * set the statement text
	 *
	 * @param txt the source text
	 */
	void setSource(String txt);

	/**
	 *	Get the Execution constants. This routine is called at Execution time.
	 *
	 *	@return	ConstantAction	The big structure enclosing the Execution constants.
	 */
	ConstantAction	getConstantAction( );

	/**
	 *	Get a saved object by number.  This is called during execution to
	 *  access objects created at compile time.  These are meant to be
	 *  read-only at run time.
	 *
	 *	@return	Object	A saved object.  The caller has to know what
	 *	it is requesting and cast it back to the expected type.
	 */
	Object	getSavedObject(int objectNum);

	/**
	 *	Get all the saved objects.  Used for stored prepared
	 * 	statements.
	 *
	 *	@return	Object[]	the saved objects
	 */
	Object[]	getSavedObjects();

	/**
	 *	Get the saved cursor info.  Used for stored prepared
	 * 	statements.
	 *
	 *	@return	Object	the cursor info
	 */
	Object	getCursorInfo();

	/**
	 *  Get the class generated for this prepared statement.
	 *  Used to confirm compatability with auxilary structures.
	 *
	 * @exception StandardException on error obtaining class
	 *	(probably when a stored prepared statement is loading)
	 */
	GeneratedClass getActivationClass() throws StandardException;

	/**
	 *  Mark the statement as unusable, i.e. the system is
	 * finished with it and no one should be able to use it.
	 */
	void finish(LanguageConnectionContext lcc);

	/**
	 * Does this statement need a savpoint
	 *
	 * @return true if needs a savepoint
	 */
	boolean needsSavepoint();

	/**
	 * Get a new prepared statement that is a shallow copy
	 * of the current one.
	 *
	 * @return a new prepared statement
	 *
	 * @exception StandardException on error 
	 */
	public ExecPreparedStatement getClone() throws StandardException;

	/* Methods from old CursorPreparedStatement */

	/**
	 * the update mode of the cursor
	 *
	 * @return	The update mode of the cursor
	 */
	int	getUpdateMode();

	/**
	 * the target table of the cursor
	 *
	 * @return	target table of the cursor
	 */
	ExecCursorTableReference getTargetTable();

	/**
	 * the target columns of the cursor; this is a superset of
	 * the updatable columns, describing the row available
	 *
	 * @return	target columns of the cursor as an array of column descriptors
	 */
	ResultColumnDescriptor[]	getTargetColumns();

	/**
	 * the update columns of the cursor
	 *
	 * @return	update columns of the cursor as a string of column names
	 */
	String[]	getUpdateColumns();

	/**
	 * set this parepared statement to be valid
	 */
	void setValid();

	/**
	 * Indicate that the statement represents an SPS action
	 */
	void setSPSAction();
}

