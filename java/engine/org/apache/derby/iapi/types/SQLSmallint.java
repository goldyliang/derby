/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.iapi.types
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.reference.SQLState;

import org.apache.derby.iapi.services.io.ArrayInputStream;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.BooleanDataValue;

import org.apache.derby.iapi.services.io.FormatIdUtil;
import org.apache.derby.iapi.services.io.StoredFormatIds;
import org.apache.derby.iapi.services.io.Storable;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.sanity.SanityManager;

import org.apache.derby.iapi.services.cache.ClassSize;

import org.apache.derby.iapi.types.NumberDataType;
import org.apache.derby.iapi.types.SQLBoolean;

import java.math.BigDecimal;

import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * SQLSmallint satisfies the DataValueDescriptor
 * interfaces (i.e., OrderableDataType). It implements a smallint column, 
 * e.g. for storing a column value; it can be specified
 * when constructed to not allow nulls. Nullability cannot be changed
 * after construction, as it affects the storage size and mechanism.
 * <p>
 * Because OrderableDataType is a subtype of ValueColumn,
 * SQLSmallint can play a role in either a ValueColumn/Row
 * or a OrderableDataType/Row, interchangeably.
 * <p>
 * We assume the store has a flag for nullness of the value,
 * and simply return a 0-length array for the stored form
 * when the value is null.
 * <p>
 * PERFORMANCE: There are likely alot of performance improvements
 * possible for this implementation -- it new's Short
 * more than it probably wants to.
 */
