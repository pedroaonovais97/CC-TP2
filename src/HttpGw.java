import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpGw 
{

    public static void startP()
    {
        Thread t = new Thread(new HttpWorker());
        System.out.println("Start Thread");
        t.start();
        System.out.println("End Thread");
    }

    public static void main(String[] args) 
    {
        startP();
    }
    
}
