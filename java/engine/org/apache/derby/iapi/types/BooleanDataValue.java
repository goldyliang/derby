/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.iapi.types
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public interface BooleanDataValue extends DataValueDescriptor
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;
	public boolean	getBoolean();

	/**
	 * The SQL AND operator.  This provides SQL semantics for AND with unknown
	 * truth values - consult any standard SQL reference for an explanation.
	 *
	 * @param otherValue	The other BooleanDataValue to AND with this one
	 *
	 * @return	this AND otherValue
	 *
	 */
	public BooleanDataValue and(BooleanDataValue otherValue);

	/**
	 * The SQL OR operator.  This provides SQL semantics for OR with unknown
	 * truth values - consult any standard SQL reference for an explanation.
	 *
	 * @param otherValue	The other BooleanDataValue to OR with this one
	 *
	 * @return	this OR otherValue
	 *
	 */
	public BooleanDataValue or(BooleanDataValue otherValue);

	/**
	 * The SQL IS operator - consult any standard SQL reference for an explanation.
	 *
	 *	Implements the following truth table:
	 *
	 *	         otherValue
	 *	        | TRUE    | FALSE   | UNKNOWN
	 *	this    |----------------------------
	 *	        |
	 *	TRUE    | TRUE    | FALSE   | FALSE
	 *	FALSE   | FALSE   | TRUE    | FALSE
	 *	UNKNOWN | FALSE   | FALSE   | TRUE
	 *
	 *
	 * @param otherValue	BooleanDataValue to compare to. May be TRUE, FALSE, or UNKNOWN.
	 *
	 * @return	whether this IS otherValue
	 *
	 */
	public BooleanDataValue is(BooleanDataValue otherValue);

	/**
	 * Implements NOT IS. This reverses the sense of the is() call.
	 *
	 *
	 * @param otherValue	BooleanDataValue to compare to. May be TRUE, FALSE, or UNKNOWN.
	 *
	 * @return	NOT( this IS otherValue )
	 *
	 */
	public BooleanDataValue isNot(BooleanDataValue otherValue);

	/**
	 * Throw an exception with the given SQLState if this BooleanDataValue
	 * is false. This method is useful for evaluating constraints.
	 *
	 * @param SQLState		The SQLState of the exception to throw if
	 *						this SQLBoolean is false.
	 * @param tableName		The name of the table to include in the exception
	 *						message.
	 * @param constraintName	The name of the failed constraint to include
	 *							in the exception message.
	 *
	 * @return	this
	 *
	 * @exception	StandardException	Thrown if this BooleanDataValue
	 *									is false.
	 */
	public BooleanDataValue throwExceptionIfFalse(
									String SQLState,
									String tableName,
									String constraintName)
							throws StandardException;

	/*
	** NOTE: The NOT operator is translated to "= FALSE", which does the same
	** thing.
	*/

	/**
	 * Set the value of this BooleanDataValue.
	 *
	 * @param theValue	Contains the boolean value to set this BooleanDataValue
	 *					to.  Null means set this BooleanDataValue to null.
	 *
	 * @return	This BooleanDataValue
	 *
	 */
	public void setValue(Boolean theValue);


	/**
	 * Set the value of this BooleanDataValue to the given int value
	 *
	 * @param theValue	The value to set this BooleanDataValue to
	 *
	 * @return	This BooleanDataValue
	 *
	 */
	public void setValue(Integer theValue);

	/**
	 * Set the value of this BooleanDataValue to the given double value
	 *
	 * @param theValue	The value to set this BooleanDataValue to
	 *
	 * @return	This BooleanDataValue
	 *
	 */
	public void setValue(Double theValue);

	/**
	 * Set the value of this BooleanDataValue to the given float value
	 *
	 * @param theValue	The value to set this BooleanDataValue to
	 *
	 * @return	This BooleanDataValue
	 *
	 */
	public void setValue(Float theValue);


	/**
	 * Set the value of this BooleanDataValue to the given short value
	 *
	 * @param theValue	The value to set this BooleanDataValue to
	 *
	 * @return	This BooleanDataValue
	 *
	 */
	public void setValue(Short theValue);

	/**
	 * Set the value of this BooleanDataValue to the given long value
	 *
	 * @param theValue	The value to set this BooleanDataValue to
	 *
	 * @return	This BooleanDataValue
	 *
	 */
	public void setValue(Long theValue);


	/**
	 * Set the value of this BooleanDataValue to the given Byte value
	 *
	 * @param theValue	The value to set this BooleanDataValue to
	 *
	 * @return	This BooleanDataValue
	 *
	 */
	public void setValue(Byte theValue);

	/**
	 * Tell whether a BooleanDataValue has the given value.  This is useful
	 * for short-circuiting.
	 *
	 * @param value		The value to look for
	 *
	 * @return	true if the BooleanDataValue contains the given value.
	 */
	public boolean equals(boolean value);
}
