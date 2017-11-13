package net.azar.sender;


import net.azar.ArrayUtil;
import net.azar.Packet;
import net.azar.SecUtil;
import net.azar.FileHeaderData;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.net.Socket;

public class Sender
{
    private Socket s;

    public Sender(String host, int port, String file)
        {
        try
            {
            s = new Socket(host, port);
            sendFile(file);
            }
        catch (Exception e)
            {
            e.printStackTrace();
            }
        }

    public void sendFile(String filename) throws IOException
        {
        // TODO get parameters from settings
        int buffersize = 4096;
        File file = new File(filename);

        byte[] key = SecUtil.DeriveKey("bingo","Client1");
        System.out.println("Key = " + ArrayUtil.bytesToHexString(key));

        //  Send the file header first
        FileHeaderData fileheader = new FileHeaderData(filename, (int) file.length() );
        Packet headerpacket = new Packet("Client1",'H',fileheader.serialize(),key );
        DataOutputStream ds = new DataOutputStream(s.getOutputStream());
        ds.write(headerpacket.serialize());

        // Read the file in blocks, package the blocks in packets, and send them
        byte[] buffer = new byte[buffersize];
        FileInputStream fs = new FileInputStream(file);
        Packet dataPacket;

        byte [] dataPacketSerialized;
        int bytesread = 0;
        while ((bytesread = fs.read(buffer)) > 0)
            {
            byte [] transmitBuffer = new byte[bytesread];
            System.arraycopy(buffer,0,transmitBuffer, 0, bytesread);
            // put the buffer just read in to a packet
            dataPacket = new Packet("Client1",'D', transmitBuffer, key );

            // serialze the packet
            dataPacketSerialized = dataPacket.serialize();
            System.out.println("Sending packet size = " + dataPacketSerialized.length);

            // send it
            ds.write(dataPacketSerialized, 0, dataPacketSerialized.length);  // wrap in try catch
            ds.flush();
            }

        fs.close();
        ds.close();
        }

    public static void main(String[] args)
        {

        // TODO send filename and size first
        String filename = args[0];
        System.out.println("Sending file: " + filename + " on port 1988");
        Sender s = new Sender("localhost", 1988, filename);

        }


}
