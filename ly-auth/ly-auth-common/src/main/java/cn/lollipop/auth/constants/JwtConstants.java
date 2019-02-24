package cn.lollipop.auth.constants;

import lombok.Data;

@Data
public abstract class JwtConstants {
    public static final String JWT_KEY_ID = "id";
    public static final String JWT_KEY_USER_NAME = "username";
}