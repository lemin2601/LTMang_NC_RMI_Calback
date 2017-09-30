import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfClient extends Remote {

    Long execute(Task<Matrix> t) throws RemoteException;

    String getIP() throws RemoteException;
}
