public class Data extends FSCPDU {
    private long FileID;
    
    public Data(long seqNum,int subtype,long fileid){
        super(seqNum,0,subtype);
        this.FileID = fileid;
    }
}
