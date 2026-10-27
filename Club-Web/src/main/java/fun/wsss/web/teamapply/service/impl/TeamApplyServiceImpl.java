package fun.wsss.web.teamapply.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.teamapply.entity.TeamApply;
import fun.wsss.web.teamapply.mapper.TeamApplyMapper;
import fun.wsss.web.teamapply.service.TeamApplyService;
import org.springframework.stereotype.Service;

/**
 * 社团申请
 *
 * @author Wsssfun
 */
@Service
public class TeamApplyServiceImpl extends ServiceImpl<TeamApplyMapper, TeamApply> implements TeamApplyService {
}
