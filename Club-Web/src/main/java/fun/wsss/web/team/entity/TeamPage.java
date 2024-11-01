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
    private Integer currentPage;
    private Integer pageSize;
    private String name;
}
