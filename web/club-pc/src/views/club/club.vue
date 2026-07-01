<template>
  <div class="club-page">
    <div class="page-head">
      <h1>社团</h1>
    </div>
    <div class="layout">
      <aside class="sidebar">
        <div class="sidebar-title">分类</div>
        <div
          class="cat"
          :class="{ active: selectedCategory === null }"
          @click="selectCategory(null)"
        >全部</div>
        <div
          class="cat"
          :class="{ active: selectedCategory === item.value }"
          v-for="item in categoryList"
          :key="item.value"
          @click="selectCategory(item.value)"
        >{{ item.label }}</div>
      </aside>
      <div class="card">
        <div class="item" v-for="item in teamList" :key="item.id">
          <el-image
            class="item-img"
            :src="item.image ? imgUrl(item.image) : defaultImg"
            fit="cover"
          />
          <div class="item-body">
            <div class="item-name">{{ item.name }}</div>
            <span class="date">{{ item.createTime }}</span>
          </div>
        </div>
        <el-empty v-if="teamList.length === 0" description="暂无社团" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import defaultImg from "@/assets/jitashe.jpg";
import http from "@/http";

const BASE = "http://localhost:8888";
const imgUrl = (image: string) => {
  if (!image) return "";
  let url = image.split(",")[0].trim();
  if (url.startsWith("undefined")) url = url.replace("undefined", "");
  if (url.startsWith("/")) url = BASE + url;
  return url;
};
const categoryList = ref<any[]>([]);
const teamList = ref<any[]>([]);
const selectedCategory = ref<number | null>(null);

onMounted(async () => {
  try {
    const [c, t] = await Promise.all([
      http.get("/api/category/getSelectList"),
      http.get("/api/team/list", { currentPage: 1, pageSize: 50 }),
    ]);
    if (c?.code === 200) categoryList.value = c.data || [];
    if (t?.code === 200) teamList.value = t.data.records || [];
  } catch (e) { console.error(e); }
});

const selectCategory = (id: number | null) => {
  selectedCategory.value = id;
};
</script>

<style scoped lang="scss">
.club-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-head h1 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-1);
}

.layout {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 20px;
}

.sidebar {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 12px;
  height: fit-content;
  position: sticky;
  top: calc(var(--header-h) + 24px);
}

.sidebar-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-1);
  padding-bottom: 10px;
  margin-bottom: 6px;
  border-bottom: 1px solid var(--border-light);
}

.cat {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: var(--text-2);
  cursor: pointer;
  transition: all 0.15s;

  &:hover { background: var(--bg-hover); }
  &.active { background: var(--primary-bg); color: var(--primary); font-weight: 500; }
}

.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.15s;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); }
}

.item-img {
  width: 110px;
  height: 75px;
  border-radius: 6px;
  flex-shrink: 0;
  object-fit: cover;
}

.item-body {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: 6px;
}

.date { font-size: 12px; color: var(--text-3); }

@media (max-width: 768px) {
  .layout { grid-template-columns: 1fr; }
}
</style>
