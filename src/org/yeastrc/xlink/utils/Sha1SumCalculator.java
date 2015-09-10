package org.yeastrc.xlink.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1SumCalculator {

    private static final Sha1SumCalculator instance = new Sha1SumCalculator();
    
    private static char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
    private Sha1SumCalculator() {}
    
    public static Sha1SumCalculator instance() {
        return instance;
    }
    
    public String sha1SumFor(File file) throws NoSuchAlgorithmException, IOException {
        FileInputStream inStr;
        inStr = new FileInputStream(file);
        return sha1SumFor(inStr);
    }

    public String sha1SumFor(InputStream inStr) throws NoSuchAlgorithmException, IOException {
        
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = inStr.read(buffer);
            while (bytesRead > 0) {
                digest.update(buffer, 0, bytesRead);
                bytesRead = inStr.read(buffer);
            }
        }
        finally {
            if (inStr != null) {
                try {inStr.close();}
                catch (IOException e) {}
            }
        }
        byte[] digested = digest.digest();
        return bytesToHexString(digested);
    }
    
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int highBits = (bytes[i] & 0x000000f0) >> 4;
            int lowBits  =  bytes[i] & 0x0000000f;
            buf.append(hexChars[highBits]+""+hexChars[lowBits]);
        }
        return buf.toString();
    }
}
