package fun.wsss.web.notification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.wsss.web.notification.entity.Notification;

/**
 * 站内通知
 *
 * @author Wsssfun
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 给一个用户发通知
     *
     * @param userId  接收人
     * @param type    类型
     * @param title   标题
     * @param content 内容
     * @param link    前端路由（可为空）
     */
    void push(Long userId, String type, String title, String content, String link);

    /**
     * 给所有平台管理员发通知
     *
     * @param type    类型
     * @param title   标题
     * @param content 内容
     * @param link    前端路由
     */
    void pushToAdmins(String type, String title, String content, String link);
}
