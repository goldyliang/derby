/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.iapi.store.access
   (C) Copyright IBM Corp. 1998, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException; 

/**

RowCountable provides the interfaces to read and write row counts in
tables.
<p>
@see ScanController
@see StoreCostController

**/

public interface RowCountable
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1998_2004;
    /**
     * Get the total estimated number of rows in the container.
     * <p>
     * The number is a rough estimate and may be grossly off.  In general
     * the server will cache the row count and then occasionally write
     * the count unlogged to a backing store.  If the system happens to 
     * shutdown before the store gets a chance to update the row count it
     * may wander from reality.
     * <p>
     * For btree conglomerates this call will return the count of both
     * user rows and internal implementaation rows.  The "BTREE" implementation
     * generates 1 internal implementation row for each page in the btree, and 
     * it generates 1 internal implementation row for each branch row.  For
     * this reason it is recommended that clients if possible use the count
     * of rows in the heap table to estimate the number of rows in the index
     * rather than use the index estimated row count.
     *
	 * @return The total estimated number of rows in the conglomerate.
     *
	 * @exception  StandardException  Standard exception policy.
     **/
    public long getEstimatedRowCount()
		throws StandardException;

    /**
     * Set the total estimated number of rows in the container.
     * <p>
     * Often, after a scan, the client of RawStore has a much better estimate
     * of the number of rows in the container than what store has.  For 
     * instance if we implement some sort of update statistics command, or
     * just after a create index a complete scan will have been done of the
     * table.  In this case this interface allows the client to set the
     * estimated row count for the container, and store will use that number
     * for all future references.
     * <p>
     * This routine can also be used to set the estimated row count in the
     * index to the number of rows in the base table, another workaround for
     * the problem that index estimated row count includes non-user rows.
     *
     * @param count the estimated number of rows in the container.
     *
	 * @return The total estimated number of rows in the conglomerate.
     *
	 * @exception  StandardException  Standard exception policy.
     **/
    public void setEstimatedRowCount(long count)
		throws StandardException;

}
