import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PrinterManagerServer extends PrinterManager {
    public static void main(String args[]) {
        try {
            //System.setProperty("java.rmi.server.hostname","192.168.1.2");
            PrinterManager obj = new PrinterManager();
            IPrinterManager stub = (IPrinterManager) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("IPrinterManager", stub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
