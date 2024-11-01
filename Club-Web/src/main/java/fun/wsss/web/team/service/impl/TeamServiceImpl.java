package fun.wsss.web.team.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.mapper.TeamMapper;
import fun.wsss.web.team.service.TeamService;
import org.springframework.stereotype.Service;

/**
 * @author Wsssfun
 * @date 2024/11/1 21:25
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
}
