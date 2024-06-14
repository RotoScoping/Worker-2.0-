package org.example.client;

import org.example.logger.AsyncLogger;
import org.example.model.Form;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.logging.Level;

public class Crypto {

    private static final AsyncLogger logger = AsyncLogger.get("client");
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567890123456";

    public static Form getEncryptedForm(Form form) {
       try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(form.password().getBytes());
           return new Form(form.login(),  Base64.getEncoder().encodeToString(encryptedBytes));
       } catch (Exception e) {
           logger.log(Level.WARNING, String.format("Ошибка во время шифрования: %s", e.getMessage()));
       }
       return form;
    }



}
