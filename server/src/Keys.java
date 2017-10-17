import java.security.*;
import java.security.interfaces.*;
import javax.crypto.Cipher;
import java.security.spec.RSAPublicKeySpec;
import java.math.*;

public class Keys {
    private PrivateKey privateKey = null;
    private PublicKey publicKey = null;
    private KeyPair keypair = null;

    public Keys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
            keypair = keyPairGenerator.genKeyPair();
            privateKey = keypair.getPrivate();
            publicKey = keypair.getPublic();
        } catch(Exception e) {
            Logger.err("Failed to initialize keypair!");
            e.printStackTrace();
        }
    }

    public String getPublic() {
        RSAPublicKey publicKey = (RSAPublicKey) keypair.getPublic();
        return publicKey.getModulus().toString() + "|" + publicKey.getPublicExponent().toString();
    }

    public PublicKey getPublic(String key) throws Exception {
        String[] parts = key.split("\\|");
        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(parts[0]), new BigInteger(parts[1]));
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public String encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return new String(cipher.doFinal(message.getBytes()));
    }

    public String decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(cipher.doFinal(encrypted.getBytes()));
    }
}
