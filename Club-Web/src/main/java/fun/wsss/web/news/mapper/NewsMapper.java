package fun.wsss.web.news.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.wsss.web.news.entity.News;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Wsssfun
 * @date 2024/11/19 01:38
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {
}
