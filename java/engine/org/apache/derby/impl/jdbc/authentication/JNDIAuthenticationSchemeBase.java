/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.impl.jdbc.authentication
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.error.StandardException;

import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.store.access.TransactionController;

import org.apache.derby.iapi.jdbc.AuthenticationService;
import org.apache.derby.authentication.UserAuthenticator;

import org.apache.derby.iapi.services.sanity.SanityManager;
import org.apache.derby.iapi.reference.SQLState;
import org.apache.derby.iapi.error.ExceptionSeverity;
import org.apache.derby.iapi.reference.MessageId;
import org.apache.derby.iapi.services.i18n.MessageService;

import java.util.Properties;
import java.util.Enumeration;
import java.sql.SQLException;

/**
 * This is the base JNDI authentication scheme class.
 *
 * The generic environment JNDI properties for the selected JNDI
 * scheme are retrieved here so that the user can set JNDI properties
 * at the database or system level.
 *
 * @see org.apache.derby.authentication.UserAuthenticator 
 *
 */

public abstract class JNDIAuthenticationSchemeBase implements UserAuthenticator
{
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;
	protected  final JNDIAuthenticationService authenticationService;
	protected String providerURL;

	private AccessFactory store;
	protected Properties initDirContextEnv;

	//
	// Constructor
	//
	// We get passed some Users properties if the authentication service
	// could not set them as part of System properties.
	//
	public JNDIAuthenticationSchemeBase(JNDIAuthenticationService as, Properties dbProperties) {

			this.authenticationService = as;

			//
			// Let's initialize the Directory Context environment based on
			// generic JNDI properties. Each JNDI scheme can then add its
			// specific scheme properties on top of it.
			//
			setInitDirContextEnv(dbProperties);

			// Specify the ones for this scheme if not already specified
			this.setJNDIProviderProperties();
	}


	/**
	 * To be OVERRIDEN by subclasses. This basically tests and sets
	 * default/expected JNDI properties for the JNDI provider scheme.
	 *
	 **/
	abstract protected void setJNDIProviderProperties();

	/**
	 * Construct the initial JNDI directory context environment Properties
	 * object. We retrieve JNDI environment properties that the user may
	 * have set at the database level.
	 *
	 **/
	private void setInitDirContextEnv(Properties dbProps) {

		//
		// We retrieve JNDI properties set at the database level	
		// if any.
		//
		initDirContextEnv = new Properties();

		for (Enumeration keys = dbProps.propertyNames(); keys.hasMoreElements(); ) {

			String key = (String) keys.nextElement();

			if (key.startsWith("java.naming.")) {
				initDirContextEnv.put(key, dbProps.getProperty(key));
			}
		}
	}
	
	protected static final SQLException getLoginSQLException(Exception e) {

		String text = MessageService.getTextMessage(SQLState.LOGIN_FAILED, e);

		SQLException sqle = new SQLException(
							text, SQLState.LOGIN_FAILED, ExceptionSeverity.SESSION_SEVERITY);

		return sqle;
	}

}
