package fun.wsss.web.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.notification.entity.Notification;
import fun.wsss.web.notification.mapper.NotificationMapper;
import fun.wsss.web.notification.service.NotificationService;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 站内通知
 *
 * <p>通知是业务动作的附属产物，发送失败不应该影响报名/审核本身，所以这里吞掉异常只记日志。
 *
 * @author Wsssfun
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final UserService userService;

    public NotificationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void push(Long userId, String type, String title, String content, String link) {
        if (userId == null) {
            return;
        }
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType(type == null ? "system" : type);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setLink(link);
            notification.setIsRead(0);
            notification.setCreateTime(new Date());
            save(notification);
        } catch (Exception e) {
            log.warn("发送通知失败 userId={} title={}", userId, title, e);
        }
    }

    @Override
    public void pushToAdmins(String type, String title, String content, String link) {
        try {
            QueryWrapper<User> query = new QueryWrapper<>();
            query.lambda().eq(User::getIsAdmin, 1).select(User::getUserId);
            List<User> admins = userService.list(query);
            for (User admin : admins) {
                push(admin.getUserId(), type, title, content, link);
            }
        } catch (Exception e) {
            log.warn("给管理员发通知失败 title={}", title, e);
        }
    }
}
