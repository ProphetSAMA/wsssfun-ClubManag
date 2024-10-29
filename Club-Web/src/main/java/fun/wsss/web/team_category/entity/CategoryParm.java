package fun.wsss.web.team_category.entity;


import lombok.Data;

/**
 * 活动分类参数实体类
 *
 * @author Wsssfun
 * @date 2024/10/30 02:40
 */

@Data
public class CategoryParm {
    private Integer currentPage;
    private Integer pageSize;
    private String name;
}
