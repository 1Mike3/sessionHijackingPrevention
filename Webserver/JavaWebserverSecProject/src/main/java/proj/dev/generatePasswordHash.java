package proj.dev;
/**
 * Because the users are not added during runtime this method is not used
 * to generate a hash of the password which will be compared with the user input on login attempt.
 * Class and Method not used during runtime.
 */
public class generatePasswordHash {
    public static void main(String[] args){
        //hash the password
        try {
            System.out.println(proj.util.CryptoFunc.hashSHA256("insertPasswordHere"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
