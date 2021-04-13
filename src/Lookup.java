public class Lookup extends Data {
    private long tamFich;
    private long seqNum;

    public Lookup(long seq,long tamFich,long fileid){
        super(seq,0,fileid);
        this.tamFich = tamFich;
    }

}
