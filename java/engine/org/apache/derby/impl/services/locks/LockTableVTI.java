/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.services.locks
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.impl.services.locks;

import org.apache.derby.iapi.services.locks.Latch;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import java.util.ListIterator;
import java.util.List;

/**
	This provides an Enumeration of Latch's
	from a clone of the lock table. A Latch is badly named,
	it represents lock information.
 */
class LockTableVTI implements Enumeration
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;
	// the clonedLockTable temporarily holds a copy of the lock table.
	//
	// The copy is necessary because the real lock manager needs to be single
	// threaded while a snap shot is made.  After the copy is made, it can take
	// its time digesting the information without blocking the real lock
	// manager.

	private final LockSet clonedLockTable;
	private final Enumeration outerControl;
	private Control control;
	private ListIterator grantedList;
	private ListIterator waitingList;
	private Latch nextLock;

	LockTableVTI(LockSet clonedLockTable)
	{
		this.clonedLockTable = clonedLockTable;

		outerControl = clonedLockTable.elements();
	}


	public boolean hasMoreElements() {

		if (nextLock != null)
			return true;

		for (;;) {

			if (control == null) {
				if (!outerControl.hasMoreElements())
					return false;
//System.out.println("new control lock ");

				control = (Control) outerControl.nextElement();

				List granted = control.getGranted();
				if (granted != null)
					grantedList = granted.listIterator();


				List waiting = control.getWaiting();
				if (waiting != null)
					waitingList = waiting.listIterator();

				nextLock = control.getFirstGrant();
				if (nextLock == null) {

					nextLock = getNextLock(control);
				}
				
			} else {
				nextLock = getNextLock(control);
			}


			if (nextLock != null)
				return true;

			control = null;
		}
	}

	private Latch getNextLock(Control control) {
		Latch lock = null;
//System.out.println("next lock ");
		if (grantedList != null) {
			if (grantedList.hasNext()) {
				lock = (Lock) grantedList.next();
			}
			else
				grantedList = null;
		}

		if (lock == null) {
			if (waitingList != null) {
				if (waitingList.hasNext()) {
					lock = (Lock) waitingList.next();
				}
				else
					waitingList = null;
			}
		}

		return lock;
	}

	public Object nextElement() {

		if (!hasMoreElements())
			throw new NoSuchElementException();

		Latch ret = nextLock;

		nextLock = null;
		return ret;
	}
}



