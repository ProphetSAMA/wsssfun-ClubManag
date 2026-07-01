<template>
  <div class="page">
    <div class="page-header">
      <h1>📰 新闻</h1>
      <p>了解社团最新动态</p>
    </div>
    <div class="news-grid">
      <div class="news-card" v-for="item in list" :key="item.id">
        <el-image
          v-if="item.image"
          class="card-img"
          :src="'http://localhost:8888' + item.image"
          fit="cover"
        />
        <div class="card-img placeholder" v-else>📰</div>
        <div class="card-body">
          <h3>{{ item.title }}</h3>
          <span class="card-date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty v-if="list.length === 0" description="暂无新闻" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import http from "@/http";

const list = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await http.get("/api/news/list", { currentPage: 1, pageSize: 50, type: "1", title: "" });
    if (res && res.code === 200) list.value = res.data.records || [];
  } catch (e) { console.error("获取新闻列表失败", e); }
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

.news-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

.news-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }
}

.card-img {
  width: 100%;
  height: 140px;

  &.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--primary-bg);
    font-size: 36px;
  }
}

.card-body {
  padding: 14px;

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.card-date {
  font-size: 12px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .news-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
