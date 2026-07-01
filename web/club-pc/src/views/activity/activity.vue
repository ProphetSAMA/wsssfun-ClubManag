<template>
  <div class="page">
    <div class="page-header">
      <h1>🎯 活动</h1>
      <p>丰富多彩的社团活动等你参与</p>
    </div>
    <div class="list-card">
      <div class="list-item" v-for="item in list" :key="item.id">
        <el-image
          v-if="item.image"
          class="item-img"
          :src="'http://localhost:8888' + item.image"
          fit="cover"
        />
        <div class="item-img placeholder" v-else>🎯</div>
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="item-date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty v-if="list.length === 0" description="暂无活动" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import http from "@/http";

const list = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await http.get("/api/news/list", { currentPage: 1, pageSize: 50, type: "0", title: "" });
    if (res && res.code === 200) list.value = res.data.records || [];
  } catch (e) { console.error("获取活动列表失败", e); }
});
</script>

<style scoped lang="scss">
.page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
}

.page-header {
  h1 { font-size: 28px; font-weight: 800; color: var(--text-primary); }
  p { color: var(--text-secondary); margin-top: 4px; }
}

.list-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.2s;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); }
}

.item-img {
  width: 120px;
  height: 80px;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  object-fit: cover;

  &.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--primary-bg);
    font-size: 28px;
  }
}

.item-body {
  flex: 1;
  min-width: 0;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.item-date {
  font-size: 13px;
  color: var(--text-muted);
}
</style>
