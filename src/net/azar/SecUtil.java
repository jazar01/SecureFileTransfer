package net.azar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.spec.InvalidKeySpecException;


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


    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 24;
    private static final int ITERATIONS = 1000;


    public static byte[] DeriveKey(String password)  {
        byte [] salt = makeSalt();
        byte[] key = pbkdf2(password.toCharArray(), salt, ITERATIONS, HASH_BYTES);
        return key;
    }


    public static byte[] makeSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return salt;
    }


    /**
     *  Computes the PBKDF2 hash of a password.
     *
     * @param   password    the password to hash.
     * @param   salt        the salt
     * @param   iterations  the iteration count (slowness factor)
     * @param   bytes       the length of the hash to compute in bytes
     * @return              the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)

    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
