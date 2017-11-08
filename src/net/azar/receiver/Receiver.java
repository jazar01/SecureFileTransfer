package net.azar.receiver;

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
                }
            catch (IOException e)
                {
                e.printStackTrace();
                }
            }
        }

    private void getFile(Socket senderSock) throws IOException
        {
        // TODO get name and size from stream
        String filename = "test.dat";
        int filesize = 10240;

        // TODO get parameterrs from settings
        int buffersize = 4096;


        DataInputStream ds = new DataInputStream(senderSock.getInputStream());
        FileOutputStream fs = new FileOutputStream(filename);
        byte[] buffer = new byte[buffersize];

        int read = 0;
        int totalread = 0;
        int remaining = filesize;

        while ((read = ds.read(buffer, 0, Math.min(buffer.length, remaining))) > 0)
            {
            totalread += read;
            remaining -= remaining;
            System.out.println("received " + totalread + " bytes.");
            // TODO Decrypt and extract data
            fs.write(buffer, 0, read);
            }
        }
    public static void main(String[] args)
        {
        Receiver r = new Receiver(1988);
        r.start();
        }
}
