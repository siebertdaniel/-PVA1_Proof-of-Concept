package cloud.siebert.pva1;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Database {
    // Initialize some variables that will be used by almost all methods
    static Statement stmt;
    static Connection DBConnection;
    static String sqlStatement;

    /**
     * This method can be used by other methods to connect to a database.
     * The parameters for the connection will be gotten from the properties file.
     *
     * @return The method will return Connection object that can be used to work with the connected database.
     */
    public static Connection connect() throws IOException {
        // Get the properties file to use values later
        //Properties databaseProps = new Properties(ReadProperties.readProperties("/opt/pva1/config/config.properties"));
        Properties databaseProps = new Properties(ReadProperties.readProperties("config/config.properties"));
        // Initialize a variable for the database connection
        Connection databaseConnection;
        // Build the connection string with variables from the properties file
        String DBConnectionString = "jdbc:postgresql://" + databaseProps.getProperty("databasehost") + ":" + databaseProps.getProperty("databaseport") + "/" + databaseProps.getProperty("databasename");
        // Try to connect to the databnase with the database connection string and the login data from the properties file
        try {
            Class.forName("org.postgresql.Driver");
            databaseConnection = DriverManager.getConnection(DBConnectionString, databaseProps.getProperty("username"), databaseProps.getProperty("password"));
            // If that doesn't work then throw an error and return null
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        // If it works then return the Connection Object
        //System.out.println("Opened database successfully");
        return databaseConnection;
    }

    /**
     * This method writes new user information into a given database.
     * The database connection will be gotten during the method from the Database.connect() method.
     *
     * @param userEmail   This is the email that will be written into the database.
     * @param userPwHash This is the password hash that will be written into the database.
     * @return The method will return either true or false, depending on if the writing process succeeded or not.
     */
    public static boolean writeUser(String userEmail, String userPwHash) throws IOException, SQLException {
        // Connect to the database via the Database.connect() method
        DBConnection = Database.connect();
        assert DBConnection != null;
        DBConnection.setAutoCommit(false);
        //Try to create a sql-statement and commit it
        try {
            // Create that statement
            stmt = DBConnection.createStatement();
            // First choose the method, then the table and lastly the colloums in which to insert the user information
            // After that use the given values and format them into the sqlStatement string
            sqlStatement = "INSERT INTO users (user_email, user_pw_hash) " + "VALUES ('%s', '%s');".formatted(userEmail, userPwHash);
            //System.out.println(sqlStatement);
            // Try the execution on the database
            stmt.executeUpdate(sqlStatement);
            DBConnection.commit();
            // If the execution did not work then throw an error and return false
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
            // Regardless of the outcome of that statement close the database connection
        } finally {
            stmt.close();
            DBConnection.close();
        }
        // If the execution finished without an error return true
        //System.out.println("User record created successfully");
        return true;
    }

    /**
     * This method gets the saved password hash by the user_email from the database.
     * The database connection will be gotten during the method from the Database.connect() method.
     *
     * @param userEmail This is the email by which the password string will be gotten.
     * @return The method will return a hashed and encrypted password from the database.
     */
    public static String getUserPasswordStringByEmail(String userEmail) throws SQLException, IOException {
        // Connect to the database via the Database.connect() method
        DBConnection = Database.connect();
        assert DBConnection != null;
        DBConnection.setAutoCommit(false);
        // Initialize the return value
        String queryPwHash = null;
        //Try to create a sql-statement and commit it
        try {
            // Create that statement
            stmt = DBConnection.createStatement();
            // First choose the method, then the table and lastly the colloums which should be selected
            // After that use the given values and format them into the sqlStatement string
            sqlStatement = "SELECT user_pw_hash FROM users " + "WHERE user_email='%s'".formatted(userEmail);
            //System.out.println(sqlStatement);
            // Try the execution on the database and get save the result into an ResultSet
            ResultSet queryResult = stmt.executeQuery(sqlStatement);
            // Go through the ResultSet and write the result into the return variable
            // Since we only expect one result we don't use an array for that.
            while (queryResult.next()) {
                queryPwHash = queryResult.getString("user_pw_hash");
            }
            // If the execution did not work then throw an error
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // Regardless of the outcome of that statement close the database connection
        } finally {
            stmt.close();
            DBConnection.close();
        }
        // If the execution finished without an error return the result
        return queryPwHash;
    }

    /**
     * This method gets the user_id by the user_email from the database.
     * The database connection will be gotten during the method from the Database.connect() method.
     *
     * @param userEmail This is the email by which the password string will be gotten.
     * @return The method will return a hashed and encrypted password from the database.
     */
    public static String getUserIDByEmail(String userEmail) throws SQLException, IOException {
        // Connect to the database via the Database.connect() method
        DBConnection = Database.connect();
        assert DBConnection != null;
        DBConnection.setAutoCommit(false);
        // Initialize the return value
        String queryID = null;
        try {
            // Create that statement
            stmt = DBConnection.createStatement();
            // First choose the method, then the table and lastly the colloums which should be selected
            // After that use the given values and format them into the sqlStatement string
            sqlStatement = "SELECT user_id FROM users " + "WHERE user_email='%s'".formatted(userEmail);
            //System.out.println(sqlStatement);
            // Try the execution on the database and get save the result into an ResultSet
            ResultSet queryResult = stmt.executeQuery(sqlStatement);
            // Go through the ResultSet and write the result into the return variable
            // Since we only expect one result we don't use an array for that.
            while (queryResult.next()) {
                queryID = queryResult.getString("user_id");
            }
            // If the execution did not work then throw an error
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // Regardless of the outcome of that statement close the database connection
        } finally {
            stmt.close();
            DBConnection.close();
        }
        // If the execution finished without an error return the result
        return queryID;
    }
}
