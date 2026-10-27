package fun.wsss.web.signup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.signup.entity.Signup;
import fun.wsss.web.signup.mapper.SignupMapper;
import fun.wsss.web.signup.service.SignupService;
import org.springframework.stereotype.Service;

/**
 * 活动报名 Service 实现
 *
 * @author Wsssfun
 */
@Service
public class SignupServiceImpl extends ServiceImpl<SignupMapper, Signup> implements SignupService {
}
