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

	public static void sendSingleDataPdu(DatagramSocket socket,InetAddress address, int port, File fp) throws Exception
	{

		//Envia o FSCPDU com os bytes do ficheiro		
		//Tipo Data (1), subtipo(0):
		Data dataPDU = new Data(0,(byte) 0);
						
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write( dataPDU.encodeFSCPDU() );
		outputStream.write( readFile(fp) );
		byte bData[] = outputStream.toByteArray();

		DatagramPacket pedido = new DatagramPacket(bData, bData.length, address, port);
		socket.send(pedido);
	}

	public static void sendMultDataPdu(DatagramSocket socket,InetAddress address, int port, File fp) throws Exception
	{

		//Envia o FSCPDU com os bytes do ficheiro		
		//Tipo Data (1), subtipo(0):
		int len = (int) fp.length();
		byte[] arr = readFile(fp);
		for(int i = 0; len != 0; i++)
		{
			int lastTam = 2048;
			if(len < lastTam)
				lastTam = len;

			Data dataPDU = new Data(0,(byte) 0);
			dataPDU.setSeqNum(i);
			byte[] bytesToSend = new byte[2048];	
			System.out.println("Desde: " + (i*2048) + "Num:" + lastTam);			
			System.arraycopy(arr, i*2048, bytesToSend, 0, lastTam);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			outputStream.write( dataPDU.encodeFSCPDU() );
			outputStream.write( bytesToSend );
			byte bData[] = outputStream.toByteArray();

			len-=lastTam;
			DatagramPacket pedido = new DatagramPacket(bData, bData.length, address, port);
			socket.send(pedido);
		}
	}


	public static void main(String[] args) throws Exception 
	{
		InetAddress addr = InetAddress.getByName(args[0]);

		//Control type = 1
		try (DatagramSocket socket = new DatagramSocket()) 
		{
			ConReq cr = new ConReq(0);
			byte[] bcr = new byte[1024];
			bcr = cr.encodeConReq();

			DatagramPacket crDP =  new DatagramPacket(bcr, bcr.length,addr,8880);
			System.out.println("Vai enviar");
			socket.send(crDP);
			System.out.println("Enviou");
			socket.disconnect();
			socket.close();
		}

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
				if(pdu.getTipo() == 0 && pdu.getSubtipo() == 0)
				{
					Lookup lookupPDU = new Lookup();
					lookupPDU = lookupPDU.decodeLookup(pedido.getData());

					String fileName = lookupPDU.getFileID();
					System.out.println("FileId: " + lookupPDU.getFileID());

					File fp = new File(fileName);
					if(fp.exists())
					{
						lookupPDU.setTamFich((int)fp.length());
						InetAddress address = pedido.getAddress();
        				int port = pedido.getPort();

        				byte[] aEnviar = new byte[1024];
						aEnviar = lookupPDU.encodeLookup();

						//Envia com packet os metadados
						pedido = new DatagramPacket(aEnviar, aEnviar.length, address, port);
						socket.send(pedido);

						//Fica à espera de ordem para enviar
						aReceber = new byte[1024];
						pedido =  new DatagramPacket(aReceber, aReceber.length);
						socket.receive(pedido);

						if(lookupPDU.getTamFich() > 2048)
						{
							sendMultDataPdu(socket, address, port, fp);
						}
						else
							sendSingleDataPdu(socket,address, port, fp);
						
					}
				}
			}
		} catch(IOException excp)
		{
			System.out.println(excp.getMessage());
		}
	}
}