public final class SQLSmallint
	extends NumberDataType
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;
	/*
	 * DataValueDescriptor interface
	 * (mostly implemented in DataType)
	 */


        // JDBC is lax in what it permits and what it
	// returns, so we are similarly lax

	/** 
	 * @see DataValueDescriptor#getInt 
	 */
	public int	getInt()
	{
		return (int) value;
	}

	/** 
	 * @exception StandardException thrown on failure to convert
	 * @see DataValueDescriptor#getByte 
	 */
	public byte	getByte() throws StandardException
	{
		if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE)
			throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "TINYINT");
		return (byte) value;
	}

	/** 
	 * @see DataValueDescriptor#getShort 
	 */
	public short	getShort()
	{
		return value;
	}

	/** 
	 * @see DataValueDescriptor#getLong 
	 */
	public long	getLong()
	{
		return (long) value;
	}

	/** 
	 * @see DataValueDescriptor#getFloat 
	 */
	public float	getFloat()
	{
		return (float) value;
	}

	/** 
	 * @see DataValueDescriptor#getDouble 
	 */
	public double	getDouble()
	{
		return (double) value;
	}

	/** 
	 * @see DataValueDescriptor#getBigDecimal 
	 */
	public BigDecimal	getBigDecimal()
	{
		if (isNull()) return null;
		return BigDecimal.valueOf(value);
	}

    // for lack of a specification: 0 or null is false,
    // all else is true
	/** 
	 * @see DataValueDescriptor#getBoolean 
	 */
	public boolean	getBoolean()
	{
		return (value != 0);
	}

	/** 
	 * @see DataValueDescriptor#getString 
	 */
	public String	getString()
	{
		if (isNull())
			return null;
		else
			return Short.toString(value);
	}

	/** 
	 * @see DataValueDescriptor#getLength 
	 */
	public int	getLength()
	{
		return SMALLINT_LENGTH;
	}

	/** 
	 * @see DataValueDescriptor#getObject 
	 */
	public Object	getObject() 
	{
		if (isNull())
			return null;
		else
			return new Integer(value);
	}

	// this is for DataType's error generator
	public String getTypeName()
	{
		return TypeId.SMALLINT_NAME;
	}

	/*
	 * Storable interface, implies Externalizable, TypedFormat
	 */


	/**
		Return my format identifier.

		@see org.apache.derby.iapi.services.io.TypedFormat#getTypeFormatId
	*/
	public int getTypeFormatId() {
		return StoredFormatIds.SQL_SMALLINT_ID;
	}

	/**
	 * always false for non-nullable columns
	 @see Storable#isNull
	*/
	public boolean isNull()
	{
		return isnull;
	}

	public void writeExternal(ObjectOutput out) throws IOException {

		// never called when value is null
		if (SanityManager.DEBUG)
			SanityManager.ASSERT(! isNull());

		out.writeShort(value);
	}

	/** @see java.io.Externalizable#readExternal */
	public void readExternalFromArray(ArrayInputStream in) throws IOException {

		value = in.readShort();
		isnull = false;
	}
	public void readExternal(ObjectInput in) throws IOException {

		value = in.readShort();
		isnull = false;
	}

	/**
	 * @see Storable#restoreToNull
	 *
	 */
	public void restoreToNull()
	{
		value = 0;
		isnull = true;
	}


	/** @exception StandardException		Thrown on error */
	protected int typeCompare(DataValueDescriptor arg) throws StandardException
	{
		/* neither are null, get the value */

		/* Do comparisons with ints to avoid overflow problems */
		int thisValue = this.getInt();
		int otherValue = arg.getInt();
		if (thisValue == otherValue)
			return 0;
		else if (thisValue > otherValue)
			return 1;
		else
			return -1;
	}

	/*
	 * DataValueDescriptor interface
	 */

	/** @see DataValueDescriptor#getClone */
	public DataValueDescriptor getClone()
	{
		return new SQLSmallint(value, isnull);
	}

	/**
	 * @see DataValueDescriptor#getNewNull
	 */
	public DataValueDescriptor getNewNull()
	{
		return new SQLSmallint();
	}

	/** 
	 * @see DataValueDescriptor#setValueFromResultSet 
	 *
	 * @exception SQLException		Thrown on error
	 */
	public void setValueFromResultSet(ResultSet resultSet, int colNumber,
									  boolean isNullable)
		throws SQLException
	{
		try {
			value = resultSet.getShort(colNumber);
			isnull = (isNullable && resultSet.wasNull());
		} catch (SQLException selq) {
			System.out.println("GET SHORT " + selq.toString());
			int i = resultSet.getInt(colNumber);
			System.out.println("GET SHORT VALUE " + i);
			value = (short) i;
			isnull = false;

		}
	}
	/**
		Set the value into a PreparedStatement.

		@exception SQLException Error setting value in PreparedStatement
	*/
	public final void setInto(PreparedStatement ps, int position) throws SQLException {

		if (isNull()) {
			ps.setNull(position, java.sql.Types.SMALLINT);
			return;
		}

		ps.setShort(position, value);
	}
	/**
		Set this value into a ResultSet for a subsequent ResultSet.insertRow
		or ResultSet.updateRow. This method will only be called for non-null values.

		@exception SQLException thrown by the ResultSet object
		@exception StandardException thrown by me accessing my value.
	*/
	public final void setInto(ResultSet rs, int position) throws SQLException, StandardException {
		rs.updateShort(position, value);
	}


	/*
	 * class interface
	 */

	/*
	 * constructors
	 */

	/**
		No-arg constructor, required by Formattable.
    // This constructor also gets used when we are
    // allocating space for a short.
	*/
	public SQLSmallint() 
	{
		isnull = true;
	}

	public SQLSmallint(short val)
	{
		value = val;
	}

	/* This constructor gets used for the getClone() method */
	public SQLSmallint(short val, boolean isnull)
	{
		value = val;
		this.isnull = isnull;
	}

	/**
		@exception StandardException thrown if string not accepted
	 */
	public void setValue(String theValue)
		throws StandardException
	{
		if (theValue == null)
		{
			value = 0;
			isnull = true;
		}
		else
		{
		    try {
		        value = Short.valueOf(theValue.trim()).shortValue();
			} catch (NumberFormatException nfe) {
			    throw invalidFormat();
			}
			isnull = false;
		}

	}

	public void setValue(short theValue)
	{
		value = theValue;
		isnull = false;
	}

	public void setValue(byte theValue)
	{
		value = theValue;
		isnull = false;
	}

	/**
		@exception StandardException if outsideRangeForSmallint
	 */
	public void setValue(int theValue) throws StandardException
	{
		if (theValue > Short.MAX_VALUE || theValue < Short.MIN_VALUE)
			throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "SMALLINT");
		value = (short)theValue;
		isnull = false;
	}

	/**
		@exception StandardException if outsideRangeForSmallint
	 */
	public void setValue(long theValue) throws StandardException
	{
		if (theValue > Short.MAX_VALUE || theValue < Short.MIN_VALUE)
			throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "SMALLINT");
		value = (short)theValue;
		isnull = false;
	}

	/**
	 * @see NumberDataValue#setValue
	 *
	 * @exception StandardException		Thrown on error
	 */
	public void setValue(float theValue) throws StandardException
	{
		theValue = NumberDataType.normalizeREAL(theValue);

		if (theValue > Short.MAX_VALUE || theValue < Short.MIN_VALUE)
			throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "SMALLINT");

		float floorValue = (float)Math.floor(theValue);

		value = (short)floorValue;
		isnull = false;
	}

	/**
	 * @see NumberDataValue#setValue
	 *
	 * @exception StandardException		Thrown on error
	 */
	public void setValue(double theValue) throws StandardException
	{
		theValue = NumberDataType.normalizeDOUBLE(theValue);

		if (theValue > Short.MAX_VALUE || theValue < Short.MIN_VALUE)
			throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "SMALLINT");

		double floorValue = Math.floor(theValue);

		value = (short)floorValue;
		isnull = false;
	}

	/**
	 * @see NumberDataValue#setValue
	 *
	 */
	public void setValue(boolean theValue)
	{
		value = theValue?(short)1:(short)0;
		isnull = false;
	}

	/**
	 * @see DataValueDescriptor#setValue
	 *
	 * @exception StandardException		Thrown on error
	 */	
	public void setValue(Object theValue)
		throws StandardException
	{
		if (theValue == null)
		{
			setToNull();
		}		
		else if (theValue instanceof Number)
		{
			this.setValue(((Number)theValue).shortValue());
		}
		else
		{
			genericSetObject(theValue);
		}
	}
	protected void setFrom(DataValueDescriptor theValue) throws StandardException {

		setValue(theValue.getShort());
	}

	/*
	 * DataValueDescriptor interface
	 */

	/** @see DataValueDescriptor#typePrecedence */
	public int typePrecedence()
	{
		return TypeId.SMALLINT_PRECEDENCE;
	}


	/*
	** SQL Operators
	*/

	/**
	 * The = operator as called from the language module, as opposed to
	 * the storage module.
	 *
	 * @param left			The value on the left side of the =
	 * @param right			The value on the right side of the =
	 *
	 * @return	A SQL boolean value telling whether the two parameters are equal
	 *
	 * @exception StandardException		Thrown on error
	 */

	public BooleanDataValue equals(DataValueDescriptor left,
							DataValueDescriptor right)
			throws StandardException
	{
		return SQLBoolean.truthValue(left,
									 right,
									 left.getShort() == right.getShort());
	}

	/**
	 * The <> operator as called from the language module, as opposed to
	 * the storage module.
	 *
	 * @param left			The value on the left side of the <>
	 * @param right			The value on the right side of the <>
	 *
	 * @return	A SQL boolean value telling whether the two parameters
	 *			are not equal
	 *
	 * @exception StandardException		Thrown on error
	 */

	public BooleanDataValue notEquals(DataValueDescriptor left,
							DataValueDescriptor right)
			throws StandardException
	{
		return SQLBoolean.truthValue(left,
									 right,
									 left.getShort() != right.getShort());
	}

	/**
	 * The < operator as called from the language module, as opposed to
	 * the storage module.
	 *
	 * @param left			The value on the left side of the <
	 * @param right			The value on the right side of the <
	 *						is not.
	 *
	 * @return	A SQL boolean value telling whether the first operand is less
	 *			than the second operand
	 *
	 * @exception StandardException		Thrown on error
	 */

	public BooleanDataValue lessThan(DataValueDescriptor left,
							DataValueDescriptor right)
			throws StandardException
	{
		return SQLBoolean.truthValue(left,
									 right,
									 left.getShort() < right.getShort());
	}

	/**
	 * The > operator as called from the language module, as opposed to
	 * the storage module.
	 *
	 * @param left			The value on the left side of the >
	 * @param right			The value on the right side of the >
	 *
	 * @return	A SQL boolean value telling whether the first operand is greater
	 *			than the second operand
	 *
	 * @exception StandardException		Thrown on error
	 */

	public BooleanDataValue greaterThan(DataValueDescriptor left,
							DataValueDescriptor right)
			throws StandardException
	{
		return SQLBoolean.truthValue(left,
									 right,
									 left.getShort() > right.getShort());
	}

	/**
	 * The <= operator as called from the language module, as opposed to
	 * the storage module.
	 *
	 * @param left			The value on the left side of the <=
	 * @param right			The value on the right side of the <=
	 *
	 * @return	A SQL boolean value telling whether the first operand is less
	 *			than or equal to the second operand
	 *
	 * @exception StandardException		Thrown on error
	 */

	public BooleanDataValue lessOrEquals(DataValueDescriptor left,
							DataValueDescriptor right)
			throws StandardException
	{
		return SQLBoolean.truthValue(left,
									 right,
									 left.getShort() <= right.getShort());
	}

	/**
	 * The >= operator as called from the language module, as opposed to
	 * the storage module.
	 *
	 * @param left			The value on the left side of the >=
	 * @param right			The value on the right side of the >=
	 *
	 * @return	A SQL boolean value telling whether the first operand is greater
	 *			than or equal to the second operand
	 *
	 * @exception StandardException		Thrown on error
	 */

	public BooleanDataValue greaterOrEquals(DataValueDescriptor left,
							DataValueDescriptor right)
			throws StandardException
	{
		return SQLBoolean.truthValue(left,
									 right,
									 left.getShort() >= right.getShort());
	}


	/**
	 * This method implements the + operator for "smallint + smallint".
	 *
	 * @param addend1	One of the addends
	 * @param addend2	The other addend
	 * @param result	The result of a previous call to this method, null
	 *					if not called yet
	 *
	 * @return	A SQLSmallint containing the result of the addition
	 *
	 * @exception StandardException		Thrown on error
	 */

	public NumberDataValue plus(NumberDataValue addend1,
							NumberDataValue addend2,
							NumberDataValue result)
				throws StandardException
	{
		if (result == null)
		{
			result = new SQLSmallint();
		}

		if (addend1.isNull() || addend2.isNull())
		{
			result.setToNull();
			return result;
		}

		/*
		** Java does not check for overflow with integral types. We have to
		** check the result ourselves.
		**
			The setValue(int) will perform the overflow check.
		*/
		int sum = addend1.getShort() + addend2.getShort();

		result.setValue(sum);
		return result;
	}

	/**
	 * This method implements the - operator for "smallint - smallint".
	 *
	 * @param left	The value to be subtracted from
	 * @param right	The value to be subtracted
	 * @param result	The result of a previous call to this method, null
	 *					if not called yet
	 *
	 * @return	A SQLSmallint containing the result of the subtraction
	 *
	 * @exception StandardException		Thrown on error
	 */

	public NumberDataValue minus(NumberDataValue left,
							NumberDataValue right,
							NumberDataValue result)
				throws StandardException
	{
		if (result == null)
		{
			result = new SQLSmallint();
		}

		if (left.isNull() || right.isNull())
		{
			result.setToNull();
			return result;
		}

		/*
		** Java does not check for overflow with integral types. We have to
		** check the result ourselves.
		**
			The setValue(int) will perform the overflow check.
		*/
		int difference = left.getShort() - right.getShort();

		result.setValue(difference);
		return result;
	}

	/**
	 * This method implements the * operator for "smallint * smallint".
	 *
	 * @param left	The first value to be multiplied
	 * @param right	The second value to be multiplied
	 * @param result	The result of a previous call to this method, null
	 *					if not called yet
	 *
	 * @return	A SQLSmallint containing the result of the multiplication
	 *
	 * @exception StandardException		Thrown on error
	 */

	public NumberDataValue times(NumberDataValue left,
							NumberDataValue right,
							NumberDataValue result)
				throws StandardException
	{
		if (result == null)
		{
			result = new SQLSmallint();
		}

		if (left.isNull() || right.isNull())
		{
			result.setToNull();
			return result;
		}

		/*
		** Java does not check for overflow with integral types. We have to
		** check the result ourselves.
		**
			The setValue(int) will perform the overflow check.
		*/
		int product = left.getShort() * right.getShort();
		result.setValue(product);
		return result;
	}

	/**
	 * This method implements the / operator for "smallint / smallint".
	 *
	 * @param dividend	The numerator
	 * @param divisor	The denominator
	 * @param result	The result of a previous call to this method, null
	 *					if not called yet
	 *
	 * @return	A SQLSmallint containing the result of the division
	 *
	 * @exception StandardException		Thrown on error
	 */

	public NumberDataValue divide(NumberDataValue dividend,
							 NumberDataValue divisor,
							 NumberDataValue result)
				throws StandardException
	{
		short	shortDivisor;

		if (result == null)
		{
			result = new SQLSmallint();
		}

		if (dividend.isNull() || divisor.isNull())
		{
			result.setToNull();
			return result;
		}

		/* Catch divide by 0 */
		shortDivisor = divisor.getShort();
		if (shortDivisor == 0)
		{
			throw StandardException.newException(SQLState.LANG_DIVIDE_BY_ZERO);
		}

		result.setValue((short) (dividend.getShort() / shortDivisor));
		return result;
	}
	/**
		mod(smallint, smallint)
	*/
	public NumberDataValue mod(NumberDataValue dividend,
							 NumberDataValue divisor,
							 NumberDataValue result)
				throws StandardException
	{
		if (result == null)
		{
			result = new SQLSmallint();
		}

		if (dividend.isNull() || divisor.isNull())
		{
			result.setToNull();
			return result;
		}

		/* Catch divide by 0 */
		short shortDivisor = divisor.getShort();
		if (shortDivisor == 0)
		{
			throw StandardException.newException(SQLState.LANG_DIVIDE_BY_ZERO);
		}

		result.setValue(dividend.getShort() % shortDivisor);
		return result;
	}
	/**
	 * This method implements the unary minus operator for smallint.
	 *
	 * @param result	The result of a previous call to this method, null
	 *					if not called yet
	 *
	 * @return	A SQLSmalllint containing the result of the division
	 *
	 * @exception StandardException		Thrown on error
	 */

	public NumberDataValue minus(NumberDataValue result)
									throws StandardException
	{
		if (result == null)
		{
			result = new SQLSmallint();
		}

		if (this.isNull())
		{
			result.setToNull();
			return result;
		}

		int operandValue = this.getShort();

		result.setValue(-operandValue);
		return result;
	}

    /**
     * This method implements the isNegative method.
     *
     * @return  A boolean.  If this.value is negative, return true.
     *          For positive values or null, return false.
     */

    protected boolean isNegative()
    {
        return !isNull() && value < 0;
    }

	/*
	 * String display of value
	 */

	public String toString()
	{
		if (isNull())
			return "NULL";
		else
			return Short.toString(value);
	}

	/*
	 * Hash code
	 */
	public int hashCode()
	{
		return (int) value;
	}

	/*
	 * useful constants...
	 */
	static final int SMALLINT_LENGTH = 2;

    private static final int BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog( SQLSmallint.class);

    public int estimateMemoryUsage()
    {
        return BASE_MEMORY_USAGE;
    }

	/*
	 * object state
	 */
	private short value;
	private boolean isnull;
}
