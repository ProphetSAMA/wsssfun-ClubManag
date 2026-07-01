<template>
  <div class="myactivity-container">
    <el-card>
      <template #header>
        <span>最新活动</span>
      </template>
      <div class="activity-item" v-for="item in activityList" :key="item.id">
        <el-image
          v-if="item.image"
          class="activity-img"
          :src="'http://localhost:8888' + item.image"
          fit="cover"
        />
        <div class="activity-info">
          <div class="activity-title">{{ item.title }}</div>
          <div class="activity-time">{{ item.createTime }}</div>
        </div>
      </div>
      <el-empty v-if="activityList.length === 0" description="暂无活动" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import http from '@/http'

const activityList = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await http.get('/api/news/list', {
      currentPage: 1,
      pageSize: 20,
      type: '0',
      title: ''
    }) as any
    if (res && res.code === 200) {
      activityList.value = res.data.records || []
    }
  } catch (e) {
    console.error('获取活动列表失败', e)
  }
})
</script>

<style scoped>
.myactivity-container {
  padding: 10px;
}

.activity-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #e4e7ed;
  cursor: pointer;
}

.activity-img {
  width: 160px;
  height: 90px;
  border-radius: 8px;
  flex-shrink: 0;
}

.activity-info {
  margin-left: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.activity-title {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
}

.activity-time {
  font-size: 13px;
  color: #909399;
}
</style>
