import java.nio.ByteBuffer;

public class ConReq extends  Control {

    public ConReq(long seq){
        super(seq, (byte) 1);
    }

    public byte[] encodeConReq(){
        byte[] header = super.encodeFSCPDU();
        return header;
    }

    public FSCPDU decodeConReq(byte[] bytes){
        FSCPDU header = super.decodeFSCPDU(bytes);
        return header;
    }

}
