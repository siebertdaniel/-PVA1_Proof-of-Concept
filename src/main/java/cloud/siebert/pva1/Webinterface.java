package cloud.siebert.pva1;

import cloud.siebert.pva1.exception.DatabaseErrorException;
import cloud.siebert.pva1.exception.UserCheckException;
import cloud.siebert.pva1.exception.WrongPasswordException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//./mvnw spring-boot:run
@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")

public class Webinterface {
    @CrossOrigin(origins = "*")
    public static void main(String[] args) {
        // This is needed so the webinterfaces starts correctly
        SpringApplication.run(Webinterface.class, args);
    }

    /**
     * This method is used to create an POST interface to take request from a frontend.
     * The payload is further used to encrypt the password and to write it into a database.
     * The interface then returns a ReposeEntity and, if successful, the user_id of the created user.
     *
     * @param payload This is the payload the interfaces receives via the POST interface.
     * @return The method will always return an ReposeEntity but there are three possible outcomes.
     * 1. The user already exists, then the ResponseEntity will have the https statuscode 418 and a corresponding message.
     * 2. The user creation failed, then the ResponseEntity will have http statuscode 400 and a corresponding message.
     * 3. The user creation was successful, then the ResponseEntity ill have http statuscode 200 and the user_id attached in JSON form.
     */
    @PostMapping(value = "/writeUser")
    public ResponseEntity<Object> writeUser(@RequestBody String payload) throws Exception {
        //System.out.println(payload);
        // Convert the payload string into and JSON object for further operation
        JsonObject convertedObject = new Gson().fromJson(payload, JsonObject.class);
        // Fill two variables with the contents of that json object
        String user_mail = String.valueOf(convertedObject.get("username"));
        String user_password = String.valueOf(convertedObject.get("password"));
        // Trimm the " from both strings
        user_mail = user_mail.substring(1, user_mail.length() - 1);
        user_password = user_password.substring(1, user_password.length() - 1);
        // Initialize the return variable
        String user_id;
        // Try the main methods to write those values into the database
        try {
            user_id = Main.writeUser(user_mail, user_password);
            // catch errors if they happen and send corresponding error codes
        } catch (UserCheckException e) {
            // If the user alredy exists use http statuscode 418
            return new ResponseEntity<>("User already exists.", HttpStatus.I_AM_A_TEAPOT);
        } catch (DatabaseErrorException e) {
            // If the writing operation failed use http statuscode 400
            return new ResponseEntity<>("Operation failed.", HttpStatus.BAD_REQUEST);
        }
        // If successful create a String Hashmap
        Map<String, String> data = new HashMap<>();
        // Add the return value into that Hashtable
        data.put("response", user_id);
        // Return the Hashtable with http statuscode 200
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * This method is used to create an POST interface to take request from a frontend.
     * The payload is further used to compare it with values from the database
     * The interface then returns a ReposeEntity and, if successful, the user_id of the user.
     *
     * @param payload This is the payload the interfaces receives via the POST interface.
     * @return The method will always return an ReposeEntity but there are three possible outcomes.
     * 1. The user does not exist, then the ResponseEntity will have the https statuscode 418 and a corresponding message.
     * 2. The password given by the user was wrong, then the ResponseEntity will have http statuscode 400 and a corresponding message.
     * 3. The password given by the user was correct, then the ResponseEntity ill have http statuscode 200 and the user_id attached in JSON form.
     */
    @PostMapping(value = "/loginUser")
    public ResponseEntity<Object> loginUser(@RequestBody String payload) throws SQLException, IOException {
        //System.out.println(payload);
        // Convert the payload string into and JSON object for further operation
        JsonObject convertedObject = new Gson().fromJson(payload, JsonObject.class);
        // Fill two variables with the contents of that json object
        String user_mail = String.valueOf(convertedObject.get("username"));
        String user_password = String.valueOf(convertedObject.get("password"));
        // Trimm the " from both strings
        user_mail = user_mail.substring(1, user_mail.length() - 1);
        user_password = user_password.substring(1, user_password.length() - 1);
        // Initialize the return variable
        String user_id;
        // Try the main methods to compare the login info with values from the database
        try {
            user_id = (Main.loginUser(user_mail, user_password));
            // catch errors if they happen and send corresponding error codes
        } catch (UserCheckException e) {
            // If the user doesn't exist use http statuscode 418
            return new ResponseEntity<>("User does not exists.", HttpStatus.I_AM_A_TEAPOT);
        } catch (WrongPasswordException e) {
            // If the password is wrong use http statuscode 400
            return new ResponseEntity<>("Wrong Password", HttpStatus.BAD_REQUEST);
        }
        // If successful create a String Hashmap
        Map<String, String> data = new HashMap<>();
        // Add the return value into that Hashtable
        data.put("response", user_id);
        // Return the Hashtable with http statuscode 200
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    // This method is only used for testing
    @PostMapping(value = "/test")
    public ResponseEntity<Object> test(@RequestBody String payload)  {
        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }
}