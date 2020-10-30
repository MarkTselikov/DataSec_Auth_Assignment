import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.util.*;

public class PrinterManager implements IPrinterManager {

    private final String STATUS_STOPPED = "Stopped";
    private final String STATUS_RUNNING = "Running";

    private final String ROLE_ADMIN = "Admin";
    private final String ROLE_USER = "User";

    private final String LOGIN_FILE_PATH = "logins.txt";
    private final String LOG_FILE_PATH = "log.txt";

    private String status;

    private static List<String> loggedUserTokens;
    private static Map<String, String> roles;
    private Map<String, String> config;
    private List<PrinterJob> jobQueue;

    public PrinterManager() {
        status = STATUS_RUNNING;
        config = new HashMap<String, String>();
        jobQueue = new ArrayList<>();

        loggedUserTokens = new ArrayList<>();
        roles = new HashMap<String, String>();

        File log = new File(LOG_FILE_PATH);
        try {
            log.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String print(String filename, String printer, String token) throws RemoteException {
        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to print", token);
            return "Unauthenticated attempt to print";
        }

        String message = "Adding the file " + filename + "to the queue on the printer " + printer;

        appendToLog(message, token);
        jobQueue.add(new PrinterJob(filename, printer));
        return message;
    }

    @Override
    public String queue(String printer, String token) throws RemoteException {

        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to access the printing queue", token);
            return "Unauthenticated attempt to print";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Printing the job queue: \n");
        for (PrinterJob job: jobQueue) {
            sb.append(job).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String topQueue(String printer, int job, String token) throws RemoteException {

        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to access the printing queue", token);
            return "Unauthenticated attempt to access the printing queue";
        }

        try {
            PrinterJob j = jobQueue.get(job);
            jobQueue.remove(job);
            jobQueue.add(0, j);
        }
        catch (Exception e) {
            appendToLog("Attempt to prioritize non-existing job", token);
            return "That job does not exist.";
        }

        String message = "Priority given to the job " + job + " on the printer " + printer;

        appendToLog(message, token);
        return message;
    }

    @Override
    public String start(String token) throws RemoteException {

        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to start the server", token);
            return "Unauthenticated attempt to start the server";
        }

        if (!roles.get(token).equals(ROLE_ADMIN)) {
            appendToLog("Unauthorized attempt to start the server", token);
            return "You don't have rights to perform this operation.";
        }

        if (status.equals(STATUS_RUNNING)) {
            return "Server is already running.";
        }
        else {
            status = STATUS_RUNNING;
            appendToLog("Successfully started the server.", token);
            return "Server is now running.";
        }
    }

    @Override
    public String stop(String token) throws RemoteException {

        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to access the printing queue", token);
            return "Unauthenticated attempt to access the printing queue";
        }

        if (!roles.get(token).equals(ROLE_ADMIN)) {
            appendToLog("Unauthorized attempt to stop the server", token);
            return "You don't have rights to perform this operation.";
        }

        if (status.equals(STATUS_STOPPED)) {
            return "Server is already down.";
        }
        else if (status.equals(STATUS_RUNNING)) {
            status = STATUS_STOPPED;
            appendToLog("Successfully stopped the server.", token);
            return "Server has been stopped.";
        }
        return null;
    }

    @Override
    public String restart(String token) throws RemoteException {

        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to restart the server", token);
            return "Unauthenticated attempt to access the printing queue";
        }

        if (!roles.get(token).equals(ROLE_ADMIN)) {
            appendToLog("Unauthorized attempt to access the printing queue", token);
            return "You don't have rights to perform this operation.";
        }

        appendToLog("Attempt to restart the server.", token);
        stop(token);
        config = new HashMap<String, String>();
        jobQueue = new ArrayList<>();
        start(token);
        appendToLog("Server restarted successfully.", token);
        return "Server restarted successfully.";
    }

    @Override
    public String status(String printer, String token) throws RemoteException {
        appendToLog("Status of the server requested", token);
        return status;
    }

    @Override
    public String readConfig(String parameter, String token) throws RemoteException {
        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to access the server configuration", token);
            return "Unauthenticated attempt to access the server configuration";
        }

        appendToLog("Access to the server configuration.", token);
        return "The configuration for " + parameter + ": " + config.get(parameter);
    }

    @Override
    public String setConfig(String parameter, String value, String token) throws RemoteException {

        if (!isLoggedIn(token)) {
            appendToLog("Unauthenticated attempt to change the server configuration", token);
            return "You need to log in first.";
        }

        if (!roles.get(token).equals(ROLE_ADMIN)) {
            appendToLog("Unauthorized attempt to change the server configuration", token);
            return "You don't have rights to perform this operation.";
        }

        config.put(parameter, value);

        String message = "Parameter " + parameter + " set to " + value;
        appendToLog(message);
        return message;
    }


    // Method that reads the file with the user credentials, checks if the attempted login contains credentials
    // in the file and they are correct, and generates the user token.
    // The token is added to the loggedUserTokens list and to the roles dictionary together with the role of a user.
    public String login(String username, String password) {
        try {
            File file = new File(LOGIN_FILE_PATH);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                String[] line_vals = line.split(",");
                if (line_vals[0].equals(username) && HashingUtil.check(password, line_vals[1])) {
                    String token = generateToken();
                    loggedUserTokens.add(token);
                    roles.put(token, line_vals[2]);

                    appendToLog("Logged in user " + line_vals[0] + " with role " + line_vals[2]
                            + "\n(Token: " + token + ")");
                    return token;
                }
            }

            appendToLog("Unsuccessful attempt to log in using username  " + username);
        } catch (Exception e) {
            appendToLog("Problem while accessing the credentials file:\n" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void exit(String token) throws RemoteException {
        appendToLog("User " + token + " has logged off from the server");
        loggedUserTokens.remove(token);
    }

    // checking if the user token is registered as logged in
    private boolean isLoggedIn(String token) {
        for (String loggedToken: loggedUserTokens) {
            if(token.equals(loggedToken))
                return true;
        }
        return false;
    }

    // method that generates a random string that is used as a user token
    private String generateToken() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder(25);
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    // append to log when action performed by server (without token log)
    private void appendToLog(String message) {
        System.out.println(message);
        try {
            Files.write(Paths.get(LOG_FILE_PATH), ("[" + System.currentTimeMillis() + "] " + message + "\n").getBytes(),
                    StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.out.println("An error occurred while writing to the log.");
            e.printStackTrace();
        }
    }

    // append to log when action performed by a user (with token log)
    private void appendToLog(String message, String token) {
        System.out.println("(" + token + ") " + message);
        try {
            Files.write(Paths.get(LOG_FILE_PATH), ("[" + System.currentTimeMillis() + "] / Token " + token + "\n\t"
                    + message + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.out.println("An error occurred while writing to the log.");
            e.printStackTrace();
        }
    }
}
