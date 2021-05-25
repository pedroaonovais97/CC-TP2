public class Data extends FSCPDU 
{
    public Data(long seqNum,byte subtype){
        super(seqNum, (byte) 0,subtype);
    }
}
