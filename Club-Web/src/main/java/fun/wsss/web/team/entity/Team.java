package fun.wsss.web.team.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 社团实体类
 *
 * @author Wsssfun
 * @date 2024/11/1 21:18
 */
@Data
@TableName("team")
public class Team {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String image;
    private String details;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    private String hasTop;
}
