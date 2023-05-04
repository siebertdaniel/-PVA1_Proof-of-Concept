package cloud.siebert.pva1;

import cloud.siebert.pva1.exception.DatabaseErrorException;
import cloud.siebert.pva1.exception.UserCheckException;
import cloud.siebert.pva1.exception.WrongPasswordException;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // main was mostly used to create initial users and can technically be removed.
        //System.out.println("Hello world!");
    }

    /**
     * This is the main method to register a new user by writing the email and password into the database.
     * The password will be hashed and encrypted before being written.
     *
     * @param userEmail This is email by the user which will be written into the database.
     * @param inputPw   input_pw is the cleartext password which will be hashed, encrypted and then written into the database.
     * @return This method returns the user_id after it was written into the database.
     */
    public static String writeUser(String userEmail, String inputPw) throws IOException, SQLException, UserCheckException, DatabaseErrorException {
        // Step 0: Check if the email is already in use, if yes an error will be thrown
        String userCheck = Database.getUserIDByEmail(userEmail);
        if (userCheck != null) {
            throw new UserCheckException();
        }
        // Step 1: Create Hash from cleartext password
        String userPWHash = HashGenerator.createPasswordHash(inputPw);
        // Step 2: Use global pepper and encrypt password with AES-256
        String userPWString = HashGenerator.encryptAES256(userPWHash);
        // Step 3: Write into database, if that fails an error will be thrown
        if (Database.writeUser(userEmail, userPWString)) {
            return Database.getUserIDByEmail(userEmail);
        } else {
            throw new DatabaseErrorException();
        }
    }

    /**
     * This is the main method to log in a user by checking if the user exists and then comparing the given and saved password.
     * before the password can be compared with the input password it needs to be encrypted and since a hash can't be reversed to cleartext the input password will be hashed.
     * After that the two hashes can be compared.
     *
     * @param userEmail This is email given by the user.
     * @param userInput  userInput is the cleartext password which was given by the user. It will be hashed and compared to the hash saved in the database.
     * @return This method returns either the user_id when the hashes match or will throw an error and the login will be denied.
     */
    public static String loginUser(String userEmail, String userInput) throws SQLException, IOException, UserCheckException, WrongPasswordException {
        // Step 0: Check if user email exists, if not an error will be thrown
        String userCheck = Database.getUserIDByEmail(userEmail);
        if (userCheck == null) {
            throw new UserCheckException();
        }
        // Step 1: Get the encrypted password string from the database
        String userPWString = Database.getUserPasswordStringByEmail(userEmail);
        // Step 2: Decrypt the password to get the password hash
        String userPWHash = HashGenerator.decryptAES256(userPWString);
        // Step 3: Hash the user provided password and compare it with decrypted password hash
        if (HashGenerator.compareHash(userInput, userPWHash)) {
            // If the compare function return true then return the user_id
            return Database.getUserIDByEmail(userEmail);
        } else {
            // If not throw an error, signaling the user exists but the password was wrong
            throw new WrongPasswordException();
        }
    }
}