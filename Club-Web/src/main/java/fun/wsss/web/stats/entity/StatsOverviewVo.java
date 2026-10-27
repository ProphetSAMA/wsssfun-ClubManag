package fun.wsss.web.stats.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 后台看板数据
 *
 * @author Wsssfun
 */
@Data
public class StatsOverviewVo {
    private long userCount;
    private long teamCount;
    private long categoryCount;
    private long newsCount;
    private long signupCount;

    /** 内容类型分布：[{ label, count }] */
    private List<Map<String, Object>> newsByType;
    /** 社团分类分布：[{ label, count }] */
    private List<Map<String, Object>> teamByCategory;
    /** 近 7 天报名趋势：[{ date, count }] */
    private List<Map<String, Object>> signupTrend;
}
