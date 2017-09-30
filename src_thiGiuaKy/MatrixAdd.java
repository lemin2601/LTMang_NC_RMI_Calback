
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixAdd implements Runnable, Task<Matrix>, Serializable {

    InterfClient client;
    Matrix a;
    long result;

    boolean isDone = false;

    public MatrixAdd(InterfClient client, Matrix a) {
        this.client = client;
        this.a = a;
    }

    @Override
    public void run() {
        try {
            //yêu cầu thực hiện ở client;
            result = client.execute(this);
        } catch (RemoteException ex) {
            Logger.getLogger(MatrixAdd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setClient(InterfClient client) {
        this.client = client;
    }

    @Override
    public Long execute() {
        long result = 0;
        for (int i = 0; i < a.getNumRow(); i++) {
            for (int j = 0; j < a.getNumCol(); j++) {
                result += a.getValue(i, j);
            }
        }
        return result;
    }

}
