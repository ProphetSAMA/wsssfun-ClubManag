package fun.wsss.web.user.entity;


import lombok.Data;

/**
 * 分页参数
 *
 * @author Wsssfun
 * @date 2024/10/24 00:33
 */
@Data
public class PageParm {
    private Long currentPage = 1L;
    private Long pageSize = 10L;
    private String nickName;

}
