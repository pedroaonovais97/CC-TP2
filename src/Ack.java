import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Ack extends Control {
    private long packetNum;

    public Ack(long seqNum,long p, String m){
        super(seqNum,0);
        this.packetNum = p;
    }

    public byte[] geraAck(){

        byte[] header = super.geraFSCPDU();
        byte[] p = ByteBuffer.allocate(8).putInt(this.getTipo()).array();

        byte[] completePDU = new byte[header.length + p.length];
        System.arraycopy(header, 0, completePDU, 0, header.length);
        System.arraycopy(p, 0, completePDU, header.length, p.length);

        byte[] checksum = super.geraChecksum(completePDU);
        System.arraycopy(checksum, 0, completePDU, 0, checksum.length);
        return completePDU;
    }

    public long getPacketNum(){
        return this.packetNum;
    }

}
