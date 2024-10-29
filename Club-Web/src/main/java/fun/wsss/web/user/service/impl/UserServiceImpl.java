package fun.wsss.web.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.mapper.UserMapper;
import fun.wsss.web.user.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 *
 * @author Wsssfun
 * @date 2024/10/23 23:27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
