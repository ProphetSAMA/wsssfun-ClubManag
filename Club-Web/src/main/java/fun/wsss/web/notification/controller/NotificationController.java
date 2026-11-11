package fun.wsss.web.notification.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.notification.entity.Notification;
import fun.wsss.web.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 站内通知
 *
 * <p>所有接口只看/只改自己的通知，userId 一律取自 token。
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 我的通知
     *
     * @param currentPage 页码
     * @param pageSize    每页条数
     * @param userId      当前用户
     * @return 分页结果
     */
    @GetMapping("/my")
    public ResultVo my(@RequestParam(defaultValue = "1") Integer currentPage,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        Page<Notification> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.lambda().eq(Notification::getUserId, userId).orderByDesc(Notification::getCreateTime);
        return ResultUtils.success("查询成功", notificationService.page(page, query));
    }

    /**
     * 未读数量（顶部铃铛用）
     *
     * @param userId 当前用户
     * @return 数量
     */
    @GetMapping("/unread")
    public ResultVo unread(@RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.success("查询成功", 0);
        }
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.lambda().eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0);
        return ResultUtils.success("查询成功", notificationService.count(query));
    }

    /**
     * 标记一条已读
     *
     * @param id     通知ID
     * @param userId 当前用户
     * @return 结果
     */
    @PutMapping("/{id}/read")
    public ResultVo read(@PathVariable("id") Long id,
                         @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        Notification notification = notificationService.getById(id);
        if (notification == null || !Objects.equals(notification.getUserId(), userId)) {
            return ResultUtils.error("通知不存在");
        }
        if (Objects.equals(notification.getIsRead(), 1)) {
            return ResultUtils.success("已读");
        }
        notification.setIsRead(1);
        return notificationService.updateById(notification)
                ? ResultUtils.success("已读")
                : ResultUtils.error("操作失败");
    }

    /**
     * 全部标记已读
     *
     * @param userId 当前用户
     * @return 结果
     */
    @PutMapping("/read-all")
    public ResultVo readAll(@RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        UpdateWrapper<Notification> update = new UpdateWrapper<>();
        update.lambda().eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1);
        notificationService.update(update);
        return ResultUtils.success("已全部标记为已读");
    }
}
