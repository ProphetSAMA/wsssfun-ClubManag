package fun.wsss.web.news.entity;


import lombok.Data;

/**
 * @author Wsssfun
 * @date 2024/11/19 01:47
 */
@Data
public class NewParm {
    private Integer currentPage = 1;
    private Integer pageSize = 10;
    private String title;
    private String type;
}
