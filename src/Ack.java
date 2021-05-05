import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Ack extends Control {
    private long packetNum;

    public Ack(long seqNum,long p){
        super(seqNum, (byte) 0);
        this.packetNum = p;
    }

    public byte[] encodeAck(){

        byte[] header = super.encodeFSCPDU();
        //Criação do array de bytes do packetnum
        byte[] p = ByteBuffer.allocate(8).putLong(this.getPacketNum()).array();

        //Criação do array de bytes do pdu completo
        byte[] completePDU = new byte[header.length + p.length];

        //Copia do header para o pdu completo
        System.arraycopy(header, 0, completePDU, 0, header.length);

        //Copia do packetnum para o pdu completo
        System.arraycopy(p, 0, completePDU, header.length, p.length);

        //Criação do checksum do pdu completo
        byte[] checksum = super.geraChecksum(completePDU);

        //Copia do checksum para o pdu completo
        System.arraycopy(checksum, 0, completePDU, 10, checksum.length);
        return completePDU;
    }

    public long getPacketNum(){
        return this.packetNum;
    }

    public Ack decodeAck(byte[] bytes){
        FSCPDU pdu = decodeFSCPDU(bytes);

        long pnum = ByteBuffer.wrap(bytes,18,8).getLong();

        Ack ack = new Ack(pdu.getSeqNum(),pnum);
        ack.setChecksum(pdu.getChecksum());
        ack.setTipo(pdu.getTipo());
        ack.setSubtipo(pdu.getSubtipo());

        return ack;
    }

    @Override
    public String toString() {
        return super.toString() + "packetNum=" + packetNum + "}";
    }
}
