public class ConTerm extends Control {
    private long seqNum;

    public ConTerm(long seq){
        super(seq, (byte) 2);
    }

    public byte[] encodeConTerm(){
        byte[] header = super.encodeFSCPDU();
        return header;
    }

    public FSCPDU decodeConTerm(byte[] bytes){
        FSCPDU header = super.decodeFSCPDU(bytes);
        return header;
    }
}
