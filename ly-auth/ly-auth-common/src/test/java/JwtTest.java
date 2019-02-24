import cn.lollipop.auth.pojo.UserInfo;
import cn.lollipop.auth.utils.JwtUtils;
import cn.lollipop.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {
    private static final String pubKeyPath = "G:/test/rsa.pub";
    private static final String priKeyPath = "G:/test/rsa.pri";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU1MDk5MTU2OH0.VUON8zzx3sQjT030j8P0NQ5gDnEkWJOy7ga0A9kkuy50gATK2edujM2soeQrpt1k3LOB2osA8K5HRXd-EQcTm7TekhlAS5qxwcPkeeWEcwLg1Ral6qJSz2xMXnYeAwf7WSoGvSlg68B3rBfNroj28xcZmw6kzxudyhtJ88reA-k";
        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}