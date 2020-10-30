import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class test {
    public static void main(String[] args) {
        /*try {
            Random random = new Random();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 128);
            SecretKeyFactory f = null;
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = f.generateSecret(spec).getEncoded();
            Base64.Encoder enc = Base64.getEncoder();
            System.out.printf("salt: %s%n", enc.encodeToString(salt));
            System.out.printf("hash: %s%n", enc.encodeToString(hash));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            String password = "pass";
            String hashed = getSaltedHash(password);
            String hashed2 = getSaltedHash(password);

            System.out.println(password);
            System.out.println("Hashed #1");
            System.out.println(hashed);
            System.out.println();
            /*System.out.println("Hashed #2");
            System.out.println(hashed2);

            String[] saltAndHash1 = hashed.split("\\$");
            String[] saltAndHash2 = hashed2.split("\\$");

            System.out.println("\nAfter re-reading");
            System.out.println(saltAndHash1[1]);
            System.out.println(saltAndHash2[1]);*/

            System.out.println(check(password, hashed));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(10);
        // store the salt with the password
        return Base64.getEncoder().encodeToString(salt) + "$" + hash(password, salt);
    }

    public static boolean check(String password, String stored) throws Exception{
        String[] saltAndHash = stored.split("\\$");
        if (saltAndHash.length != 2) {
            throw new IllegalStateException(
                    "The stored password must have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, Base64.getDecoder().decode(saltAndHash[0]));

        //System.out.println("Salt + Hash: " + saltAndHash);
        System.out.println("Hash of Input: " + hashOfInput);
        System.out.println("Pass hash thing: " + saltAndHash[1]);
        return hashOfInput.equals(saltAndHash[1]);
    }


    private static String hash(String password, byte[] salt) throws Exception {
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, 20, 20));
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}

