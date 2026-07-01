package fun.wsss.web.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import org.springframework.stereotype.Service;

/**
 * 社团成员Service实现类
 *
 * @author Wsssfun
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
}
