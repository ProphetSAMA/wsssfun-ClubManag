package fun.wsss.web.team.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.entity.TeamMemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社团mapper
 *
 * @author Wsssfun
 * @date 2024/11/1 21:22
 */
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 社团在册成员（不含已退出）
     *
     * @param teamId 社团ID
     * @return 成员列表，社长排前面
     */
    List<TeamMemberVo> selectMembers(@Param("teamId") Long teamId);

}
