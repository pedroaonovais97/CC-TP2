import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;

public class FastFileSrv
{
	public static byte[] readFile(File file)
	{
		FileInputStream fileInS = null;
		//para ficheiros não fragmentados 
		byte[] ret = new byte[(int) file.length()];
		
		try 
		{
			fileInS = new FileInputStream(file);
			fileInS.read(ret);
			fileInS.close();
		} catch(IOException exp)
		{
			exp.printStackTrace();
		}

		return ret;
	}

	

	public static void main(String[] args) throws Exception 
	{
		InetAddress addr = InetAddress.getByName(args[0]);

		try (DatagramSocket socket = new DatagramSocket(8888,addr)) 
		{
			byte[] aReceber = new byte[1024]; 
			DatagramPacket pedido =  new DatagramPacket(aReceber, aReceber.length);

			while(true)
			{
				socket.receive(pedido);
				FSCPDU pdu = new FSCPDU();
				pdu = pdu.decodeFSCPDU(pedido.getData());


				//FSChunck do tipo Lookup
				System.out.println("Tipo: " + pdu.getTipo() + " Subtipo: " + pdu.getSubtipo());
				if(pdu.getTipo() == 0 && pdu.getSubtipo() == 0)
				{
					System.out.println("Teste0");
					Lookup lookupPDU = new Lookup();
					lookupPDU = lookupPDU.decodeLookup(pedido.getData());

					String fileName = lookupPDU.getFileID();
					System.out.println("FileId: " + lookupPDU.getFileID());

					File fp = new File(fileName);
					//Adicionar o tamanho do ficheiro
					if(fp.exists())
					{
						byte[] aEnviar = new byte[1024];
						lookupPDU.setTamFich((int)fp.length());
						aEnviar = lookupPDU.encodeLookup();

						InetAddress address = pedido.getAddress();
            			int port = pedido.getPort();

						pedido = new DatagramPacket(aEnviar, aEnviar.length, address, port);
						socket.send(pedido);
						System.out.println("Enviado");

						aReceber = new byte[1024];
						pedido =  new DatagramPacket(aReceber, aReceber.length);
						socket.receive(pedido);
						System.out.println("Teste2");
						//Tipo Data (1), subtipo(0):
						Data dataPDU = new Data(0,(byte) 0);
						
						
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
						outputStream.write( dataPDU.encodeFSCPDU() );
						outputStream.write( readFile(fp) );
						byte bData[] = outputStream.toByteArray();

						pedido = new DatagramPacket(bData, bData.length, address, port);
						socket.send(pedido);
					}
				}
			}
		}
	}
}