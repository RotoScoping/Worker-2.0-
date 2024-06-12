package org.example.util;

import org.example.logger.AsyncLogger;
import org.example.model.Form;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.logging.Level;

public class Decrypto {

    private static final AsyncLogger logger = AsyncLogger.get("server");
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567890123456";

    public static Form getDencryptedForm(Form form) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(form.password());
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new Form(form.login(), new String(decryptedBytes));
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Ошибка во время дешифрования: %s", e.getMessage()));
        }
        return form;
    }
}
