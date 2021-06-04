import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpGw 
{

    public static void startP()
    {
        Thread t = new Thread(new HttpWorker());
        t.start();
    }

    public static void main(String[] args) 
    {
        startP();
    }
    
}
