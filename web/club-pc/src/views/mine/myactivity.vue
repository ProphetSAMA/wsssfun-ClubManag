<template>
  <div class="info-card">
    <div class="card-title">最新活动</div>
    <div class="activity-list">
      <div class="activity-item" v-for="item in activityList" :key="item.id">
        <el-image
          v-if="item.image"
          class="activity-img"
          :src="'http://localhost:8888' + item.image"
          fit="cover"
        />
        <div class="activity-img ph" v-else>🎯</div>
        <div class="activity-body">
          <h3>{{ item.title }}</h3>
          <span class="activity-date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty v-if="activityList.length === 0" description="暂无活动" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import http from '@/http'

const activityList = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await http.get('/api/news/list', { currentPage: 1, pageSize: 20, type: '0', title: '' }) as any
    if (res && res.code === 200) activityList.value = res.data.records || []
  } catch (e) { console.error('获取活动列表失败', e) }
})
</script>

<style scoped lang="scss">
.info-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  padding: var(--space-lg);
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--space-lg);
  padding-bottom: var(--space-md);
  border-bottom: 1px solid var(--border-light);
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.2s;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); margin: 0 -16px; padding: 14px 16px; border-radius: var(--radius-sm); }
}

.activity-img {
  width: 100px;
  height: 70px;
  border-radius: var(--radius-sm);
  flex-shrink: 0;

  &.ph {
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--primary-bg);
    font-size: 24px;
  }
}

.activity-body {
  flex: 1;
  min-width: 0;

  h3 {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 6px;
  }
}

.activity-date {
  font-size: 13px;
  color: var(--text-muted);
}
</style>
