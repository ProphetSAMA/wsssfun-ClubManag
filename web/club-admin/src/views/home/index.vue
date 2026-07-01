<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background-color: #409eff">
            <el-icon :size="30"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background-color: #67c23a">
            <el-icon :size="30"><Histogram /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.teamCount }}</div>
            <div class="stat-label">社团总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background-color: #e6a23c">
            <el-icon :size="30"><Operation /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.categoryCount }}</div>
            <div class="stat-label">分类数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background-color: #f56c6c">
            <el-icon :size="30"><DataAnalysis /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.newsCount }}</div>
            <div class="stat-label">新闻活动</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新社团</span>
          </template>
          <el-table :data="latestTeams" stripe size="small">
            <el-table-column prop="name" label="社团名称" />
            <el-table-column prop="createTime" label="创建时间" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新活动</span>
          </template>
          <el-table :data="latestNews" stripe size="small">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="createTime" label="发布时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getListApi as getUserList } from '@/api/user'
import { getListApi as getTeamList } from '@/api/team'
import { getCategoryApi as getCategoryList } from '@/api/category'
import { getNewsListApi as getNewsList } from '@/api/news'

const stats = reactive({
  userCount: 0,
  teamCount: 0,
  categoryCount: 0,
  newsCount: 0
})

const latestTeams = ref<any[]>([])
const latestNews = ref<any[]>([])

onMounted(async () => {
  try {
    const [userRes, teamRes, catRes, newsRes] = await Promise.all([
      getUserList({ currentPage: 1, pageSize: 1, nickName: '' }),
      getTeamList({ currentPage: 1, pageSize: 5, name: '' }),
      getCategoryList({ currentPage: 1, pageSize: 1, name: '' }),
      getNewsList({ currentPage: 1, pageSize: 5, title: '', type: '' })
    ])
    if (userRes?.code === 200) stats.userCount = userRes.data.total
    if (teamRes?.code === 200) {
      stats.teamCount = teamRes.data.total
      latestTeams.value = teamRes.data.records
    }
    if (catRes?.code === 200) stats.categoryCount = catRes.data.total
    if (newsRes?.code === 200) {
      stats.newsCount = newsRes.data.total
      latestNews.value = newsRes.data.records
    }
  } catch (e) {
    console.error('加载首页数据失败', e)
  }
})
</script>

<style scoped lang="scss">
.home-container {
  padding: 20px;
}

.stat-card {
  :deep(.el-card__body) {
    display: flex;
    align-items: center;
    padding: 20px;
  }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.stat-info {
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
  }
  .stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
  }
}
</style>
