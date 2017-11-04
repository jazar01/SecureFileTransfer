package net.azar;

public class UnitTests {
    public static void PacketTest()
    {
    byte[] testdata = "This is a block of test data for a test packet".getBytes();
    Packet p = new Packet("Client1", 'T', 0, testdata);
    boolean valid = p.IsValid();
    }
}
