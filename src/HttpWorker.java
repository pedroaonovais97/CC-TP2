import java.io.IOException;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpWorker extends Thread implements Runnable
{
    Socket socket;
    Map<String, Boolean> fstServers;

    public HttpWorker(){
        this.socket = null;
        this.fstServers = new HashMap<>();
    }

    @Override
    public void run(){
        try {
            
            try (DatagramSocket socket = new DatagramSocket(8880)) 
            {
                
                byte[] crPackets = new byte[1024];
                DatagramPacket recebe = new DatagramPacket(crPackets, crPackets.length);
                System.out.println("À Espera de Servidor...");
                socket.receive(recebe);
                System.out.println(recebe.getAddress().toString());
                socket.disconnect();
                socket.close();
                System.out.println("Conectado");

                FSCPDU fscpdu = new FSCPDU();
                fscpdu = fscpdu.decodeFSCPDU(recebe.getData());
                if(fscpdu.getTipo() == 1)
                {
                    this.fstServers.put(recebe.getAddress().getHostName(),false);
                }

            }
            catch(Exception e){}

            ServerSocket ss = new ServerSocket(8080);
            Socket socketTCP = ss.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
            PrintWriter out = new PrintWriter(socketTCP.getOutputStream());

            String line;
            while ((line = in.readLine()) != null) 
            {
                if(line.substring(0,3).toString() == "GET");
                    System.out.println(line);
            }
            
            String server = "";
            
            for(String i : fstServers.keySet())
                if(!fstServers.get(i))
                    server = i;
            try {
                connectFST("file-to-send.txt",server);
            }
            catch(IOException ex)
            {
                System.out.println(ex.getMessage());
            }

            socketTCP.shutdownOutput();
            socketTCP.shutdownInput();
            socketTCP.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public byte[] connectFST(String fileName, String addrName) throws Exception
    {
        try (DatagramSocket socket = new DatagramSocket()) 
        {
            //irá ter lista de fast file servers
            InetAddress address = InetAddress.getByName(addrName);

            //Tipo Controlo (0):
            //     subtipo Lookup (0)

            //Look up dos metadados do ficheiro e se ele existe
            //String fileName = "file-to-send.txt";
            Lookup pdu = new Lookup(0,0,fileName.length(),fileName,new byte[0]);
            System.out.println(pdu.getTipo());
            byte[] s = pdu.encodeLookup();
            DatagramPacket envia = new DatagramPacket(s, s.length, address, 8888);
            socket.send(envia);
            
            //Resposta do Servidor
            byte[] message = new byte[1024];
            DatagramPacket recebe = new DatagramPacket(message, message.length);
            socket.receive(recebe);
            
            pdu = pdu.decodeLookup(recebe.getData());
            if(pdu.getFileID() != "")
            {
                //Pedir transferir ficheiro
                int tamFile = pdu.getTamFich();
                envia = new DatagramPacket(recebe.getData(), recebe.getData().length, address, 8888);
                socket.send(envia);



                //Recebe Ficheiro
                File f = new File(fileName);
                if (!f.exists()) {f.createNewFile();}
                FileOutputStream fos = new FileOutputStream(f);

                while(tamFile > 2048)
                {
                    System.out.println("T: " + tamFile);
                    byte[] fileBytes = new byte[2048];
                    message = new byte[2068];
                    recebe = new DatagramPacket(message, message.length);       
                    socket.receive(recebe);
                    System.arraycopy(recebe.getData(), 18, fileBytes, 0, 2048);
                    fos.write(fileBytes);
                    tamFile -= 2048;
                }
        
                if(tamFile > 0)
                {
                    System.out.println("TI: " + tamFile);
                    message = new byte[tamFile + 20];
                    recebe = new DatagramPacket(message, message.length);
                    socket.receive(recebe);
                    byte[] fileBytes = new byte[tamFile];
                    System.arraycopy(recebe.getData(), 18, fileBytes, 0, tamFile);
                    fos.write(fileBytes);
                }   
                fos.flush();

            }
        }

        return null;
    }
}
