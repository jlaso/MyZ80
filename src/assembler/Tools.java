package assembler;

/**
 * Created by joseluislaso on 08/09/15.
 */
public class Tools {

    final protected static String hexChars = "0123456789ABCDEF";

    public static String byteToHex(int b)
    {
        b = b & 0xff;
        return "" + hexChars.charAt(b >>> 4) + hexChars.charAt(b & 0x0F);
    }

    public static String addressToHex(int a)
    {
        return byteToHex(a >>> 8) + byteToHex(a);
    }

    public static String bytesToHex(int[] bytes) {
        String result = "";
        for ( int j = 0; j < bytes.length; j++ ) {
            result += byteToHex(bytes[j]);
        }
        return result;
    }

}
