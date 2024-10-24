package fun.wsss.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.wsss.web.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author Wsssfun
 * @date 2024/10/23 23:19
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
