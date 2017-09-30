
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class RMIServer extends UnicastRemoteObject implements InterfServer {

    static ArrayList<InterfClient> clients;

    public RMIServer() throws RemoteException {
        super();
    }

    @Override
    public boolean register(InterfClient client) {
        try {
            System.out.println("\nAdd Client :<<" + client.getIP() + ">>");
        } catch (RemoteException ex) {
        }
        return clients.add(client);
    }

    @Override
    public boolean unRegister(InterfClient client) {
        try {
            System.out.println("\nRemove Client :<<" + client.getIP() + ">>");
        } catch (RemoteException ex) {
        }
        return clients.remove(client);
    }

    @Override
    public void checkConnect() throws RemoteException {
    }

    public static void main(String[] args) {
        String host = Lib.getMyIp();
        if (args.length > 0) {
            host =args[0];
        }
        try {
            clients = new ArrayList<>();
            System.setProperty("java.rmi.server.hostname", host);
            //đăng ký dịch vụ
            Registry r = LocateRegistry.createRegistry(8850);
            InterfServer server;
            server = new RMIServer();
            r.rebind("SERVER", server);
            System.out.println("ClientWork at:" + host + " port: 8850");

            Registry r_forClient = LocateRegistry.createRegistry(8808);
            InterfServer server_forClient;
            server_forClient = new RMIServer();
            r_forClient.rebind("SERVER_FOR_CLIENT", server_forClient);
            System.out.println("ClientCalculator at:" + host + " port: 8808");

            //kiểm tra kết nối với client 
            while (true) {
                System.out.printf("\r%60s\r", "");
                System.out.print("Connected IP: ");
                try {
                    for (InterfClient client : clients) {
                        boolean isConnected = true;
                        try {
                            System.out.print(client.getIP() + " | ");
                        } catch (RemoteException ex) {
                            isConnected = false;
                        }
                        if (!isConnected) {
                            System.out.println("\nRemove Client some client");
                            clients.remove(client);
                        }
                    }
                } catch (ConcurrentModificationException ex) {

                }
                for (int i = 0; i < System.currentTimeMillis() / 1000 % 5; i++) {
                    System.out.print(".");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {

                }
            }
        } catch (RemoteException ex) {
            System.out.println("system encrupt");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public String execute() throws RemoteException {
        String ip = "List client:";
        for (InterfClient client : clients) {
            ip += client.getIP() + " | ";
        }
        return ip;
    }

    @Override
    public long execute(Matrix a) throws RemoteException {
        System.out.println("\n Have Request ...");
        return processAdd(a);
    }

    private long processAdd(Matrix a) {
        int numOfClient = clients.size();//số client join
        if (numOfClient > a.getNumRow()) {
            numOfClient = a.getNumRow();
        }
        Thread[] threads = new Thread[numOfClient]; //2 thread
        MatrixAdd[] matrixAdds = new MatrixAdd[numOfClient]; // 2 matrix add          
        int numOfRowSub = (int) Math.ceil(((double) a.getNumRow() / (double) numOfClient)); // 8/3 = 3         
        //thực hiện việc chia matrix
        for (int i = 0; i < numOfClient; i++) {
            Matrix tmpA = Lib.SubMatrixByRow(a, i, numOfRowSub);
            matrixAdds[i] = new MatrixAdd(clients.get(i), tmpA);// chia ma trạn 
            threads[i] = new Thread(matrixAdds[i]);
            threads[i].start();
        }
        long result = 0;
        for (int i = 0; i < numOfClient; i++) {
            try {
                threads[i].join();
                //nối matrix lại gửi trả client
                long ketqua = matrixAdds[i].result;
                System.out.println("Result from " + i + ": " + ketqua);
                result += ketqua;
            } catch (InterruptedException ex) {
                //Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

}
