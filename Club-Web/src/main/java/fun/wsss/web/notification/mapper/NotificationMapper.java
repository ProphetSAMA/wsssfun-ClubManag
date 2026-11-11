package fun.wsss.web.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.wsss.web.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内通知
 *
 * @author Wsssfun
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
