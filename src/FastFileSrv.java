import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class FastFileSrv
{
	public static void main(String[] args) throws Exception 
	{
		try (DatagramSocket socket = new DatagramSocket(8888)) 
		{
			byte[] aReceber= new byte[1024]; 
			DatagramPacket pedido =  new DatagramPacket(aReceber, aReceber.length);

			while(true)
			{
				socket.receive(pedido);
				FSCPDU pdu = new FSCPDU();
				pdu = pdu.decodeFSCPDU(pedido.getData());
				System.out.println(pdu.toString());
			}
		}
	}
}