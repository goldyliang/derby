/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.sql.execute
   (C) Copyright IBM Corp. 2000, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.sanity.SanityManager;

import org.apache.derby.iapi.error.StandardException;

import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

import org.apache.derby.iapi.types.DataValueDescriptor;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;

import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ResultDescription;

import org.apache.derby.vti.DeferModification;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Properties;

/**
 * Insert the rows from the source into the specified
 * base table. This will cause constraints to be checked
 * and triggers to be executed based on the c's and t's
 * compiled into the insert plan.
 */
public class InsertVTIResultSet extends DMLVTIResultSet
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_2000_2004;

	private PreparedStatement		ps;
	private VTIResultSet			vtiRS;
	private java.sql.ResultSet		rs;

	private	TemporaryRowHolderImpl	rowHolder;

    /**
	 *
	 * @exception StandardException		Thrown on error
     */
    public InsertVTIResultSet(NoPutResultSet source, 
							  NoPutResultSet vtiRS,
						   Activation activation)
		throws StandardException
    {
		super(source, activation);
		this.vtiRS = (VTIResultSet) vtiRS;
	}
	
	/**
		@exception StandardException Standard Cloudscape error policy
	*/
	protected void openCore() throws StandardException
	{
		/* We must instantiate the VTI on each execution if any of the
		 * parameters contain a ?.
		 */
		if (ps == null) 
		{
			ps = (PreparedStatement) vtiRS.getVTIConstructor().invoke(activation);
		}

        if( ps instanceof DeferModification)
        {
            try
            {
                ((DeferModification) ps).modificationNotify( DeferModification.INSERT_STATEMENT, constants.deferred);
            }
            catch (Throwable t)
            {
                throw StandardException.unexpectedUserException(t);
            }
        }

		row = getNextRowCore(sourceResultSet);

		try
		{
			rs = ps.executeQuery();
		}
		catch (Throwable t)
		{
			throw StandardException.unexpectedUserException(t);
		}
		normalInsertCore(lcc, firstExecute);
	} // end of openCore


	// Do the work for a "normal" insert
	private void normalInsertCore(LanguageConnectionContext lcc, boolean firstExecute)
		throws StandardException
	{
		/* Get or re-use the row changer.
		 * NOTE: We need to set ourself as the top result set
		 * if this is not the 1st execution.  (Done in constructor
		 * for 1st execution.)
		 */
		if (! firstExecute)
		{
			lcc.getStatementContext().setTopResultSet(this, subqueryTrackingArray);
		}

		/* The source does not know whether or not we are doing a
		 * deferred mode insert.  If we are, then we must clear the
		 * index scan info from the activation so that the row changer
		 * does not re-use that information (which won't be valid for
		 * a deferred mode insert).
		 */
		if (constants.deferred)
		{
			activation.clearIndexScanInfo();
		}

		if (firstExecute && constants.deferred)
		{
			Properties properties = new Properties();

			/*
			** If deferred we save a copy of the entire row.
			*/
			rowHolder = new TemporaryRowHolderImpl(tc, properties, resultDescription);
		}

		while ( row != null )
        {
			/*
			** If we're doing a deferred insert, insert into the temporary
			** conglomerate.  Otherwise, insert directly into the permanent
			** conglomerates using the rowChanger.
			*/
			if (constants.deferred)
			{
				rowHolder.insert(row);
			}
			else
			{
				insertIntoVTI(rs);
			}

            rowCount++;

			// No need to do a next on a single row source
			if (constants.singleRowSource)
			{
				row = null;
			}
			else
			{
				row = getNextRowCore(sourceResultSet);
			}
        }

		/*
		** If it's a deferred insert, scan the temporary conglomerate and
		** insert the rows into the permanent conglomerates using rowChanger.
		*/
		if (constants.deferred)
		{
			CursorResultSet tempRS = rowHolder.getResultSet();
			try
			{
                ExecRow	deferredRowBuffer = null;

				tempRS.open();
				while ((deferredRowBuffer = tempRS.getNextRow()) != null)
				{
					row = deferredRowBuffer;
					insertIntoVTI(rs);
				}
			} finally
			{
				sourceResultSet.clearCurrentRow();
				tempRS.close();
			}
		}

		if (rowHolder != null)
		{
			rowHolder.close();
			// rowHolder kept across opens
		}
    } // end of normalInsertCore

	private void insertIntoVTI(ResultSet target)
		throws StandardException
	{
		try
		{
			target.moveToInsertRow();

			DataValueDescriptor[] rowArray = row.getRowArray();
			for (int index = 0; index < rowArray.length; index++)
			{
				DataValueDescriptor dvd = rowArray[index];

				try {
					if (dvd.isNull())
						target.updateNull(index + 1);
					else
						dvd.setInto(target, index + 1);
				} catch (Throwable t) {
					// backwards compatibility - 5.0 and before used
					// updateObject always.
					target.updateObject(index + 1, dvd.getObject());
				}
			}

			target.insertRow();
		}
		catch (Throwable t)
		{
			throw StandardException.unexpectedUserException(t);
		}
	}

	/**
	 * @see org.apache.derby.iapi.sql.ResultSet#cleanUp
	 *
	 * @exception StandardException		Thrown on error
	 */
	public void	cleanUp() throws StandardException
	{
		if (rowHolder != null)
		{
			rowHolder.close();
		}

		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (Throwable t)
			{
				throw StandardException.unexpectedUserException(t);
			}
			rs = null;
		}

		// Close the ps if it needs to be instantiated on each execution
		if (!vtiRS.isReuseablePs() && ps != null)
		{
			try
			{
				ps.close();
				ps = null;
			}
			catch (Throwable t)
			{
				throw StandardException.unexpectedUserException(t);
			}
		}
		super.cleanUp();
	} // end of cleanUp

	// Class implementation

	public void finish() throws StandardException {

		if ((ps != null) && !vtiRS.isReuseablePs())
		{
			try
			{
				ps.close();
				ps = null;
			}
			catch (Throwable t)
			{
				throw StandardException.unexpectedUserException(t);
			}
		}
		super.finish();
	} // end of finish
}

