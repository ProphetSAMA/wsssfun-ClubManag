package fun.wsss.web.news.entity;


import lombok.Data;

/**
 * @author Wsssfun
 * @date 2024/11/19 01:47
 */
@Data
public class NewParm {
    private Integer currentPage;
    private Integer pageSize;
    private String title;
    private String type;
}
