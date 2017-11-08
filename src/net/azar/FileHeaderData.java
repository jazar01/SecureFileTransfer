package net.azar;

public class FileHeaderData
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

    /**
     * get the File Header Data as series of byes (serialize)
     * @return
     */
    public byte[] getBytes()
        {
        byte[] delimeter = {0};
        byte[] name = filename.getBytes();
        byte[] len = Integer.toString(length).getBytes();
        return ArrayUtil.arrayjoin(name, delimeter, len, delimeter);
        }

    /**
     * Constructor
     * @param bheader serialized header data
     */
    public FileHeaderData(byte[] bheader)
        {
        byte[] name = null;
        byte[] len = null;

        int i = 0;
        while (i < bheader.length)
            {
            if (bheader[i] == 0)
                {
                name = new byte [i];
                System.arraycopy(bheader, 0, name, 0, i);
                i = name.length + 1;
                break;
                }
            i++;
            }
        int n = i;
        while (i < bheader.length)
            {
            if (bheader[i] == 0)
                {
                len = new byte[i - n];
                System.arraycopy(bheader, n, len, 0, i - n);
                break;
                }
            i++;
            }

        filename = new String(name);
        length = Integer.parseInt(new String(len));
        }




}



