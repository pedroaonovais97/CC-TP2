public class Control extends FSCPDU {
    public Control(long seqNum, byte subtype){
        super(seqNum, (byte) 1,subtype);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
