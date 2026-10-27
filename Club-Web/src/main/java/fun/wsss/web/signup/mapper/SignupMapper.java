package fun.wsss.web.signup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.web.signup.entity.Signup;
import fun.wsss.web.signup.entity.SignupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动报名 Mapper
 *
 * @author Wsssfun
 */
@Mapper
public interface SignupMapper extends BaseMapper<Signup> {

    /**
     * 我报名的活动（只返回已报名且活动已发布的）
     *
     * @param page   分页
     * @param userId 当前用户
     * @return 分页结果
     */
    IPage<SignupVo> selectMySignups(Page<SignupVo> page, @Param("userId") Long userId);

    /**
     * 某个活动的报名名单（管理员用）
     *
     * @param page   分页
     * @param newsId 活动ID
     * @return 分页结果
     */
    IPage<SignupVo> selectSignupsByNews(Page<SignupVo> page, @Param("newsId") Long newsId);
}
