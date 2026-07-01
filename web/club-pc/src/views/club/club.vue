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
      <div class="grid">
        <div class="team-card" v-for="item in teamList" :key="item.id">
          <el-image
            class="team-img"
            :src="item.image ? 'http://localhost:8888' + item.image : defaultImg"
            fit="cover"
          />
          <div class="team-info">
            <div class="team-name">{{ item.name }}</div>
            <div class="team-date">{{ item.createTime }}</div>
          </div>
        </div>
        <el-empty v-if="teamList.length === 0" description="暂无社团" class="empty-full" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import defaultImg from "@/assets/jitashe.jpg";
import http from "@/http";

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

.page-head {
  h1 {
    font-size: 20px;
    font-weight: 700;
    color: var(--text-1);
  }
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

  &:hover {
    background: var(--bg-hover);
  }

  &.active {
    background: var(--primary-bg);
    color: var(--primary);
    font-weight: 500;
  }
}

.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.empty-full {
  grid-column: 1 / -1;
}

.team-card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: var(--shadow-md);
  }
}

.team-img {
  width: 100%;
  height: 150px;
}

.team-info {
  padding: 10px 12px;
}

.team-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: 4px;
}

.team-date {
  font-size: 12px;
  color: var(--text-3);
}

@media (max-width: 768px) {
  .layout { grid-template-columns: 1fr; }
  .grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
