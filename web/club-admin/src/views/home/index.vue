<template>
  <div class="home-container" v-loading="loading">
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />

    <!-- 概览数字 -->
    <el-row :gutter="16">
      <el-col :xs="12" :sm="8" :md="4" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ backgroundColor: card.color }">
            <el-icon :size="26"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="16" class="charts">
      <el-col :md="12">
        <el-card>
          <template #header><span>近 7 天报名趋势</span></template>
          <div class="bars" v-if="signupTrend.length">
            <div class="bar-item" v-for="item in signupTrend" :key="item.date">
              <em>{{ item.count }}</em>
              <div class="bar" :style="{ height: barHeight(item.count) + 'px' }"></div>
              <span>{{ item.date }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无报名数据" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :md="12">
        <el-card>
          <template #header><span>内容类型分布</span></template>
          <div class="h-bars" v-if="newsByType.length">
            <div class="h-row" v-for="item in newsByType" :key="item.label">
              <span class="h-label">{{ item.label }}</span>
              <div class="h-track">
                <div class="h-fill" :style="{ width: percent(item.count, newsMax) + '%' }"></div>
              </div>
              <span class="h-value">{{ item.count }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无内容" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新数据 -->
    <el-row :gutter="16" class="tables">
      <el-col :md="12">
        <el-card>
          <template #header><span>最新社团</span></template>
          <el-table :data="latestTeams" stripe size="small">
            <el-table-column prop="name" label="社团名称" />
            <el-table-column prop="createTime" label="创建时间" width="120" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :md="12">
        <el-card>
          <template #header><span>最新内容</span></template>
          <el-table :data="latestNews" stripe size="small">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="createTime" label="发布时间" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import http from '@/http'
import { getListApi as getTeamList } from '@/api/team'
import { getNewsListApi as getNewsList } from '@/api/news'

const loading = ref(false)
const error = ref('')

const counts = ref({
  userCount: 0,
  teamCount: 0,
  categoryCount: 0,
  newsCount: 0,
  signupCount: 0
})
const newsByType = ref<any[]>([])
const signupTrend = ref<any[]>([])
const latestTeams = ref<any[]>([])
const latestNews = ref<any[]>([])

const cards = computed(() => [
  { label: '用户总数', value: counts.value.userCount, color: '#409eff', icon: 'User' },
  { label: '社团总数', value: counts.value.teamCount, color: '#67c23a', icon: 'Histogram' },
  { label: '分类数量', value: counts.value.categoryCount, color: '#e6a23c', icon: 'Operation' },
  { label: '内容总数', value: counts.value.newsCount, color: '#f56c6c', icon: 'DataAnalysis' },
  { label: '报名人次', value: counts.value.signupCount, color: '#8b5cf6', icon: 'Avatar' }
])

const newsMax = computed(() => Math.max(1, ...newsByType.value.map((n: any) => Number(n.count) || 0)))
const trendMax = computed(() => Math.max(1, ...signupTrend.value.map((n: any) => Number(n.count) || 0)))
const barHeight = (value: number) => Math.max(4, Math.round((Number(value) || 0) / trendMax.value * 120))
const percent = (value: number, max: number) => Math.round((Number(value) || 0) / max * 100)

onMounted(async () => {
  loading.value = true
  error.value = ''
  try {
    const [stats, teams, news] = await Promise.all([
      http.get('/api/stats/overview'),
      getTeamList({ currentPage: 1, pageSize: 5, name: '' }).catch(() => null),
      getNewsList({ currentPage: 1, pageSize: 5, title: '', type: '' }).catch(() => null)
    ]) as any[]

    if (stats?.code === 200) {
      const d = stats.data
      counts.value = {
        userCount: d.userCount,
        teamCount: d.teamCount,
        categoryCount: d.categoryCount,
        newsCount: d.newsCount,
        signupCount: d.signupCount
      }
      newsByType.value = d.newsByType || []
      signupTrend.value = d.signupTrend || []
    } else {
      error.value = stats?.msg || '看板数据加载失败'
    }
    if (teams?.code === 200) latestTeams.value = teams.data.records || []
    if (news?.code === 200) latestNews.value = news.data.records || []
  } catch (e) {
    console.error(e)
    error.value = '看板数据加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.home-container {
  padding: 20px;
}

.err { margin-bottom: 16px; }

.stat-card {
  margin-bottom: 16px;

  :deep(.el-card__body) {
    display: flex;
    align-items: center;
    padding: 16px;
  }
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 12px;
  flex-shrink: 0;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}

.charts, .tables {
  margin-top: 4px;
}

.bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 170px;
  padding: 0 4px;
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;

  em {
    font-style: normal;
    font-size: 12px;
    color: #606266;
  }

  span {
    font-size: 11px;
    color: #909399;
  }
}

.bar {
  width: 60%;
  max-width: 34px;
  background: linear-gradient(180deg, #79bbff, #409eff);
  border-radius: 4px 4px 0 0;
  transition: height 0.3s;
}

.h-bars {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 8px 0;
}

.h-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.h-label {
  width: 48px;
  font-size: 13px;
  color: #606266;
  text-align: right;
}

.h-track {
  flex: 1;
  height: 12px;
  background: #f0f2f5;
  border-radius: 6px;
  overflow: hidden;
}

.h-fill {
  height: 100%;
  background: linear-gradient(90deg, #79bbff, #409eff);
  border-radius: 6px;
  transition: width 0.3s;
}

.h-value {
  width: 32px;
  font-size: 12px;
  color: #606266;
}
</style>
