package net.azar;

import net.azar.receiver.Receiver;

import java.util.Arrays;

public class UnitTests
{
    public static byte[] key = SecUtil.DeriveKey("bingo");

    public static void PacketTest() throws Exception
        {
        byte[] testdata = "This is a block of test data for a test packet".getBytes();
        Packet p = new Packet("Client1", 'T', testdata, key);

        // derive a new key from the same passphrase to make sure the secret
        // key doesn't change when a new key is derived.
        key = SecUtil.DeriveKey("bingo");

        // Get data from packet and decrypt
        // validate that decrpted data matches original
        byte[] returndata = p.getDataBytes(key);

        if (!Arrays.equals(testdata, returndata))
            throw new Exception("Packet failed integrity check");
        else
            System.out.println("Passed packet Integrity check");

        // test serialization and deserialization of packets
        byte[] serializedPacket = p.serialize();

        Packet p2 = Packet.deserialize(serializedPacket);

        if (!p.equals(p2))
            throw new Exception("Failed packet serialization check");
        else
            System.out.println("Passed packet serialization check");

        // test serialization and deserialization of fileheaders
        FileHeaderData fh = new FileHeaderData("test.dat", 10240);
        byte[] fhserialized = fh.serialize();
        FileHeaderData fh2 = FileHeaderData.deserialize(fhserialized);

        if (!fh.equals(fh2))
            throw new Exception("Failed header seriealization check");
        else
            System.out.println("Passed header seriealization check");

        // test serialization and deserialization of a packet
        // containing a file header.
        byte [] key1 = SecUtil.DeriveKey("bingo");
        Packet p3 = new Packet("Client1", 'H', fhserialized, key1);
        byte [] p3serialized = p3.serialize();
        byte [] key2 = SecUtil.DeriveKey("bingo");
        Packet p4 = Packet.deserialize(p3serialized);
        FileHeaderData fh4 = FileHeaderData.deserialize(p4.getDataBytes(key2));
        System.out.println("P4 Filename = " + fh4.getFilename() + " Length = " + fh4.getLength());

        if (!fh4.equals(fh2))
            throw new Exception("Failed header pakcet seriealization check");
        else
            System.out.println("Passed header packet seriealization check");


        // Receiver testing for debugging
        //System.out.println("Receiver port 1988");
        //Receiver r = new Receiver(1988);
        //r.start();


        }
}
