package net.azar;


/**
 *  Payload class defines the data payload that is transmitted in a packet
 */
public class Payload
{
    private byte[] salt;
    private byte[] data;
    private byte[] hash;

    public Payload() {
    }

    public Payload(byte[] Data)
    {
        data = Data;
        salt = SecUtil.makeSalt();
        hash = SecUtil.getSHA1(Data);
    }

    public byte [] getSaltedData()
    {
        byte [] combined = new byte[salt.length + data.length];
        System.arraycopy(salt,0,combined,0,salt.length);
        System.arraycopy(data,0,combined,0,data.length);
        return combined;
    }

    public byte [] getData()
    {
        return data;
    }
}
