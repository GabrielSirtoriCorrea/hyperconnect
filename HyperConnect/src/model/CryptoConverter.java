package model;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoConverter {

    public static byte[] encrypt(String text) {
        try {
            SecretKey secret = new SecretKeySpec(Base64.getDecoder().decode(JsonEditor.readJSON().get("secret").toString()), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] cryptoText = cipher.doFinal(text.getBytes());
            return cryptoText;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(byte[] cryptoText) {
        try {
            SecretKey secret = new SecretKeySpec(Base64.getDecoder().decode(JsonEditor.readJSON().get("secret").toString()), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, secret);
            return new String(cipher.doFinal(cryptoText));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
    }
}
