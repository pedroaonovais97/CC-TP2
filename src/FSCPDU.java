import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FSCPDU {

    private byte tipo;
    private byte subtipo;
    private long seqNum;
    private long checksum;

    public FSCPDU(long seqNum,byte type, byte subtype){
        this.seqNum = seqNum;
        this.tipo = type;
        this.subtipo = subtype;
    }

    public byte getTipo(){
        return this.tipo;
    }

    @Override
    public String toString() {
        return "FSCPDU{" +
                "tipo=" + tipo +
                ", subtipo=" + subtipo +
                ", seqNum=" + seqNum +
                ", checksum=" + checksum;
    }

    public byte getSubtipo(){
        return this.subtipo;
    }

    public long getChecksum() {
        return checksum;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public void setTipo(byte tipo) {
        this.tipo = tipo;
    }

    public void setSubtipo(byte subtipo) {
        this.subtipo = subtipo;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    public void setSeqNum(long seqNum) {
        this.seqNum = seqNum;
    }


    public byte[] encodeFSCPDU(){

        byte[] pdu = new byte[18];
        pdu[0] = tipo;
        pdu[1] = subtipo;
        byte[] seqnum = ByteBuffer.allocate(8).putLong(this.getSeqNum()).array();

        int p = 2;

        for(int i = 0; i < seqnum.length; i++, p++){
            pdu[p] = seqnum[i];
        }

        byte[] checksum = geraChecksum(pdu);
        for(int i = 0; i < checksum.length; i++,p++) {
            pdu[p] = checksum[i];
        }

        return pdu;
    }


    public byte[] geraChecksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes,10,bytes.length-10);
        long c = crc32.getValue();
        this.checksum = c;
        byte[] checksBytes = ByteBuffer.allocate(8).putLong(checksum).array();
        return checksBytes;
    }

    public FSCPDU decodeFSCPDU(byte[] bytes){
        byte type = bytes[0];
        byte subtype = bytes[1];
        long seqNum = ByteBuffer.wrap(bytes,2,8).getLong();
        long checksum = ByteBuffer.wrap(bytes,10,8).getLong();

        FSCPDU pdu = new FSCPDU(seqNum,type,subtype);
        pdu.setChecksum(checksum);

        return pdu;
    }
}
