package net.azar;

import java.util.Arrays;

public class UnitTests

{




    public static byte[] key = SecUtil.DeriveKey("bingo");


    public static void PacketTest() throws Exception {
    byte[] testdata = "This is a block of test data for a test packet".getBytes();
    Packet p = new Packet("Client1", 'T',  testdata, key);

    byte[] returndata = p.getDataBytes(key);

    if (!Arrays.equals(testdata, returndata))
        throw new Exception("Packet failed integrity check");
    else
        System.out.println("Passed packet Integrity check");


    boolean valid = p.IsValid();
    }



}
