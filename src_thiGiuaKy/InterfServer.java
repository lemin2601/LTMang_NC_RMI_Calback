
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfServer extends Remote {

    long execute(Matrix a) throws RemoteException;

    String execute() throws RemoteException;

    boolean register(InterfClient client) throws RemoteException;

    boolean unRegister(InterfClient client) throws RemoteException;

    void checkConnect() throws RemoteException;
}
