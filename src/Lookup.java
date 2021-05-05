import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Lookup extends Data {
    private int tamFich;
    private int fileIDsize;
    private String fileID;
    private byte[] hash;

    public Lookup(long seq,int tamFich, int fidsize, String fileid, byte[] hash){
        super(seq, (byte) 0);
        this.tamFich = tamFich;
        this.fileIDsize = fidsize;
        this.fileID = fileid;
        this.hash = hash;
    }

    public int getTamFich() {
        return tamFich;
    }

    public void setTamFich(int tamFich) {
        this.tamFich = tamFich;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public int getFileIDsize() {
        return fileIDsize;
    }

    public void setFileIDsize(int fileIDsize) {
        this.fileIDsize = fileIDsize;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] encodeLookup(){

        byte[] header = super.encodeFSCPDU();
        byte[] tam = ByteBuffer.allocate(8).putInt(this.getTamFich()).array();
        byte[] fidsize = ByteBuffer.allocate(4).putInt(this.getFileIDsize()).array();
        byte[] id = this.fileID.getBytes(Charset.forName("UTF-8"));

        byte[] completePDU = new byte[header.length + tam.length + fidsize.length + id.length + hash.length];
        System.arraycopy(header, 0, completePDU, 0, header.length);
        System.arraycopy(tam, 0, completePDU, header.length, tam.length);
        System.arraycopy(fidsize, 0, completePDU, header.length + tam.length , fidsize.length);
        System.arraycopy(id, 0, completePDU, header.length + tam.length + fidsize.length, id.length);
        System.arraycopy(hash, 0, completePDU, header.length + tam.length  + fidsize.length + id.length, hash.length);

        byte[] checksum = super.geraChecksum(completePDU);
        System.arraycopy(checksum, 0, completePDU, 10, checksum.length);
        return completePDU;
    }

    public Lookup decodeLookup(byte[] bytes){
        FSCPDU pdu = decodeFSCPDU(bytes);
        int i = 18;

        int tam = ByteBuffer.wrap(bytes,i,8).getInt();
        i+=8;

        int fidsize = ByteBuffer.wrap(bytes,i,4).getInt();
        i+=4;

        //Array de bytes do fileID
        byte[] idBytes = Arrays.copyOfRange(bytes,i,i+fileIDsize);
        //Conversão para String
        String fID = new String(idBytes,Charset.forName("UTF-8"));

        byte[] hash = Arrays.copyOfRange(bytes,i+fileIDsize, bytes.length);

        Lookup l = new Lookup(pdu.getSeqNum(), tam, fidsize, fID, hash);
        l.setChecksum(pdu.getChecksum());
        l.setTipo(pdu.getTipo());
        l.setSubtipo(pdu.getSubtipo());

        return l;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", tamFich=" + tamFich +
                ", fileIDsize=" + fileIDsize +
                ", fileID='" + fileID + '\'' +
                ", hash=" + Arrays.toString(hash) +
                '}';
    }
}
