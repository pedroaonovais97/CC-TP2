import javax.swing.tree.TreeNode;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Transfer extends Data {
    private int offset;
    private int chunksize;
    private int fileIDsize;
    private String fileID;
    private byte[] dados;

    public Transfer(long seq, int off, int chunk, int fidsize, String fileid, byte[] d){
        super(seq, (byte) 1);
        this.offset = off;
        this.chunksize = chunk;
        this.fileIDsize = fidsize;
        this.fileID = fileid;
        this.dados = d;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getChunksize() {
        return chunksize;
    }

    public void setChunksize(int chunksize) {
        this.chunksize = chunksize;
    }

    public int getFileIDsize() {
        return fileIDsize;
    }

    public void setFileIDsize(int fileIDsize) {
        this.fileIDsize = fileIDsize;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }

    public byte[] encodeTransfer(){

        byte[] header = super.encodeFSCPDU();
        byte[] off = ByteBuffer.allocate(8).putInt(this.getOffset()).array();
        byte[] chunk = ByteBuffer.allocate(8).putInt(this.getChunksize()).array();
        byte[] fidsize = ByteBuffer.allocate(4).putInt(this.getFileIDsize()).array();
        byte[] id = this.fileID.getBytes(Charset.forName("UTF-8"));

        byte[] completePDU = new byte[header.length + off.length + chunk.length + fidsize.length + id.length + dados.length];
        System.arraycopy(header, 0, completePDU, 0, header.length);
        System.arraycopy(off, 0, completePDU, header.length, off.length);
        System.arraycopy(chunk, 0, completePDU, header.length + off.length, chunk.length);
        System.arraycopy(fidsize, 0, completePDU, header.length + off.length + chunk.length, fidsize.length);
        System.arraycopy(id, 0, completePDU, header.length + off.length + chunk.length + fidsize.length, id.length);
        System.arraycopy(dados, 0, completePDU, header.length + off.length + chunk.length + fidsize.length + id.length, dados.length);

        byte[] checksum = super.geraChecksum(completePDU);
        System.arraycopy(checksum, 0, completePDU, 10, checksum.length);
        return completePDU;
    }

    public Transfer decodeTransfer(byte[] bytes){
        FSCPDU pdu = decodeFSCPDU(bytes);
        int i = 18;

        int off = ByteBuffer.wrap(bytes,i,8).getInt();
        i+=8;

        int chunk = ByteBuffer.wrap(bytes,i,8).getInt();
        i+=8;

        int fidsize = ByteBuffer.wrap(bytes,i,4).getInt();
        i+=4;

        //Array de bytes do fileID
        byte[] idBytes = Arrays.copyOfRange(bytes,i,i+fileIDsize);
        //Conversão para String
        String fID = new String(idBytes,Charset.forName("UTF-8"));

        byte[] dados = Arrays.copyOfRange(bytes,i+fileIDsize, bytes.length);

        Transfer t = new Transfer(pdu.getSeqNum(), off, chunk, fidsize, fID, dados);
        t.setChecksum(pdu.getChecksum());
        t.setTipo(pdu.getTipo());
        t.setSubtipo(pdu.getSubtipo());

        return t;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", offset=" + offset +
                ", chunksize=" + chunksize +
                ", fileIDsize=" + fileIDsize +
                ", fileID='" + fileID + '\'' +
                ", dados=" + Arrays.toString(dados) +
                '}';
    }
}
