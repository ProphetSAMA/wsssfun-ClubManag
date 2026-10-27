package fun.wsss.web.stats.controller;

import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.stats.entity.StatsOverviewVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台看板统计
 *
 * <p>只有管理员能访问（由 AdminInterceptor 的只读白名单拦截）。
 * 直接用 JdbcTemplate 跑聚合查询，避免为了几个数字再写一套 Mapper。
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private static final Map<String, String> NEWS_TYPE_LABELS = Map.of(
            "0", "活动",
            "1", "新闻",
            "2", "公告"
    );

    private final JdbcTemplate jdbc;

    public StatsController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 看板概览
     *
     * @return 各项数量 + 分布 + 近 7 天报名趋势
     */
    @GetMapping("/overview")
    public ResultVo overview() {
        StatsOverviewVo vo = new StatsOverviewVo();
        vo.setUserCount(count("SELECT COUNT(*) FROM sys_user"));
        vo.setTeamCount(count("SELECT COUNT(*) FROM team"));
        vo.setCategoryCount(count("SELECT COUNT(*) FROM team_category"));
        vo.setNewsCount(count("SELECT COUNT(*) FROM news"));
        vo.setSignupCount(count("SELECT COUNT(*) FROM activity_signup WHERE status = 1"));

        List<Map<String, Object>> newsByType = new ArrayList<>();
        for (Map<String, Object> row : jdbc.queryForList("SELECT type, COUNT(*) AS c FROM news GROUP BY type")) {
            String type = row.get("type") == null ? "" : String.valueOf(row.get("type"));
            newsByType.add(Map.of(
                    "label", NEWS_TYPE_LABELS.getOrDefault(type, "其它"),
                    "count", toLong(row.get("c"))
            ));
        }
        vo.setNewsByType(newsByType);

        List<Map<String, Object>> teamByCategory = new ArrayList<>();
        for (Map<String, Object> row : jdbc.queryForList(
                "SELECT c.name AS name, COUNT(t.id) AS c FROM team_category c "
                        + "LEFT JOIN team t ON t.category_id = c.id GROUP BY c.id, c.name ORDER BY c.order_num")) {
            teamByCategory.add(Map.of(
                    "label", row.get("name") == null ? "未分类" : String.valueOf(row.get("name")),
                    "count", toLong(row.get("c"))
            ));
        }
        vo.setTeamByCategory(teamByCategory);

        vo.setSignupTrend(buildSignupTrend());
        return ResultUtils.success("查询成功", vo);
    }

    /**
     * 近 7 天报名趋势：没有报名的日期补 0，前端不用再处理空洞
     */
    private List<Map<String, Object>> buildSignupTrend() {
        Map<String, Long> counted = new LinkedHashMap<>();
        for (Map<String, Object> row : jdbc.queryForList(
                "SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS d, COUNT(*) AS c FROM activity_signup "
                        + "WHERE status = 1 AND create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY d")) {
            counted.put(String.valueOf(row.get("d")), toLong(row.get("c")));
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter display = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            String key = day.toString();
            trend.add(Map.of(
                    "date", day.format(display),
                    "count", counted.getOrDefault(key, 0L)
            ));
        }
        return trend;
    }

    private long count(String sql) {
        Long value = jdbc.queryForObject(sql, Long.class);
        return value == null ? 0L : value;
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        return ((Number) value).longValue();
    }
}
