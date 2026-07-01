<template>
  <div class="club-page">
    <div class="page-header">
      <h1>社团列表</h1>
      <p>发现并加入你感兴趣的社团</p>
    </div>
    <div class="club-layout">
      <!-- 左侧分类 -->
      <aside class="category-sidebar">
        <div class="sidebar-title">社团分类</div>
        <div
          class="cat-item"
          :class="{ active: selectedCategory === null }"
          @click="selectCategory(null)"
        >
          全部
        </div>
        <div
          class="cat-item"
          :class="{ active: selectedCategory === item.value }"
          v-for="item in categoryList"
          :key="item.value"
          @click="selectCategory(item.value)"
        >
          {{ item.label }}
        </div>
        <el-empty v-if="categoryList.length === 0" description="暂无分类" :image-size="40" />
      </aside>

      <!-- 右侧社团列表 -->
      <div class="club-grid">
        <div class="club-card" v-for="item in teamList" :key="item.id">
          <el-image
            class="club-img"
            :src="item.image ? 'http://localhost:8888' + item.image : defaultImg"
            fit="cover"
          />
          <div class="club-body">
            <h3>{{ item.name }}</h3>
            <div class="club-meta">
              <span>📅 {{ item.createTime }}</span>
            </div>
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

const categoryList = ref<any[]>([]);
const teamList = ref<any[]>([]);
const selectedCategory = ref<number | null>(null);

const getCategoryList = async () => {
  try {
    const res = await http.get("/api/category/getSelectList");
    if (res && res.code === 200) categoryList.value = res.data || [];
  } catch (e) { console.error("获取分类失败", e); }
};

const getTeamList = async () => {
  try {
    const res = await http.get("/api/team/list", { currentPage: 1, pageSize: 50 });
    if (res && res.code === 200) teamList.value = res.data.records || [];
  } catch (e) { console.error("获取社团列表失败", e); }
};

const selectCategory = (id: number | null) => {
  selectedCategory.value = id;
};

onMounted(() => {
  getCategoryList();
  getTeamList();
});
</script>

<style scoped lang="scss">
.club-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
}

.page-header {
  h1 {
    font-size: 28px;
    font-weight: 800;
    color: var(--text-primary);
  }
  p {
    color: var(--text-secondary);
    margin-top: 4px;
  }
}

.club-layout {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: var(--space-lg);
}

.category-sidebar {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  padding: var(--space-md);
  height: fit-content;
  position: sticky;
  top: calc(var(--header-height) + var(--space-xl));
}

.sidebar-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-light);
}

.cat-item {
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: var(--bg-hover);
    color: var(--text-primary);
  }

  &.active {
    background: var(--primary-bg);
    color: var(--primary);
    font-weight: 600;
  }
}

.club-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

.club-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-6px);
    box-shadow: var(--shadow-xl);
  }
}

.club-img {
  width: 100%;
  height: 160px;
}

.club-body {
  padding: 14px;

  h3 {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 8px;
  }
}

.club-meta {
  font-size: 12px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .club-layout {
    grid-template-columns: 1fr;
  }
  .club-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
