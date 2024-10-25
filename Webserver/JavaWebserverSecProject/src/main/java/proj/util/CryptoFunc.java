package proj.util;
import java.security.MessageDigest;

/**
 * This class contains functions for cryptographic operations.
 */
public class CryptoFunc {

  public static String hashSHA256(String input) throws Exception {
      //Digests the input string using SHA-256
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    //Obtains the hash of the input string
    byte[] hash = digest.digest(input.getBytes("UTF-8"));

    //Converts the hash to a hexadecimal string
    StringBuilder stringBuilder = new StringBuilder();
    //Byte Array to string conversion
    for (int i = 0; i < hash.length; i++) {
      String hex = Integer.toHexString(0xff & hash[i]);
      if (hex.length() == 1) {
          stringBuilder.append('0');
      }
        stringBuilder.append(hex);
    }

    return stringBuilder.toString();
  }

}
