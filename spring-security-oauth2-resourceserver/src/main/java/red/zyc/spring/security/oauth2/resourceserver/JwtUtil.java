package red.zyc.spring.security.oauth2.resourceserver;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.converter.RsaKeyConverters;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

/**
 * @author zyc
 */
@Slf4j
public final class JwtUtil {

    private static RSAPrivateKey privateKey;
    private static RSAPublicKey publicKey;

    static {
        privateKey = RsaKeyConverters.pkcs8().convert(JwtUtil.class.getResourceAsStream("/key.private"));
        publicKey = RsaKeyConverters.x509().convert(JwtUtil.class.getResourceAsStream("/key.public"));
    }

    private JwtUtil() {
    }

    /**
     * 生成jwt
     *
     * @return jwt
     */
    public static String jwt() {
        try {
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject("zyc")
                    .issueTime(new Date())
                    .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(new JWSAlgorithm("RS512")), jwtClaimsSet);
            // 私钥签名，公钥验签
            jwt.sign(new RSASSASigner(privateKey));
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 生成私钥和公钥
     */
    private static void keys() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            log.info("{}{}{}", "\n-----BEGIN PRIVATE KEY-----\n", Base64.getMimeEncoder().encodeToString(privateKey.getEncoded()), "\n-----END PRIVATE KEY-----");
            log.info("{}{}{}", "\n-----BEGIN PUBLIC KEY-----\n", Base64.getMimeEncoder().encodeToString(publicKey.getEncoded()), "\n-----END PUBLIC KEY-----");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 加密
     *
     * @param s 明文
     * @return 密文
     */
    private static String encrypt(String s) {
        try {
            log.info("plainText:{}", s);
            Cipher cipher = Cipher.getInstance("rsa");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes()));
            log.info("encrypt:{}", encrypt);
            return encrypt;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 解密
     *
     * @param s 密文
     * @return 明文
     */
    private static String decrypt(String s) {
        try {
            Cipher cipher = Cipher.getInstance("rsa");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String decrypt = new String(cipher.doFinal(Base64.getDecoder().decode(s)));
            log.info("decrypt:{}", decrypt);
            return decrypt;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
