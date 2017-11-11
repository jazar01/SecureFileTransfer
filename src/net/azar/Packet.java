package net.azar;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Packet class defines a packet of data to be transferred.
 * the packet contains meta data and contains a payload.
 * the payload is the content that is being transferred.
 */
public class Packet implements Serializable
{
    private byte[] clientID = new byte[16];
    private char ptype;
    private byte flags;
    private byte[] cipherPayLoad;

    public void setClientID(String ClientID)
        {
        if (ClientID.length() <= 16)
            clientID = ClientID.getBytes();
        else
            clientID = ClientID.substring(0, 15).getBytes();
        }

    public String getClientID()
        {
        return new String(clientID);
        }

    private byte[] getCipherPayLoad()
        {
        return cipherPayLoad;
        }

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
    private void setDataBytes(byte[] DataBytes, byte[] key)
        {
        DataPayload payload = new DataPayload(DataBytes);
        cipherPayLoad = SecUtil.Encrypt(payload.getSaltedData(), key);
        }

    /**
     * returns the unencryped bytes from the payload
     * @return
     */
    public byte[] getDataBytes(byte[] key)
        {
        return SecUtil.Decrypt(cipherPayLoad, key);
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

    /**
     * compares two packets.  May only be needed for unit testing
     * @param p
     * @return
     */
    public boolean equals(Packet p)
        {
            if (!p.getClientID().equals(getClientID()))
                return false;
            if (p.getPType() != getPType())
                return false;
            if (!Arrays.equals(p.getCipherPayLoad(), getCipherPayLoad()))
                return false;

            return true;
        }

    /**
     * Serialize the packet to a byte array
     * @return
     * @throws IOException
     */
    public byte[] serialize() throws IOException
        {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(this);
        return out.toByteArray();
        }

    /**
     * Deserialze a byte array into a packet
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Packet deserialize(byte[] data) throws IOException, ClassNotFoundException
        {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Packet) is.readObject();
        }
}


