/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.sql.compile
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package	org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.sanity.SanityManager;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.error.StandardException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * QueryTreeNodeVector is the root class for all lists of query tree nodes.
 * It provides a wrapper for java.util.Vector. All
 * lists of query tree nodes inherit from QueryTreeNodeVector.
 *
 * @author Jerry Brenner
 */

abstract class QueryTreeNodeVector extends QueryTreeNode
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;
	private Vector			v = new Vector();

	public final int size()
	{
		return v.size();
	}

	public QueryTreeNode elementAt(int index)
	{
		return (QueryTreeNode) v.elementAt(index);
	}

	public final void addElement(QueryTreeNode qt)
	{
		v.addElement(qt);
	}

	final void removeElementAt(int index)
	{
		v.removeElementAt(index);
	}

	final void removeElement(QueryTreeNode qt)
	{
		v.removeElement(qt);
	}

	final Object remove(int index)
	{
		return((QueryTreeNode) (v.remove(index)));
	}

	final int indexOf(QueryTreeNode qt)
	{
		return v.indexOf(qt);
	}

	final void setElementAt(QueryTreeNode qt, int index)
	{
		v.setElementAt(qt, index);
	}

	public void destructiveAppend(QueryTreeNodeVector qtnv)
	{
		nondestructiveAppend(qtnv);
		qtnv.removeAllElements();
	}

	public void nondestructiveAppend(QueryTreeNodeVector qtnv)
	{
		int qtnvSize = qtnv.size();
		for (int index = 0; index < qtnvSize; index++)
		{
			v.addElement(qtnv.elementAt(index));
		}
	}

	final void removeAllElements()
	{
		v.removeAllElements();
	}

	final void insertElementAt(QueryTreeNode qt, int index)
	{
		v.insertElementAt(qt, index);
	}

	/**
	 * Format this list as a string
	 *
	 * We can simply iterate through the list.  Note each list member
	 * is a QueryTreeNode, and so should have its specialization of
	 * toString defined.
	 *
	 * @return	This list formatted as a String
	 */
	public String toString()
	{
		if (SanityManager.DEBUG)
		{
			StringBuffer	buffer = new StringBuffer("");

			for (int index = 0; index < size(); index++)
			{
				buffer.append(elementAt(index).toString()).append("; ");
			}

			return buffer.toString();
		}
		else
		{
			return "";
		}
	}

	/**
	 * Accept a visitor, and call v.visit()
	 * on child nodes as necessary.  
	 * 
	 * @param v the visitor
	 *
	 * @exception StandardException on error
	 */
	public Visitable accept(Visitor v) 
		throws StandardException
	{
		Visitable		returnNode = v.visit(this);
	
		if (v.skipChildren(this))
		{
			return returnNode;
		}

		int size = size();
		for (int index = 0; index < size; index++)
		{
			setElementAt((QueryTreeNode)((QueryTreeNode) elementAt(index)).accept(v), index);
		}
		
		return returnNode;
	}
}
