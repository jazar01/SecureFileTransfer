package net.azar;


import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static javafx.application.Platform.exit;


public class SecUtil
{

    public static byte[] getSHA1(byte[] inData)
        {
        MessageDigest md;
        try
            {
            md = MessageDigest.getInstance("SHA-1");
            }
        catch (NoSuchAlgorithmException e)
            {
            e.printStackTrace();
            return null;
            }
        return md.digest(inData);
        }

    public static boolean testSHA1(byte[] hash, byte[] inData)
        {
        return Arrays.equals(hash, getSHA1(inData));
        }


    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SALT_BYTES = 8;
    private static final int KEY_BYTES = 32;
    private static final int IV_BYTES = 16;
    private static final int ITERATIONS = 1000;

    /**
     * derives an encryption key from a passphrase
     *
     * @param passphrase
     * @return
     */
    public static byte[] DeriveKey(String passphrase)
        {
        /*  Key is comprised of the salt + a derived key to equal a total of KEY_BYTES  */
        byte[] salt = makeSalt();
        byte[] key = pbkdf2(passphrase.toCharArray(), salt, ITERATIONS, KEY_BYTES - SALT_BYTES);

        return ArrayUtil.arrayjoin(salt, key);
        }


    public static byte[] makeSalt()
        {
        //TODO fix this salt problem
        // salted key should still be usable
        SecureRandom random = new SecureRandom();
        // byte[] salt = new byte[SALT_BYTES];
        // random.nextBytes(salt);
        byte [] salt = {1,2,3,4,5,6,7,8};  // temporary fix for testing only
        return salt;
        }


    /**
     * Computes the PBKDF2 hash of a password.
     *
     * @param password   the password to hash.
     * @param salt       the salt
     * @param iterations the iteration count (slowness factor)
     * @param bytes      the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
        {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        try
            {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
            }
        catch (Exception e)
            {
            e.printStackTrace();
            return null;
            }
        }

    /**
     * AES Encrypt a byte array using specified key
     *
     * @param inData
     * @param key
     * @return
     */
    public static byte[] Encrypt(byte[] inData, byte[] key)
        {
        SecureRandom random = new SecureRandom();
        byte[] randomIV = new byte[IV_BYTES];
        random.nextBytes(randomIV);

        IvParameterSpec iv = new IvParameterSpec(randomIV);
        SecretKeySpec skey = new SecretKeySpec(key, "AES");


        try
            {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);

            return ArrayUtil.arrayjoin(randomIV, cipher.doFinal(inData));
            }
        catch (Exception e)
            {
            e.printStackTrace();
            return null;
            }
        }


    public static byte[] Decrypt(byte[] inCipherData, byte key[])
        {
        byte[] randomIV = ArrayUtil.subarray(inCipherData, 0, IV_BYTES);
        byte[] encrypteddata = ArrayUtil.subarray(inCipherData, IV_BYTES, inCipherData.length - IV_BYTES);

        IvParameterSpec iv = new IvParameterSpec(randomIV);
        SecretKeySpec skey = new SecretKeySpec(key, "AES");

        Cipher cipher;
        byte[] saltydata = null;
        try
            {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skey, iv);
            saltydata = cipher.doFinal(encrypteddata);
            }
        catch (Exception e)
            {
            System.out.println("exception attempting to decrypt:");
            System.out.println(ArrayUtil.bytesToHexString(encrypteddata));

            System.out.println("using key:");
            System.out.println(ArrayUtil.bytesToHexString(key));

            e.printStackTrace();
            exit();
            }

        byte[] unsalteddata = ArrayUtil.subarray(saltydata, SALT_BYTES, saltydata.length - SALT_BYTES - 20);
        byte[] hash = ArrayUtil.subarray(saltydata, saltydata.length - 20, 20);
        // test data with hash to make sure it is not corrupt
        if (!testSHA1(hash, unsalteddata))
            return null;

        return unsalteddata;
        }


}
