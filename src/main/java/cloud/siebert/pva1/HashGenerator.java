package cloud.siebert.pva1;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;


public class HashGenerator {
    /**
     * This method takes a cleartext password, hashes it with Argon2 and returns the hash.
     * The return will not only include the hash but also the parameters and a generated salt by Argon2.
     * Example: $argon2id$v=19$m=16,t=2,p=1$MUlacHJteGRndWdhUUJ3Nw$FCnMOcSIjs0AuXqrFuFOJg
     * Explained: $Variant$Parameters$GeneratedSalt$Hash
     *
     * @param inputPassword This is the password which will be hashed.
     * @return The return is the complete output from Argon2, which is explained above.
     */
    public static String createPasswordHash(String inputPassword) {
        // Create an instance of Argon2 with parameters.
        // Those parameters are the variant Argon2id, the salt length, set to 16, and the hash length set 32
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
        // The input password is taken and converted into a char array
        char[] password = (inputPassword).toCharArray();
        // This char array will then be used in the hash function and then returned as a String
        return argon2.hash(1, 262144, 8, password);
    }

    /**
     * This method takes a cleartext password, hashes it with Argon2 compares it with another given hash.
     *
     * @param hashInput    This is the password which will be hashed and compared.
     * @param hashDatabase This is the password hash which was given and compared.
     * @return The method will either return true if the two hashes match or false when they don't match.
     */
    public static boolean compareHash(String hashInput, String hashDatabase) {
        // Create an instance of Argon2 with parameters.
        // Those parameters are the variant Argon2id, the salt length, set to 16, and the hash length set 32
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
        // Try to compare the passwords. This might throw an error when the library is not installed
        try {
            // Convert the input password to a char array and compare it to the stored hash.
            // This will either return true or false, one of which will be returned
            return argon2.verify(hashDatabase, hashInput.toCharArray());
            // If an error is thrown then false will be returned, but the library needs to be checked!
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
            // Lastly wipe the input hash, so it can't be used again by accident
        } finally {
            argon2.wipeArray(hashDatabase.toCharArray());
        }
    }

    /**
     * This method gets the key values from the properties file and then generates and returns a workable SecretKeySpec.
     *
     * @return The method will return a SecretKeySpec that will be used in AES-256 encryption and decryption.
     */
    public static SecretKeySpec getSecretKeyAES256() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        // Get the properties file to use values later
        //Properties encryptProps = new Properties(ReadProperties.readProperties("/opt/pva1/config/config.properties"));
        Properties encryptProps = new Properties(ReadProperties.readProperties("config/config.properties"));
        // Generate a new key with a specific key derivation function and algorithm
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        // Create a new KeySpec with the parameters got from the properties file
        // This is so the same key can be generated multiple times
        KeySpec spec = new PBEKeySpec(encryptProps.getProperty("AESSecretKey").toCharArray(), encryptProps.getProperty("AESSecretSalt").getBytes(), 65536, 256);
        // Generate the new key with the KeySpec
        SecretKey tmp = factory.generateSecret(spec);
        // And lastly encode it with AES and then return it
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * This method gets the IV (initialization vector) values from the properties file returns them.
     *
     * @return The method will return a IvParameterSpec that will be used in AES-256 encryption and decryption.
     */
    public static IvParameterSpec getSecretIVSpecAES256() throws IOException {
        // Get the properties file to use values later
        //Properties encryptProps = new Properties(ReadProperties.readProperties("/opt/pva1/config/config.properties"));
        Properties encryptProps = new Properties(ReadProperties.readProperties("config/config.properties"));
        // Get the String from the properties file, convert them into bytes and then write them into a byte array
        byte[] iv = encryptProps.getProperty("AESSecretIV").getBytes(StandardCharsets.UTF_8);
        // Return inform of a IvParameterSpec object filled with that byte array
        return new IvParameterSpec(iv);
    }

    /**
     * This method takes a String and encrypts it with AES256.
     * The values key and IV needed for that encryption will be generated by other methods.
     * Returned will be the encrypted string or null.
     *
     * @param encryptString This is the String that will be encrypted.
     * @return The method will return an AES-256 encrypted string or null, if the encryption fails.
     */
    public static String encryptAES256(String encryptString) {
        try {
            //Get the SecretKeySpec from the getSecretKeyAES256 method
            SecretKeySpec secretKey = HashGenerator.getSecretKeyAES256();
            //Get the IvParameterSpec from the getSecretIVSpecAES256 method
            IvParameterSpec ivSpec = HashGenerator.getSecretIVSpecAES256();
            // Create a new cipher with a specific algorithm
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // Initialize the cipher to encrypt with the SecretKeySpec and IvParameterSpec
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            // Finally convert the String to encrypt into bytes, encrypt that with the cipher, encode it with base64 and return it
            return Base64.getEncoder().encodeToString(cipher.doFinal(encryptString.getBytes(StandardCharsets.UTF_8)));
            // If that fails then throw an error and return null
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
            return null;
        }
    }

    /**
     * This method takes an AES-256 encrypted String and decrypts it.
     * The values key and IV needed for that decryption will be generated by other methods.
     * Returned will be the decrypted string or null.
     *
     * @param decryptString This is the that will be encrypted.
     * @return The method will return a decrypted string or null, if the decryption fails.
     */
    public static String decryptAES256(String decryptString) {
        try {
            //Get the SecretKeySpec from the getSecretKeyAES256 method
            SecretKeySpec secretKey = HashGenerator.getSecretKeyAES256();
            //Get the IvParameterSpec from the getSecretIVSpecAES256 method
            IvParameterSpec ivSpec = HashGenerator.getSecretIVSpecAES256();
            // Create a new cipher with a specific algorithm
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            // Initialize the cipher to decrypt with the SecretKeySpec and IvParameterSpec
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            // Finally decode the string with base64, then use the cipher to decrypt it and return the decrypted string
            return new String(cipher.doFinal(Base64.getDecoder().decode(decryptString)));
            // If that fails then throw an error and return null
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e);
            return null;
        }
    }
}