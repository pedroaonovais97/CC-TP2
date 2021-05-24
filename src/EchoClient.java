import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println("GET / HTTP/1.1");
            out.println("HOST: www.uminho.pt");
            out.println("Connection: close");
            out.println("User-Agent: HttpGw/1.0");
            out.println("Accept-Language: PT");
            out.println("");
            out.flush();            

            String t;
          /*  while((t = in.readLine()) != null)
            {
                out.println(t);
                out.flush();
                System.out.println("T:" + t);
            } */
            //in.close();

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

