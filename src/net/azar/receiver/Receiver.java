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
    // TODO figure out how to make this run as one thread per transfer
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
                System.out.println("waiting for file");
                Socket senderSock = ss.accept();
                getFile(senderSock); // TODO start a thread for this??
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
        // TODO Check packet types
        // TODO error checking and exception handling
        // figure out how much overhhead room needs to be allocated
        int buffersize = 4276;  // Make sure we have enough room for expansion and overhead
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

        // the key to decrypt is derived from the Passphrase and ClientID
        byte[] key = SecUtil.DeriveKey("bingo", p.getClientID());
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

        // We have a filename and file size from the sender
        // now create file at receiver location
        FileOutputStream fs = new FileOutputStream(filename);

        int read = 0;
        int totalread = 0;
        int remaining = filesize;
        Packet packet = null;

        System.out.println("Key = " + ArrayUtil.bytesToHexString(key));
        // read buffers from the socket until buffers stop comming
        while ((read = ds.read(buffer)) > 0)
            {
            totalread += read;
            // remaining -= read;

            // copy the buffer to a raw data packet to truncate the buffer to
            // exact size received.
            byte [] rawDatapacket = new byte[read];
            System.arraycopy(buffer,0,rawDatapacket,0,read);
            // TODO System.out.println("Received a packet of " + read + " bytes.  Total read = " + totalread);

            // attemp to deserialize the raw data into a packet object
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

            // TODO System.out.println("received " + totalread + " bytes.");

            // decrypt the data portion of packet into a byte array
            byte [] databytes = packet.getDataBytes(key);
            // TODO System.out.println("Unpacked " + databytes.length + " bytes");

            // write the decrypted data bytes to the output file
            fs.write(databytes, 0, databytes.length);
            }

        fs.close(); // close the output file
        ds.close(); // close the socket

        System.out.println("Completed reading file");
        }


    public static void main(String[] args)
        {
        System.out.println("Receiver port 1988");
        Receiver r = new Receiver(1988);
        r.start();
        }
}
