/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.sql.compile
   (C) Copyright IBM Corp. 2003, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.reference.SQLState;

import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.sanity.SanityManager;
import org.apache.derby.iapi.services.io.StoredFormatIds;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;

import org.apache.derby.iapi.types.BitDataValue;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.types.TypeId;

import org.apache.derby.iapi.types.DataTypeDescriptor;

import org.apache.derby.iapi.sql.compile.TypeCompiler;

import org.apache.derby.iapi.reference.ClassName;

import java.sql.Types;
import org.apache.derby.iapi.reference.JDBC20Translation;

/**
 * This class implements TypeCompiler for the SQL LOB types.
 *
 * @author Jonas S Karlsson
 */

public class CLOBTypeCompiler extends BaseTypeCompiler
	/**
		IBM Copyright &copy notice.
	*/

{ private static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_2003_2004;

        /**
         * Tell whether this type (LOB) can be compared to the given type.
         * Clobs are not comparable.
         *
         * @param otherType     The TypeId of the other type.
         */

        public boolean comparable(TypeId otherType,
                                  boolean forEquals,
                                  ClassFactory cf)
        {
			return false;
        }


        /**
         * Tell whether this type (LOB) can be converted to the given type.
         *
         * @see TypeCompiler#convertible
         */
        public boolean convertible(TypeId otherType, 
								   boolean forDataTypeFunction)
        {
            // allow casting to any string
            return (otherType.isStringTypeId()) ;

        }

        /**
         * Tell whether this type (CLOB) is compatible with the given type.
         *
         * @param otherType     The TypeId of the other type.
         */
		public boolean compatible(TypeId otherType)
		{
				return convertible(otherType,false);
		}

	    /**
         * Tell whether this type (LOB) can be stored into from the given type.
         *
         * @param otherType     The TypeId of the other type.
         * @param cf            A ClassFactory
         */

        public boolean storable(TypeId otherType, ClassFactory cf)
        {
            // no automatic conversions at store time--but string
			// literals (or values of type CHAR/VARCHAR) are STORABLE
            // as clobs, even if the two types can't be COMPARED.
            return (otherType.isStringTypeId()) ;
        }

        /** @see TypeCompiler#interfaceName */
        public String interfaceName()
        {
            return ClassName.StringDataValue;
        }

        /**
         * @see TypeCompiler#getCorrespondingPrimitiveTypeName
         */

        public String getCorrespondingPrimitiveTypeName() {
            int formatId = getStoredFormatIdFromTypeId();
            switch (formatId) {
                case StoredFormatIds.CLOB_TYPE_ID:  return "java.sql.Clob";
                case StoredFormatIds.NCLOB_TYPE_ID: return "java.sql.Clob";
                default:
                    if (SanityManager.DEBUG)
                        SanityManager.THROWASSERT("unexpected formatId in getCorrespondingPrimitiveTypeName() - " + formatId);
                    return null;
            }
        }

        public String getMatchingNationalCharTypeName()
        {
            return TypeId.NCLOB_NAME;
        }

        /**
         * @see TypeCompiler#getCastToCharWidth
         */
        public int getCastToCharWidth(DataTypeDescriptor dts)
        {
                return dts.getMaximumWidth();
        }

        protected String nullMethodName() {
            int formatId = getStoredFormatIdFromTypeId();
            switch (formatId) {
                case StoredFormatIds.CLOB_TYPE_ID:  return "getNullClob";
                case StoredFormatIds.NCLOB_TYPE_ID: return "getNullNClob";
                default:
                    if (SanityManager.DEBUG)
                        SanityManager.THROWASSERT("unexpected formatId in nullMethodName() - " + formatId);
                    return null;
            }
        }

        protected String dataValueMethodName()
        {
            int formatId = getStoredFormatIdFromTypeId();
            switch (formatId) {
                case StoredFormatIds.CLOB_TYPE_ID:  return "getClobDataValue";
                case StoredFormatIds.NCLOB_TYPE_ID: return "getNClobDataValue";
                default:
                    if (SanityManager.DEBUG)
                        SanityManager.THROWASSERT("unexpected formatId in dataValueMethodName() - " + formatId);
                    return null;
                }
        }
}
