import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpGw 
{
    ServerSocket s;
    public void start(){
        try {
            this.s = new ServerSocket(8080);
            while (true) {

                Socket socket = s.accept();
                Thread t = new Thread(new HttpWorker(socket));
                t.start();
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
