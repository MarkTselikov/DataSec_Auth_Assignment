import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPrinterManager extends Remote {
    String print(String filename, String printer, String token) throws RemoteException;
    String queue(String printer, String token) throws RemoteException;
    String topQueue(String printer, int job, String token) throws RemoteException;
    String start(String token) throws RemoteException;
    String stop(String token) throws RemoteException;
    String restart(String token) throws RemoteException;
    String status(String printer, String token) throws RemoteException;
    String readConfig(String parameter, String token) throws RemoteException;
    String setConfig(String parameter, String token, String value) throws RemoteException;
    String login(String username, String password) throws RemoteException;
    void exit(String token) throws RemoteException;
}
