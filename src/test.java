public class test {
    public static void main(String args[]){
        Lookup pdu = new Lookup(90,3,4,"Ssss",new byte[0]);
        byte [] s = pdu.encodeLookup();

        System.out.println(pdu.decodeLookup(s).toString());
    }
}
