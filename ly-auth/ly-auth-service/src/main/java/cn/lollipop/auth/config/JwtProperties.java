package cn.lollipop.auth.config;

import cn.lollipop.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private int expire;
    private String cookieName;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    // 实例化完成后进行公钥和私钥的读取
    @PostConstruct
    public void init() throws Exception {
        File pubKeyFile = new File(pubKeyPath);
        File priKeyFile = new File(priKeyPath);
        // 如果不存在，则立即生成
        if (!pubKeyFile.exists() || !priKeyFile.exists()) {
            log.info("生成密钥");
            RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
        }
        // 读取公钥和私钥
        publicKey = RsaUtils.getPublicKey(pubKeyPath);
        privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }
}
