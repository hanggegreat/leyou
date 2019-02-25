package cn.lollipop.user.service;

import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.common.util.NumberUtils;
import cn.lollipop.user.mapper.UserMapper;
import cn.lollipop.user.pojo.User;
import cn.lollipop.user.utils.CodecUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private static final String KEY_PREFIX = "user:verify:phone";


    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public UserService(UserMapper userMapper, StringRedisTemplate redisTemplate, AmqpTemplate amqpTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.amqpTemplate = amqpTemplate;
    }

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        if (type == 1) {
            user.setUsername(data);
        } else if (type == 2) {
            user.setPhone(data);
        } else {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }
        return userMapper.selectCount(user) == 0;
    }

    public void sendCode(String phone) {
        // 生成验证码
        String code = NumberUtils.generateCode(6);
        String time = "5";
        Map<String, Object> map = Maps.newHashMap();
        map.put("phoneNumber", phone);
        map.put("params", new String[]{code, time});
        // 发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", map);
        // 存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }

    public void register(User user, String code) {
        // 从redis取出验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        // 校验验证码
        if (!StringUtils.equals(code, cacheCode)) {
            throw new LyException(ExceptionConstant.INVALID_VERIFY_CODE);
        }
        // 进行密码加密
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        user.setCreated(new Date());
        userMapper.insert(user);
    }

    public User queryUserByUsernameAndPassword(String username, String password) {
        // 查询用户
        User user = new User();
        user.setUsername(username);
        user = userMapper.selectOne(user);

        // 校验
        if (user == null || !StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password, user.getSalt()))) {
            throw new LyException(ExceptionConstant.INVALID_USERNAME_OR_PASSWORD);
        }

        return user;
    }
}
