package fun.wsss.web.audit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.audit.entity.AuditLog;
import fun.wsss.web.audit.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志（仅管理员）
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditLogService auditLogService;

    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * 日志列表
     *
     * @param keyword     账号或路径关键字
     * @param method      HTTP 方法
     * @param currentPage 页码
     * @param pageSize    每页条数
     * @param isAdmin     当前用户是否管理员
     * @return 分页结果
     */
    @GetMapping("/list")
    public ResultVo list(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String method,
                         @RequestParam(defaultValue = "1") Integer currentPage,
                         @RequestParam(defaultValue = "20") Integer pageSize,
                         @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        if (isAdmin == null || isAdmin != 1) {
            return ResultUtils.error("只有管理员可以查看操作日志", StatusCode.FORBIDDEN);
        }
        Page<AuditLog> page = new Page<>(currentPage, pageSize);
        QueryWrapper<AuditLog> query = new QueryWrapper<>();
        query.lambda()
                .eq(method != null && !method.isBlank(), AuditLog::getMethod, method)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(AuditLog::getUsername, keyword)
                        .or()
                        .like(AuditLog::getPath, keyword))
                .orderByDesc(AuditLog::getId);
        return ResultUtils.success("查询成功", auditLogService.page(page, query));
    }
}
