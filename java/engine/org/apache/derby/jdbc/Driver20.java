/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derby.jdbc
   (C) Copyright IBM Corp. 1998, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derby.jdbc;

import org.apache.derby.iapi.reference.Attribute;
import org.apache.derby.iapi.reference.MessageId;
import org.apache.derby.iapi.reference.Property;
import org.apache.derby.iapi.reference.SQLState;

import org.apache.derby.impl.jdbc.EmbedConnection;

import org.apache.derby.iapi.services.sanity.SanityManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.iapi.jdbc.BrokeredConnectionControl;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.io.FormatableProperties;

import org.apache.derby.impl.jdbc.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;

import java.util.Properties;

/**
	This class extends the local JDBC driver in order to determine at JBMS
	boot-up if the JVM that runs us does support JDBC 2.0. If it is the case
	then we will load the appropriate class(es) that have JDBC 2.0 new public
	methods and sql types.
*/

public class Driver20 extends Driver169 implements Driver {

	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1998_2004;

	private static final String[] BOOLEAN_CHOICES = {"false", "true"};

	private Class  antiGCDriverManager;

	/*
	**	Methods from ModuleControl
	*/

	public void boot(boolean create, Properties properties) throws StandardException {

		super.boot(create, properties);

		// Register with the driver manager
		try {			
			DriverManager.registerDriver(this);

			// hold onto the driver manager to avoid it being garbage collected.
			// make sure the class is loaded by using .class
			antiGCDriverManager = java.sql.DriverManager.class;

		} catch (SQLException e) {
			throw StandardException.newException(SQLState.JDBC_DRIVER_REGISTER, e);
		}
	}

	public void stop() {

		super.stop();

		try {
			DriverManager.deregisterDriver(this);
		} catch (SQLException sqle) {
			// just do nothing
		}
	}

	/**
 	 * Get a new nested connection.
	 *
	 * @param conn	The EmbedConnection.
	 *
	 * @return A nested connection object.
	 *
	 */
	public Connection getNewNestedConnection(EmbedConnection conn)
	{
		if (SanityManager.DEBUG)
		{
			SanityManager.ASSERT(conn instanceof EmbedConnection20,
				"conn expected to be instanceof EmbedConnection20");
		}
		return new EmbedConnection20(conn);
	}

	/*
		Methods to be overloaded in sub-implementations such as
		a tracing driver.
	 */
	public EmbedConnection getNewEmbedConnection(String url, Properties info)
		 throws SQLException 
	{
		// make a new local connection with a new transaction resource
		return new EmbedConnection20(this, url, info);
	}

	/**
	 	@exception SQLException if fails to create statement
	 */
	public java.sql.PreparedStatement 
	newEmbedPreparedStatement (EmbedConnection conn, 
							   String stmt, 
							   boolean forMetaData,
							   int resultSetType,
							   int resultSetConcurrency,
							   int resultSetHoldability,
							   int autoGeneratedKeys,
							   int[] columnIndexes,
							   String[] columnNames)
		 throws SQLException
	{
		return new EmbedPreparedStatement20(conn, stmt, forMetaData,
										  resultSetType, resultSetConcurrency, resultSetHoldability,
										  autoGeneratedKeys, columnIndexes, columnNames);
	}

	/**
	 	@exception SQLException if fails to create statement
	 */
	public java.sql.CallableStatement newEmbedCallableStatement(
				EmbedConnection conn,
				String stmt, 
				int resultSetType,
				int resultSetConcurrency,
				int resultSetHoldability)
		throws SQLException
	{
		return new EmbedCallableStatement20(conn, stmt,
										  resultSetType, resultSetConcurrency, resultSetHoldability);
	}
  
