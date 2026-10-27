package fun.wsss.web.team.entity;


import lombok.Data;

/**
 * 社团分页
 *
 * @author Wsssfun
 * @date 2024/11/1 21:23
 */
@Data
public class TeamPage {
    private Integer currentPage = 1;
    private Integer pageSize = 10;
    private String name;
}
