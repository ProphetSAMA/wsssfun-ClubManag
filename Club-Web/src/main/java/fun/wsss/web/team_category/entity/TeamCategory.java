package fun.wsss.web.team_category.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 活动分类实体类
 *
 * @author Wsssfun
 * @date 2024/10/30 02:20
 */
@Data
@TableName("team_category")
public class TeamCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer orderNum;
}
