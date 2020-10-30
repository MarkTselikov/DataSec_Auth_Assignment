import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    static String token;
    static IPrinterManager server;
    static BufferedReader reader;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            server = (IPrinterManager) registry.lookup("IPrinterManager");

            System.out.println("Welcome to the amazing printing server that is super secure! To continue." +
                    "you need to login first. Then you can select an operation from the menu by selecting a number" +
                    "with the option you need.\n");

            reader = new BufferedReader(new InputStreamReader(System.in));
            String loginToken = null;
            while (loginToken == null) {
                loginToken = loginToServer();
                if (loginToken == null)
                    System.out.println("Login not successful, check your user name and password and try again.");
            }

            token = loginToken;
            System.out.println("Successfully logged in to the server!");
            displayMenu();
            reader.close();
            server.exit(token);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static String loginToServer() {
        try {
            System.out.println("Enter your user name:");
            String username = reader.readLine().trim();

            String password;
            Console console = System.console();
            if (console != null)
                password = new String(System.console().readPassword("Enter your password:"));
            else {
                System.out.println("Enter your password:");
                password = reader.readLine().trim();
            }

            return server.login(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void displayMenu() {
        int choice = -1;
        while (choice != 0) {
            System.out.println("\nPress a number and press enter: ");
            System.out.println("1. Print a file on a printer");
            System.out.println("2. Get a queue on a specified printer");
            System.out.println("3. Prioritize a job on a specified printer");
            System.out.println("4. Read a server configuration");
            System.out.println("5. Set server configuration (requires admin rights)");
            System.out.println("6. Start the server (requires admin rights)");
            System.out.println("7. Stop the server (requires admin rights)");
            System.out.println("8. Restart the server (requires admin rights)");
            System.out.println("0. Exit");

            choice = getUserInput();

            switch (choice) {
                case 1:
                    print();
                    break;
                case 2:
                    queue();
                    break;
                case 3:
                    topQueue();
                    break;
                case 4:
                    getConfig();
                    break;
                case 5:
                    setConfig();
                    break;
                case 6:
                    try {
                        System.out.println(server.start(token));
                        break;
                    }
                    catch (RemoteException e) { e.printStackTrace(); }
                case 7:
                    try {
                        System.out.println(server.stop(token));
                        break;
                    }
                    catch (RemoteException e) { e.printStackTrace(); }
                case 8:
                    try {
                        System.out.println(server.restart(token));
                        break;
                    }
                    catch (RemoteException e) { e.printStackTrace(); }
                case 0:
                    break;
                default:
                    choice = -1;
                    break;
            }
        }
    }

    static void print() {
        try {
            System.out.println("Enter the file name: ");
            String filename = reader.readLine().trim();

            System.out.println("Enter the printer: ");
            String printer = reader.readLine().trim();

            System.out.println("Sending the job to the server...");
            System.out.println(server.print(filename, printer, token));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void queue() {
        try {
            System.out.println("Enter the printer to get the queue: ");
            String printer = reader.readLine().trim();

            System.out.println(server.queue(printer, token));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void topQueue() {
        try {
            System.out.println("Enter the printer: ");
            String printer = reader.readLine().trim();

            System.out.println("Enter the job number: ");
            int jobNumber = Integer.parseInt(reader.readLine().trim());

            System.out.println(server.topQueue(printer, jobNumber, token));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void getConfig() {
        try {
            System.out.println("Enter the configuration parameter to read: ");
            String param = reader.readLine().trim();

            System.out.println(server.readConfig(param, token));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setConfig() {
        try {
            System.out.println("Enter the configuration parameter name: ");
            String param = reader.readLine().trim();

            System.out.println("Enter the parameter value: ");
            String val = reader.readLine().trim();

            System.out.println(server.setConfig(param, val, token));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int getUserInput() {
        try {
            return Integer.parseInt(reader.readLine().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
