import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.util.*;



public class AccessAuthorization {

    // files used for RBAC: who has which roles, and the authorizations for each role
    private static final String ROLES_FILE_PATH = "roles.txt";
    private static final String ACCESS_FILE_PATH = "role_authorization.txt";

    private String status;

    public static Map<String,List<String>> roleFunctions;
    public static Map<String,List<String>> roleUsers;

    //function that return true if the user username can execute the function, false otherwise.
    public static boolean verifyAccess(String username, String function) {

        roleFunctions = new HashMap<String,List<String>>();
        roleUsers = new HashMap<String,List<String>>();

        String userRole = findUserRole(username, roleUsers);

        List<String> functionAccess = findFunctionsForRole(userRole, roleFunctions);

        if (functionAccess.contains(function)) {

            return true;
        }
        else {

            return false;
        }

        
    }

    // returns the first key where user is in the list; Note: with that configuration, one user can only have one role. Could be extended
    // fixed
    public static String findUserRole(String username, Map<String, List<String >> dict) {

        dict = parseFile(ROLES_FILE_PATH, dict);

        String userRole = "";

        for (String key: dict.keySet()) {
            List<String> usersList = dict.get(key);
            if (usersList.contains(username)){
                userRole = key; // 
            }
        }
    return userRole;
    }

    // will parse the RBAC files and return a dictionnary { key: [value1, value2], ...} where key is
    // the role (String) and value1, ... is either a username or a function (both strings)
    // fixed
    public static Map<String, List<String >>  parseFile(String fileToParse, Map<String, List<String >>  dict) {

        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new FileReader(fileToParse));
            String line = bufReader.readLine();

            while (line != null) {
                String[] splitLine = line.split(","); // list of element on the line
                int len = splitLine[1].length(); // lenght of the second string (table - like string)
                String[] splitTable = splitLine[1].substring(1,len-2) .split(";"); //removes the brackets from the string, then splits
                dict.put(splitLine[0], Arrays.asList(splitTable));
                System.out.println(splitTable);
                line = bufReader.readLine();
            }

            bufReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dict;
    }

    //returns a list the functions that the given role can access
    //fixed
    public static List<String>findFunctionsForRole(String role, Map<String, List<String >> roleFunctions) {
        Map<String, List<String >> rightsJson = parseFile(ACCESS_FILE_PATH, roleFunctions);
        List<String> listFunctions = rightsJson.get(role);
        return listFunctions;
    } 
    
}
