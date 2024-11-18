package fun.wsss.web.news.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.mapper.NewsMapper;
import fun.wsss.web.news.service.NewsService;
import org.springframework.stereotype.Service;

/**
 * @author Wsssfun
 * @date 2024/11/19 01:40
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

}