	public org.apache.derby.impl.jdbc.EmbedResultSet 
	newEmbedResultSet(EmbedConnection conn, ResultSet results, boolean forMetaData, org.apache.derby.impl.jdbc.EmbedStatement statement, boolean isAtomic)
	{
		return new EmbedResultSet20(conn, results, forMetaData, statement,
								 isAtomic); 
	}
	public BrokeredConnection newBrokeredConnection(BrokeredConnectionControl control) {

		return new BrokeredConnection(control);
	}
    /**
     * <p>The getPropertyInfo method is intended to allow a generic GUI tool to 
     * discover what properties it should prompt a human for in order to get 
     * enough information to connect to a database.  Note that depending on
     * the values the human has supplied so far, additional values may become
     * necessary, so it may be necessary to iterate though several calls
     * to getPropertyInfo.
     *
     * @param url The URL of the database to connect to.
     * @param info A proposed list of tag/value pairs that will be sent on
     *          connect open.
     * @return An array of DriverPropertyInfo objects describing possible
     *          properties.  This array may be an empty array if no properties
     *          are required.
     * @exception SQLException if a database-access error occurs.
     */
	public  DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {

		// RESOLVE other properties should be added into this method in the future ... 

        if (info != null) {
			if (Boolean.valueOf(info.getProperty(Attribute.SHUTDOWN_ATTR)).booleanValue()) {
	
				// no other options possible when shutdown is set to be true
				return new DriverPropertyInfo[0];
			}
		}

		// at this point we have databaseName, 

		String dbname = Driver169.getDatabaseName(url, info);

		// convert the ;name=value attributes in the URL into
		// properties.
		FormatableProperties finfo = getAttributes(url, info);
		info = null; // ensure we don't use this reference directly again.
		boolean encryptDB = Boolean.valueOf(finfo.getProperty(Attribute.DATA_ENCRYPTION)).booleanValue();		
		String encryptpassword = finfo.getProperty(Attribute.BOOT_PASSWORD);

		if (dbname.length() == 0 || (encryptDB = true && encryptpassword == null)) {

			// with no database name we can have shutdown or a database name

			// In future, if any new attribute info needs to be included in this
			// method, it just has to be added to either string or boolean or secret array
			// depending on whether it accepts string or boolean or secret(ie passwords) value. 

			String[][] connStringAttributes = {
				{Attribute.DBNAME_ATTR, MessageId.CONN_DATABASE_IDENTITY},
				{Attribute.CRYPTO_PROVIDER, MessageId.CONN_CRYPTO_PROVIDER},
				{Attribute.CRYPTO_ALGORITHM, MessageId.CONN_CRYPTO_ALGORITHM},
				{Attribute.CRYPTO_KEY_LENGTH, MessageId.CONN_CRYPTO_KEY_LENGTH},
				{Attribute.CRYPTO_EXTERNAL_KEY, MessageId.CONN_CRYPTO_EXTERNAL_KEY},
				{Attribute.TERRITORY, MessageId.CONN_LOCALE},
				{Attribute.USERNAME_ATTR, MessageId.CONN_USERNAME_ATTR},
				{Attribute.LOG_DEVICE, MessageId.CONN_LOG_DEVICE},
				{Attribute.ROLL_FORWARD_RECOVERY_FROM, MessageId.CONN_ROLL_FORWARD_RECOVERY_FROM},
				{Attribute.CREATE_FROM, MessageId.CONN_CREATE_FROM},
				{Attribute.RESTORE_FROM, MessageId.CONN_RESTORE_FROM},
			};

			String[][] connBooleanAttributes = {
				{Attribute.SHUTDOWN_ATTR, MessageId.CONN_SHUT_DOWN_CLOUDSCAPE},
				{Attribute.CREATE_ATTR, MessageId.CONN_CREATE_DATABASE},
				{Attribute.DATA_ENCRYPTION, MessageId.CONN_DATA_ENCRYPTION},
				{Attribute.UPGRADE_ATTR, MessageId.CONN_UPGRADE_DATABASE},
				};

			String[][] connStringSecretAttributes = {
				{Attribute.BOOT_PASSWORD, MessageId.CONN_BOOT_PASSWORD},
				{Attribute.PASSWORD_ATTR, MessageId.CONN_PASSWORD_ATTR},
				};

			
			DriverPropertyInfo[] optionsNoDB = new 	DriverPropertyInfo[connStringAttributes.length+
																	  connBooleanAttributes.length+
			                                                          connStringSecretAttributes.length];
			
			int attrIndex = 0;
			for( int i = 0; i < connStringAttributes.length; i++, attrIndex++ )
			{
				optionsNoDB[attrIndex] = new DriverPropertyInfo(connStringAttributes[i][0], 
									  finfo.getProperty(connStringAttributes[i][0]));
				optionsNoDB[attrIndex].description = MessageService.getTextMessage(connStringAttributes[i][1]);
			}

			optionsNoDB[0].choices = Monitor.getMonitor().getServiceList(Property.DATABASE_MODULE);
			// since database name is not stored in FormatableProperties, we
			// assign here explicitly
			optionsNoDB[0].value = dbname;

			for( int i = 0; i < connStringSecretAttributes.length; i++, attrIndex++ )
			{
				optionsNoDB[attrIndex] = new DriverPropertyInfo(connStringSecretAttributes[i][0], 
									  (finfo.getProperty(connStringSecretAttributes[i][0]) == null? "" : "****"));
				optionsNoDB[attrIndex].description = MessageService.getTextMessage(connStringSecretAttributes[i][1]);
			}

			for( int i = 0; i < connBooleanAttributes.length; i++, attrIndex++ )
			{
				optionsNoDB[attrIndex] = new DriverPropertyInfo(connBooleanAttributes[i][0], 
           		    Boolean.valueOf(finfo == null? "" : finfo.getProperty(connBooleanAttributes[i][0])).toString());
				optionsNoDB[attrIndex].description = MessageService.getTextMessage(connBooleanAttributes[i][1]);
				optionsNoDB[attrIndex].choices = BOOLEAN_CHOICES;				
			}

			return optionsNoDB;
		}

		return new DriverPropertyInfo[0];
	}
}
