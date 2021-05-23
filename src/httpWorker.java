import java.io.IOException;
import java.net.Socket;

public class httpWorker extends Thread implements Runnable{
    Socket socket;
    String fileName;


    public httpWorker(Socket s){
        this.socket = s;
    }
    @Override
    public void run() {
        try {
            String m = socket.getInputStream().toString();
            m = m.substring(m.indexOf('/'));
            m = m.substring(0,m.indexOf(" "));
            this.fileName = m;
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
