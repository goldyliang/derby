/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.sql.compile
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.Optimizable;

import org.apache.derby.iapi.services.sanity.SanityManager;

import java.util.Vector;

class ColumnOrdering { 

	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;

	/* See RowOrdering for possible values */
	int	myDirection;

	/* A vector of column numbers (Integers) */
	Vector columns = new Vector();

	/*
	** A vector of table numbers (Integers), corresponding to the column
	** vector by position.
	*/
	Vector tables = new Vector();

	/**
	 * @param direction	See RowOrdering for possible values
	 */
	ColumnOrdering(int direction) {
		myDirection = direction;
	}

	/**
	 * Does this ColumnOrdering contain the given column in the given table
	 * in the right direction?
	 *
	 * @param direction		See RowOrdering for possible values
	 * @param tableNumber	The number of the table in question
	 * @param columnNumber	The column number in the table (one-based)
	 *
	 * @return	true if the column is found here in the right direction
	 */
	boolean ordered(int direction, int tableNumber, int columnNumber) {
		/*
		** Check the direction only if the direction isn't DONTCARE
		*/
		if (direction != RowOrdering.DONTCARE) {
			if (direction != myDirection)
				return false;
		}

		/* The direction matches - see if the column is in this ordering */
		return contains(tableNumber, columnNumber);
	}

	/**
	 * Does this ColumnOrdering contain the given column?
	 *
	 * @param tableNumber	The number of table in question
	 * @param columnNumber	The column number in the table (one-based)
	 *
	 * @return	true if the column is found here in the right direction
	 */
	boolean contains(int tableNumber, int columnNumber)
	{
		for (int i = 0; i < columns.size(); i++) {
			Integer col = (Integer) columns.elementAt(i);
			Integer tab = (Integer) tables.elementAt(i);

			if (tab.intValue() == tableNumber &&
				col.intValue() == columnNumber) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Get the direction of this ColumnOrdering
	 */
	int direction()
	{
		return myDirection;
	}

	/**
	 * Add a column in a table to this ColumnOrdering
	 *
	 * @param tableNumber	The number of table in question
	 * @param columnNumber	The column number in the table (one-based)
	 */
	void addColumn(int tableNumber, int columnNumber)
	{
		tables.addElement(new Integer(tableNumber));
		columns.addElement(new Integer(columnNumber));
	}

	/**
	 * Remove all columns with the given table number
	 */
	void removeColumns(int tableNumber)
	{
		/*
		** Walk the list backwards, so we can remove elements
		** by position.
		*/
		for (int i = tables.size() - 1; i >= 0; i--)
		{
			Integer tab = (Integer) tables.elementAt(i);
			if (tab.intValue() == tableNumber)
			{
				tables.removeElementAt(i);
				columns.removeElementAt(i);
			}
		}
	}

	/**
	 * Tell whether this ColumnOrdering has no elements.
	 */
	boolean empty()
	{
		return (tables.size() == 0);
	}

	/** Return a clone of this ColumnOrdering */
	ColumnOrdering cloneMe() {
		ColumnOrdering retval = new ColumnOrdering(myDirection);

		for (int i = 0; i < columns.size(); i++) {
			/* Integers are immutable, so just copy the pointers */
			retval.columns.addElement(columns.elementAt(i));
			retval.tables.addElement(tables.elementAt(i));
		}

		return retval;
	}

	/** Is the given table number in this ColumnOrdering? */
	boolean hasTable(int tableNumber) {
		if (tables.size() == 0)
			return false;

		for (int i = 0; i < tables.size(); i++) {
			Integer tab = (Integer) tables.elementAt(i);
			
			if (tab.intValue() == tableNumber)
				return true;
		}

		return false;
	}

	/** Is there any table other than the given one in this ColumnOrdering? */
	boolean hasAnyOtherTable(int tableNumber) {
		if (tables.size() == 0)
			return false;

		for (int i = 0; i < tables.size(); i++) {
			Integer tab = (Integer) tables.elementAt(i);
			
			if (tab.intValue() != tableNumber)
				return true;
		}

		return false;
	}

	public String toString() {
		String retval = "";

		if (SanityManager.DEBUG) {
			retval += "Direction: " + myDirection;

			for (int i = 0; i < columns.size(); i++) {
				retval += " Table " + tables.elementAt(i) +
							", Column " + columns.elementAt(i);
			}
		}

		return retval;
	}
}
