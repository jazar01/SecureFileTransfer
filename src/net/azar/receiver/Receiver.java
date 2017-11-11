package net.azar.receiver;

import net.azar.ArrayUtil;
import net.azar.FileHeaderData;
import net.azar.Packet;
import net.azar.SecUtil;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver extends Thread
{

    private ServerSocket ss;

    public Receiver(int port)
        {
        try
            {
            ss = new ServerSocket(port);
            }
        catch (IOException e)
            {
            e.printStackTrace();
            }
        }

    public void run()
        {
        System.out.println("Receiver starting");
        while (true)
            {
            try
                {
                Socket senderSock = ss.accept();
                getFile(senderSock);
                }
            catch (IOException e)
                {
                e.printStackTrace();
                }
            }
        }

    private void getFile(Socket senderSock) throws IOException
        {


        // TODO get parameterrs from settings
        // figure out how much overhhead room needs to be allocated
        int buffersize = 4276;  // Make sure we have enough room for expansion and overhead
        byte[] key = SecUtil.DeriveKey("bingo");
        System.out.println("Preparing to receive data");
        DataInputStream ds = new DataInputStream(senderSock.getInputStream());


        byte[] buffer = new byte[buffersize];

        System.out.println("Get file header");

        /* The first packet from the socket should be the packet
        containing the file header */

        int headerPacketSize = ds.read(buffer,0,buffer.length);
        System.out.println("File header bytes received: " + headerPacketSize);

        /* copy the the number of bytes sent to the rawpacket byte array*/
        byte [] rawpacket = new byte[headerPacketSize];
        System.arraycopy(buffer,0,rawpacket,0,headerPacketSize);

        /* attempt to deserialize the raw packet to a packet object */
        Packet p = null;
        FileHeaderData fhd = null;
        try
            {
            p = Packet.deserialize(rawpacket);
            }
        catch (Exception e)
            {
            System.out.println("Error deserializing packet" );
            System.out.println("Bytes: " + new String(rawpacket));
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return;
            }

        /* attempt to extract and deserialize the file header from the encrypted
            payload.
         */
        byte [] fileheaderbytes = null;
        try
            {
            fileheaderbytes = p.getDataBytes(key);
            }
        catch (Exception e)
            {
            System.out.println("Error getting data from file header" );
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return;
            }
        try
            {
            fhd = FileHeaderData.deserialize(fileheaderbytes);
            }
        catch (Exception e)
            {
            System.out.println("Error deserializing file header" );
            System.out.println("Bytes: " + new String(fileheaderbytes));
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return;
            }


        String filename = fhd.getFilename();
        int filesize = fhd.getLength();
        System.out.println("Filename: " + filename);
        System.out.println("Filesize " + filesize);

        FileOutputStream fs = new FileOutputStream(filename);

        int read = 0;
        int totalread = 0;
        int remaining = filesize;
        Packet packet = null;

        System.out.println("Key = " + ArrayUtil.bytesToHexString(key));
        while ((read = ds.read(buffer, 0, Math.min(buffer.length, remaining))) > 0)
            {
            totalread += read;
            remaining -= read;
            byte [] rawDatapacket = new byte[read];
            System.arraycopy(buffer,0,rawDatapacket,0,read);
            System.out.println("Received a packet of " + read + " bytes.  Total read = " + totalread);
            try
                {
                packet = Packet.deserialize(rawDatapacket);
                }
            catch (ClassNotFoundException e)
                {
                System.out.println("Error deserializing data packet");
                e.printStackTrace();
                Thread.currentThread().interrupt();
                }

            System.out.println("received " + totalread + " bytes.");
            // TODO Decrypt and extract data
            byte [] databytes = packet.getDataBytes(key);
            System.out.println("Unpacked " + databytes.length + " bytes");
            fs.write(databytes, 0, databytes.length);
            }
        System.out.println("Completed reading file");
        }
    public static void main(String[] args)
        {
        System.out.println("Receiver port 1988");
        Receiver r = new Receiver(1988);
        r.start();
        }
}
