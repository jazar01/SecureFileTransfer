package net.azar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SecUtil {

    public static byte[] getSHA1(byte[] inData)
    {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return md.digest(inData);
    }

    public static boolean testSHA1(byte [] hash, byte [] inData)
    {
        return Arrays.equals(hash, getSHA1(inData));
    }

}
