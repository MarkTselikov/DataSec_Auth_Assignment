public class HashingTest {
    public static void main(String[] args) {
        String password = "pass";

        String hashed = null;
        try {
            hashed = HashingUtil.getSaltedHash(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Plain Text Password: ");
        System.out.println(password);
        System.out.println("\nHashed Password: ");
        System.out.println(hashed);
    }
}
