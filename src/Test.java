import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Test {
    public static void main(String args[]) throws Exception{
        Lookup pdu = new Lookup(90,3,4,"Ssss",new byte[0]);
        byte[] s = pdu.encodeLookup();

        //System.out.println(pdu.decodeLookup(s).toString());

        try (DatagramSocket socket = new DatagramSocket()) 
		{
			InetAddress address = InetAddress.getByName("localhost");
			DatagramPacket envia = new DatagramPacket(s, s.length, address, 8888);
			socket.send(envia);
		}

    }
}
