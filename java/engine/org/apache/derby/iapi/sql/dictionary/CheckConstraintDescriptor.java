/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.iapi.sql.dictionary
   (C) Copyright IBM Corp. 1998, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.iapi.sql.dictionary;
import org.apache.derby.catalog.ReferencedColumns;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.sanity.SanityManager;
import org.apache.derby.iapi.sql.StatementType;

/**
 * This class represents a check constraint descriptor.
 *
 * @author jamie
 */
public class CheckConstraintDescriptor extends ConstraintDescriptor
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1998_2004;
	ReferencedColumns	referencedColumns;
	String						constraintText;

	CheckConstraintDescriptor(
		    DataDictionary dataDictionary,
			TableDescriptor table,
			String constraintName,
			boolean deferrable,
			boolean initiallyDeferred,
			UUID constraintId,
			String constraintText,
			ReferencedColumns referencedColumns,
			SchemaDescriptor schemaDesc,
			boolean	isEnabled
			)							
	{
		super(dataDictionary, table, constraintName, deferrable,
			  initiallyDeferred, (int []) null,
			  constraintId, schemaDesc, isEnabled);
		this.constraintText = constraintText;
		this.referencedColumns = referencedColumns;
	}

	/**
	 * Does this constraint have a backing index?
	 *
	 * @return boolean	Whether or not there is a backing index for this constraint.
	 */
	public boolean hasBackingIndex()
	{
		return false;
	}

	/**
	 * Gets an identifier telling what type of descriptor it is
	 * (UNIQUE, PRIMARY KEY, FOREIGN KEY, CHECK).
	 *
	 * @return	An identifier telling what type of descriptor it is
	 *		(UNIQUE, PRIMARY KEY, FOREIGN KEY, CHECK).
	 */
	public int	getConstraintType()
	{
		return DataDictionary.CHECK_CONSTRAINT;
	}

	/**
	 * Get the text of the constraint. (Only non-null/meaningful for check
	 * constraints.)
	 * @return	The constraint text.
	 */
	public String getConstraintText()
	{
		return constraintText;
	}

	/**
	 * Get the UUID of the backing index, if one exists.
	 *
	 * @return The UUID of the backing index, if one exists, else null.
	 */
	public UUID getConglomerateId()
	{
		return null;
	}

	/**
	 * Get the ReferencedColumns.
	 *
	 * @return The ReferencedColumns.
	 */
	public ReferencedColumns getReferencedColumnsDescriptor()
	{
		return referencedColumns;
	}

	/**
	 * Set the ReferencedColumns; used in drop column
	 *
	 * @param	The new ReferencedColumns.
	 * @return	void
	 */
	public void setReferencedColumnsDescriptor(ReferencedColumns rcd)
	{
		referencedColumns = rcd;
	}

	/**
	 * Get the referenced columns as an int[] of column ids.
	 *
	 * @return The array of referenced column ids.
	 */
	public int[] getReferencedColumns()
	{
		return referencedColumns.getReferencedColumnPositions();
	}

	/**
	 * Does this constraint need to fire on this type of
	 * DML?  For a check constraint, all inserts, and
	 * appropriate updates
	 *
	 * @param dmlType	the type of DML 
	 * (StatementType.INSERT|StatementType.UPDATE|StatementType.DELETE)
	 * @param modifiedCols	the columns modified, or null for all
	 *
	 * @return true/false
	 */
	public boolean needsToFire(int stmtType, int[] modifiedCols)
	{
		/*
		** If we are disabled, we never fire
		*/
		if (!isEnabled)
		{
			return false;
		}

		if (stmtType == StatementType.INSERT)
		{
			return true;
		}

		if (stmtType == StatementType.DELETE)
		{
			return false;
		}
	
		// if update, only relevant if columns intersect
		return doColumnsIntersect(modifiedCols, getReferencedColumns());
	}

	/**
	 * Convert the CheckConstraintDescriptor to a String.
	 *
	 * @return	A String representation of this CheckConstraintDescriptor
	 */

	public String	toString()
	{
		if (SanityManager.DEBUG)
		{
			return "constraintText: " + constraintText + "\n" +
			   "referencedColumns: " + referencedColumns + "\n" +
				super.toString();
		}
		else
		{
			return "";
		}
	}


}
