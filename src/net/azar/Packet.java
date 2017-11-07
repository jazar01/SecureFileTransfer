package net.azar;

import java.lang.reflect.Array;

/**
 * Packet class defines a packet of data to be transferred.
 *  the packet contains meta data and contains a payload.
 *  the payload is the content that is being transferred.
 */
public class Packet {
    private byte[] clientID = new byte[16];
    private char ptype;
    private byte flags;
    private byte [] cipherPayLoad;

    public void setClientID(String ClientID)
    {
        if (ClientID.length() <= 16 )
            clientID = ClientID.getBytes();
        else
            clientID = ClientID.substring(0, 15).getBytes();
    };

    public String getClientID()
    {
        return clientID.toString();
    };

    public void setPType(char p)
    {
        ptype = p;
    }

    public char getPType()
    {
        return ptype;
    }

    /**
     * @param DataBytes
     */
    private void setDataBytes(byte[] DataBytes, byte [] key)
    {
        DataPayload payload = new DataPayload(DataBytes);
        cipherPayLoad = SecUtil.Encrypt(payload.getSaltedData(),key );
    }

    public byte[] getDataBytes(byte [] key)
    {

        return  SecUtil.Decrypt(cipherPayLoad, key);
    }

    // constructor
    /**
     * @param ClientID
     * @param PacketType
     * @param data
     * @param key
     */
    public Packet(String ClientID, char PacketType, byte[] data, byte[] key)
    {
        setClientID(ClientID);
        setPType(PacketType);
        setDataBytes(data, key);
        flags = 0;
    }



    public boolean IsValid()
    {


        return true;
    }

}


