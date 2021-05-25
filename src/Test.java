import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.io.File;
import java.io.FileOutputStream;

public class Test 
{

    public static void main(String args[]) throws Exception
    {

        try (DatagramSocket socket = new DatagramSocket()) 
		{
			//irá ter lista de fast file servers
			InetAddress address = InetAddress.getByName("localhost");

			//Tipo Controlo (0):
    		//	   subtipo Lookup (0)

    		//Look up dos metadados do ficheiro e se ele existe
    		String fileName = "file-to-send.txt";
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

    }
}
