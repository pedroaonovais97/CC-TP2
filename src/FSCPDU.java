import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FSCPDU {

    private int tipo;
    private int subtipo;
    private long checksum;
    private long seqNum;

    public FSCPDU(long seqNum,int type, int subtype){
        this.seqNum = seqNum;
        this.tipo = type;
        this.subtipo = subtype;
    }

    public FSCPDU(long seqNum,int type, int subtype,long fileID){
        this.seqNum = seqNum;
        this.tipo = type;
        this.subtipo = subtype;
    }

    public int getTipo(){
        return this.tipo;
    }

    public int getSubtipo(){
        return this.subtipo;
    }

    public long getChecksum() {
        return checksum;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public void setSubtipo(int subtipo) {
        this.subtipo = subtipo;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    public void setSeqNum(long seqNum) {
        this.seqNum = seqNum;
    }


    public byte[] geraFSCPDU(){

        byte[] pdu = new byte[20];
        byte[] type = ByteBuffer.allocate(4).putInt(this.getTipo()).array();
        byte[] subtype = ByteBuffer.allocate(4).putInt(this.getSubtipo()).array();
        byte[] seqnum = ByteBuffer.allocate(8).putLong(this.getSeqNum()).array();

        int p = 0;

        for(int i = 0; i < seqnum.length; i++, p++){
            pdu[p] = seqnum[i];
        }

        for(int i = 0; i < type.length; i++, p++){
            pdu[p] = type[i];
        }
        for(int i = 0; i < subtype.length; i++, p++){
            pdu[p] = subtype[i];
        }
        return pdu;
    }


    public byte[] geraChecksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        long c = crc32.getValue();
        this.checksum = c;
        byte[] checksBytes = ByteBuffer.allocate(8).putLong(checksum).array();
        return checksBytes;
    }
}
