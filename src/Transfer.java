public class Transfer extends Data {
    private long seqNum;
    private int offset;
    private byte[] dados;

    public Transfer(long seq,int off, long fileid, byte[] d){
        super(seq,1,fileid);
        this.offset = off;
        this.dados = d;
    }
}
