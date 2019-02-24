package cn.lollipop.user.mapper;

import cn.lollipop.common.mapper.BaseMapper;
import cn.lollipop.user.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserMapper extends BaseMapper<User, Long> {
}
