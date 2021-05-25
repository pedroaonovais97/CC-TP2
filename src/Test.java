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

        //System.out.println(pdu.decodeLookup(s).toString());


        //Recebi um HTTP REQUEST com o nome do ficheiro a transferir "file-to-send.txt"

        //Pedir os metadados desse ficheiro a um ou mais dos servidores FastFileSrv (verificar se existe??)
        
        //Executar um algoritmo de descarga do ficheiro

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
				int tamFile = pdu.getTamFich();
				//Pedir transferir ficheiro
				envia = new DatagramPacket(recebe.getData(), recebe.getData().length, address, 8888);
				socket.send(envia);

				message = new byte[1024];
				recebe = new DatagramPacket(message, message.length);
				System.out.println("Vai receber");
				socket.receive(recebe);


				System.out.println("Recebeu");
				byte[] fileBytes = new byte[tamFile]; 
				System.arraycopy(recebe.getData(), 18, fileBytes, 0, tamFile);

				File f = new File("pedro.txt");
				if (!f.exists()) {
	     			f.createNewFile();
	 		 	}
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(fileBytes);
				fos.flush();
				System.out.println();
			}
		}

    }
}
