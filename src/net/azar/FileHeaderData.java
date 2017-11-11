package net.azar;
import java.io.*;
public class FileHeaderData implements Serializable
{
    private String filename;
    private int length;

    /**
     * Constructor
     * @param Filename
     * @param Length
     */
    public FileHeaderData(String Filename, int Length)
        {
        filename = Filename;
        length = Length;
        }

    public String getFilename()
        {
        return filename;
        }

    public int getLength()
        {
        return length;
        }

    public boolean equals(FileHeaderData fhd)
        {
        if (!fhd.getFilename().equals( getFilename()))
            return false;
        if (fhd.getLength() != getLength())
            return false;

        return true;
        }

    /**
     * Serialize the file header to a byte array
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
     * Deserialze a byte array into a file header
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static FileHeaderData deserialize(byte[] data) throws IOException, ClassNotFoundException
        {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (FileHeaderData) is.readObject();
        }


}



