package net.azar.sender;


import net.azar.Packet;
import net.azar.SecUtil;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
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

    public void sendFile(String file) throws IOException
        {
        // TODO get parameters from settings
        int buffersize = 4096;

        byte[] key = SecUtil.DeriveKey("bingo");

        // Packet p = new Packet("Client1", 'H',  testdata, key);

        DataOutputStream ds = new DataOutputStream(s.getOutputStream());
        FileInputStream fs = new FileInputStream(file);
        byte[] buffer = new byte[buffersize];

        while (fs.read(buffer) > 0)
            {
            // TODO encrypt and package data
            ds.write(buffer);  // TODO check the return value here.
            }

        fs.close();
        ds.close();
        }

    public static void main(String[] args)
        {
        // TODO send filename and size first
        String filename = args[0];
        Sender s = new Sender("localhost", 1988, filename);

        }


}
