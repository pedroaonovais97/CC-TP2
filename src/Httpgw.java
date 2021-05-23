import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Httpgw {
    ServerSocket s;
    public void start(){
        try {
            this.s = new ServerSocket(80);
            while (true) {

                Socket socket = s.accept();
                Thread t = new Thread(new httpWorker(socket));
                t.start();
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
