/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.iapi.services.diag
   (C) Copyright IBM Corp. 1998, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.iapi.services.diag;

import org.apache.derby.iapi.error.StandardException;

import java.util.Properties;

/**

  The Diagnosticable class implements the Diagnostics protocol, and can
  be used as the parent class for all other Diagnosticable objects.

**/

public interface Diagnosticable
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1998_2004;
	/*
	** Methods of Diagnosticable
	*/
    public void init(Object obj);

    /**
     * Default implementation of diagnostic on the object.
     * <p>
     * This routine returns a string with whatever diagnostic information
     * you would like to provide about this associated object passed in
     * the init() call.
     * <p>
     * This routine should be overriden by a real implementation of the
     * diagnostic information you would like to provide.
     * <p>
     *
	 * @return A string with diagnostic information about the object.
     *
     * @exception StandardException  Standard cloudscape exception policy
     **/
    public String diag() throws StandardException;

    /**
     * Default implementation of detail diagnostic on the object.
     * <p>
     * This interface provides a way for an object to pass back pieces of
     * information as requested by the caller.  The information is passed
     * back and forth through the properties argument.  It is expected that
     * the caller knows what kind of information to ask for, and correctly
     * handles the situation when the diagnostic object can't provide the
     * information.
     * <p>
     * As an example assume an object TABLE exists, and that we have created
     * an object D_TABLE that knows how to return the number of pages in the
     * TABLE object.  The code to get that information out would looks something
     * like the following:
     * <p>
     * print_num_pages(Object table)
     * {
     *     Properties prop = new Properties();
     *     prop.put(Page.DIAG_NUM_PAGES,        "");
     *
     *     DiagnosticUtil.findDiagnostic(table).diag_detail(prop);
     *
     *     System.out.println(
     *        "number of pages = " + prop.getProperty(Page.DIAG_NUM_PAGES));
     * }
     * <p>
     * This routine should be overriden if there is detail diagnostics to
     * be provided by a real implementation.
     * <p>
     *
     * @exception StandardException  Standard cloudscape exception policy
     **/
    public void diag_detail(Properties prop) throws StandardException;
}
