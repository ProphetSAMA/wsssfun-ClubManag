package fun.wsss.web.news.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.wsss.web.news.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author Wsssfun
 * @date 2024/11/19 01:38
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {

    /**
     * 浏览量 +1（直接走 SQL，避免并发下读改写丢失）
     *
     * @param id 内容ID
     * @return 影响行数
     */
    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{id}")
    int increaseView(@Param("id") Long id);
}
