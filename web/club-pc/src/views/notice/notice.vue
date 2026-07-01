<template>
  <div class="page">
    <div class="page-header">
      <h1>📢 公告</h1>
      <p>重要通知与公告</p>
    </div>
    <div class="list-card">
      <div class="list-item" v-for="item in list" :key="item.id">
        <div class="item-icon">📢</div>
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="item-date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty v-if="list.length === 0" description="暂无公告" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import http from "@/http";

const list = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await http.get("/api/news/list", { currentPage: 1, pageSize: 50, type: "2", title: "" });
    if (res && res.code === 200) list.value = res.data.records || [];
  } catch (e) { console.error("获取公告列表失败", e); }
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

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: var(--primary-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.item-body {
  flex: 1;
  min-width: 0;

  h3 {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 4px;
  }
}

.item-date {
  font-size: 13px;
  color: var(--text-muted);
}
</style>
