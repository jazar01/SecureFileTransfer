package net.azar;
/**
 *  DataPayload class defines the data payload that is transmitted in a packet
 */
public class DataPayload
{
    private byte[] salt;  // 8 bytes
    private byte[] data;
    private byte[] hash;  // 20 bytes

    public DataPayload(byte[] Data)
    {
        data = Data;
        salt = SecUtil.makeSalt();
        hash = SecUtil.getSHA1(Data);
    }

    public byte [] getSaltedData()
    {
        return ArrayUtil.arrayjoin(salt,data,hash);
    }


    public byte [] getData()
    {
        return data;
    }

    public int getLength()
    {
        return salt.length+data.length+hash.length;
    }
}
