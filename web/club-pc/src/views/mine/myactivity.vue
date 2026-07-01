<template>
  <div class="card">
    <div class="card-title">最新活动</div>
    <div class="list">
      <div class="item" v-for="item in activityList" :key="item.id">
        <el-image
          v-if="item.image"
          class="item-img"
          :src="'http://localhost:8888' + item.image"
          fit="cover"
        />
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="date">{{ item.createTime }}</span>
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
  } catch (e) { console.error(e) }
})
</script>

<style scoped lang="scss">
.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 20px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-light);
}

.list {
  display: flex;
  flex-direction: column;
}

.item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.15s;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); margin: 0 -12px; padding: 12px; border-radius: 6px; }
}

.item-img {
  width: 100px;
  height: 68px;
  border-radius: 6px;
  flex-shrink: 0;
}

.item-body {
  flex: 1;
  min-width: 0;

  h3 {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-1);
    margin-bottom: 4px;
  }
}

.date { font-size: 12px; color: var(--text-3); }
</style>
