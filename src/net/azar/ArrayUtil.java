package net.azar;

public class ArrayUtil
{
    public static byte[] arrayjoin(byte[] a, byte [] b)
    {
        byte [] result = new byte [a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static byte[] arrayjoin(byte[] a, byte [] b, byte [] c)
    {
        byte [] result = new byte [a.length + b.length + c.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result,a.length+b.length, c.length);
        return result;
    }

    public static byte[] arrayjoin(byte[] a, byte [] b, byte [] c, byte [] d)
        {
        byte [] result = new byte [a.length + b.length + c.length + d.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result,a.length+b.length, c.length);
        System.arraycopy(d, 0, result,a.length+b.length+c.length, d.length);
        return result;
        }

    public static byte[] subarray(byte [] a, int startindex, int length)
    {
        byte [] result = new byte[length];
        for (int i = 0; i < length; i++)
            result[i] = a[startindex + i];
        return result;
    }

    /** convert a byte array to hex string
     *      for debugging and messages
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes){
    StringBuilder sb = new StringBuilder();
    for(byte b : bytes){
    sb.append(String.format("%02x", b&0xff));
    }
    return sb.toString();
    }
}
