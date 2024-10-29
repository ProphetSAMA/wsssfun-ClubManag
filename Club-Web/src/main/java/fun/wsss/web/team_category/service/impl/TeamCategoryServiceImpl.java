package fun.wsss.web.team_category.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.team_category.entity.TeamCategory;
import fun.wsss.web.team_category.mapper.TeamCategoryMapper;
import fun.wsss.web.team_category.service.TeamCategoryService;
import org.springframework.stereotype.Service;

/**
 * 活动分类服务实现类
 *
 * @author Wsssfun
 * @date 2024/10/30 02:28
 */
@Service
public class TeamCategoryServiceImpl extends ServiceImpl<TeamCategoryMapper, TeamCategory> implements TeamCategoryService {
}
