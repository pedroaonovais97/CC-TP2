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

    public FSCPDU geraFSCPDU{

    }

    public static long geraChecksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
