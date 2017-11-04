package net.azar;

public class Packet {
    private byte[] clientID = new byte[16];
    private char ptype;
    private int sequence;
    private byte flags;
    private int dataLength;
    private byte[] hash = new byte [20];   // sha1 hash
    private byte[] data = new byte [4096]; // payload

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

    public void setSequence(int seq)
    {
        sequence = seq;
    }

    public int getSequence()
    {
        return sequence;
    }

    /**
     * @param DataBytes
     */
    public void setDataBytes(byte[] DataBytes)
    {
        data = DataBytes;
        dataLength = data.length;
        // encrypt data here
        hash = SecUtil.getSHA1(data);
    }

    public byte[] getDataBytes()
    {
        //decrypt data here
        return data;
    }

    // constructor
    /**
     * @param ClientID
     * @param PacketType
     * @param Sequence
     * @param data
     * @throws Exception
     */
    public Packet(String ClientID, char PacketType, int Sequence, byte[] data)
    {
        setClientID(ClientID);
        setPType(PacketType);
        setSequence(Sequence);
        setDataBytes(data);
        flags = 0;
    }



    public boolean IsValid()
    {
        if (!SecUtil.testSHA1(hash,data))
            return false;
        if (dataLength != data.length)
            return false;

        return true;
    }

}


