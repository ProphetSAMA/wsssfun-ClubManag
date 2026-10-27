package fun.wsss.web.teamapply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.web.teamapply.entity.TeamApply;
import fun.wsss.web.teamapply.entity.TeamApplyVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 社团申请
 *
 * @author Wsssfun
 */
@Mapper
public interface TeamApplyMapper extends BaseMapper<TeamApply> {

    /**
     * 后台申请列表
     *
     * @param page   分页
     * @param status 状态筛选，null 表示全部
     * @param keyword 社团名/申请人关键字
     * @return 分页结果
     */
    IPage<TeamApplyVo> selectApplyPage(Page<TeamApplyVo> page,
                                       @Param("status") Integer status,
                                       @Param("keyword") String keyword);
}
