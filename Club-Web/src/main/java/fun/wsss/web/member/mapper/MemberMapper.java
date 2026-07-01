package fun.wsss.web.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.entity.MemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 社团成员Mapper
 *
 * @author Wsssfun
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    /**
     * 分页查询成员列表（关联社团名称和用户姓名）
     *
     * @param page     分页参数
     * @param teamId   社团ID（可选）
     * @param userId   用户ID（可选）
     * @param userName 成员姓名（模糊搜索，可选）
     * @return 成员列表
     */
    IPage<MemberVo> selectMemberPage(Page<MemberVo> page,
                                     @Param("teamId") Long teamId,
                                     @Param("userId") Long userId,
                                     @Param("userName") String userName);
}
