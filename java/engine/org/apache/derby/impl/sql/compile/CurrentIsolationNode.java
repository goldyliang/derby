/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.sql.compile
   (C) Copyright IBM Corp. 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package	org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.compiler.LocalField;

import org.apache.derby.iapi.services.sanity.SanityManager;

import org.apache.derby.iapi.services.classfile.VMOpcode;
import org.apache.derby.iapi.reference.ClassName;

import org.apache.derby.impl.sql.compile.ExpressionClassBuilder;

import java.lang.reflect.Modifier;

import java.sql.Types;
import java.util.Vector;

/**
 * The CurrentIsolationNode is for the CURRENT ISOLATION special register
 *
 * @author Jack Klebanoff
 */
public class CurrentIsolationNode extends ValueNode 
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_2004;


	/**
	 * Return the variant type for the underlying expression.
	 * The variant type can be:
	 *		VARIANT				- variant within a scan
	 *							  (method calls and non-static field access)
	 *		SCAN_INVARIANT		- invariant within a scan
	 *							  (column references from outer tables)
	 *		QUERY_INVARIANT		- invariant within the life of a query
	 *							  (constant expressions)
	 *
	 * @return	The variant type for the underlying expression.
	 */
	protected int getOrderableVariantType()
	{
		// CurrentDate, Time, Timestamp are invariant for the life of the query
		return Qualifier.QUERY_INVARIANT;
	}

	//
	// QueryTreeNode interface
	//

	/**
	 * Binding this expression means setting the result DataTypeServices.
	 * In this case, the result type is based on the operation requested.
	 *
	 * @param fromList			The FROM list for the statement.  This parameter
	 *							is not used in this case.
	 * @param subqueryList		The subquery list being built as we find 
	 *							SubqueryNodes. Not used in this case.
	 * @param aggregateVector	The aggregate vector being built as we find 
	 *							AggregateNodes. Not used in this case.
	 *
	 * @return	The new top of the expression tree.
	 *
	 * @exception StandardException		Thrown on error
	 */
	public ValueNode bindExpression(FromList fromList, SubqueryList subqueryList,
							Vector	aggregateVector)
					throws StandardException
	{
        setType( DataTypeDescriptor.getBuiltInDataTypeDescriptor(Types.CHAR, 2));
        return this;
    }

	/**
	 * CurrentDatetimeOperatorNode is used in expressions.
	 * The expression generated for it invokes a static method
	 * on a special Cloudscape type to get the system time and
	 * wrap it in the right java.sql type, and then wrap it
	 * into the right shape for an arbitrary value, i.e. a column
	 * holder. This is very similar to what constants do.
	 *
	 * @param acb	The ExpressionClassBuilder for the class being built
	 * @param mb	The method the code to place the code
	 *
	 *
	 * @exception StandardException		Thrown on error
	 */
	public void generateExpression(ExpressionClassBuilder acb,
											MethodBuilder mb)
									throws StandardException
	{
        mb.pushThis();
		mb.callMethod(VMOpcode.INVOKEINTERFACE, ClassName.Activation, "getLanguageConnectionContext",
											 ClassName.LanguageConnectionContext, 0);
        mb.callMethod(VMOpcode.INVOKEINTERFACE, (String) null, "getCurrentIsolationLevelStr", "java.lang.String", 0);
        String fieldType = getTypeCompiler().interfaceName();
		LocalField field = acb.newFieldDeclaration(Modifier.PRIVATE, fieldType);

		acb.generateDataValue(mb, getTypeCompiler(), field);
	} // end of generateExpression

    
    public String toString()
    {
        if (SanityManager.DEBUG)
		{
			return super.toString() + "\n";
		}
		else
		{
			return "";
		}
	}

}
