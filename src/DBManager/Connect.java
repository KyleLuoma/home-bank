/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBManager;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Administrator
 */
public class Connect {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    }
        
    private Connection dbConnection = null;
    
    public Connection getConnection() {
        
        return dbConnection;
        
    }
    
    public void connect() {
        
        this.connectToDB();
        
    }
    
    private Connection connectToDB() {
        //connects to database
        Connection dbConnection = null;
        String stringURL = "jdbc:derby:HomeBankDB;user=parent;password=parent";
        try {
            dbConnection = DriverManager.getConnection(stringURL);
            System.out.println("Connected to database");
        } catch (SQLException sqle) {
            //sqle.printStackTrace();
            System.out.println("Unable to connect to the database. Check to see if it's open.");
            }
        return dbConnection;
    }

    private void loadDBDriver() {
        //Load JDBC driver
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            System.out.println("DB driver load complete");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    private void setDBSystemDir() {
        // Decide on the db system directory: <userhome>/.bankdb/
        String userHomeDir = System.getProperty("user.home", ".");
        String systemDir = userHomeDir + "/.bankdb";

        // Create a db system directory
        new File(systemDir).mkdir();

        // Set the db system directory
        System.setProperty("derby.system.home", systemDir);

        System.out.println("system directory set");
        System.out.println("User Home Directory is " + userHomeDir);
        System.out.println("System Directory is " + systemDir);
    }

    private void createDB() {
        //create the database
        Connection dbConnection = null;
        String stringURL = "jdbc:derby:HomeBankDB;create=true";
        try {
            dbConnection = DriverManager.getConnection(stringURL);
            System.out.println("Database created");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createStandardTables(String accountName, Connection dbConnection) {
        // checks for existence of required tables and creates new tables if none exist.
        // This method is currently a test bed for the javadb API. Needs to be 
        // reconfigured for it's actual purpose.
              
        String createUserTableQuery
                = "CREATE table " + accountName + ".USERS (\n"
                + "ID          INTEGER NOT NULL, \n"
                + "LASTNAME    VARCHAR(30), \n"
                + "FIRSTNAME   VARCHAR(30), \n"
                + "ROLE        VARCHAR(10), \n"
                + "LEVEL       INTEGER )";
        
        String createAccountTableQuery
                = "CREATE table " + accountName + ".ACCOUNTS (\n"
                + "ID          INTEGER NOT NULL, \n"
                + "HOLDERID    INTEGER NOT NULL, \n"
                + "MGRID       INTEGER NOT NULL, \n"
                + "BALANCE     FLOAT, \n"
                + "DATECREATED DATE \n"
                + ")";

        String createTransactionTableQuery 
                = "CREATE table " + accountName + ".TRANSACTIONS (\n"
                + "ID               INTEGER NOT NULL, \n"
                + "FROMACCOUNTID    INTEGER NOT NULL, \n"
                + "TOACCOUNTID      INTEGER NOT NULL, \n"
                + "AMOUNT           FLOAT NOT NULL,   \n"
                + "DATE             DATE NOT NULL,    \n"
                + "TIME             TIME NOT NULL    \n"
                + ")";
        
        Statement statement = null;
        
        //boolean test = checkTable("USER", dbConnection, accountName);
        
        //System.out.println("check table works is " + test);
        
        try {
            
            if (checkTable("USERS", dbConnection, accountName) == false) {

                System.out.println("Creating user table");
                statement = dbConnection.createStatement();
                statement.execute(createUserTableQuery);
            
            } else {System.out.println("Did not create user table, already exists");}
        
        } catch (SQLException ut) {
            
            System.out.println("There was a problem creating the users table.");
            System.out.println(ut);
            
        }
            
        try {
        
            if (checkTable("ACCOUNTS", dbConnection, accountName) == false) {
                
                System.out.println("Creating account table");
                statement = dbConnection.createStatement();
                statement.execute(createAccountTableQuery);
                
            } else {System.out.println("Did not create account table, already exists");}
            
        } catch (SQLException at) {
            
            System.out.println("There was a problem creating the accounts table");
            System.out.println(at);
            
        }
            
        try {
        
            if (checkTable("TRANSACTIONS", dbConnection, accountName) == false) {
                
                System.out.println("Creating transactions table");
                statement = dbConnection.createStatement();
                statement.execute(createTransactionTableQuery);
                
            } else {System.out.println("Did not create transactions table, already exists");}
            
        } catch (SQLException tt) {

            System.out.println("There was a problem creating the transactions table");
            System.out.println(tt);

        }
    }
    
    private boolean checkTable(String tableName, Connection connection, String accountName) {
        // Checks for the existence of a table within a database and returns a boolean
        
        boolean tableExists;
                
        try {
            
            Statement statement = connection.createStatement();
            DatabaseMetaData tableCheck = connection.getMetaData();
            ResultSet tableVerify = tableCheck.getTables(null, accountName, tableName, null);
            tableVerify.next();            
            tableExists = tableName.equals(tableVerify.getString("TABLE_NAME"));
            
        } catch (SQLException ctFail) {
            
            System.out.println("There was no table named " + tableName);
            //System.out.println(ctFail);
            tableExists = false;
                        
        }
        
        return tableExists;
        
    }
    
}
