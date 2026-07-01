package fun.wsss.web.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.wsss.web.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社团成员Mapper
 *
 * @author Wsssfun
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
