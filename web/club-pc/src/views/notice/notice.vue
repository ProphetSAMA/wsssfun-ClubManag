<template>
  <div class="page">
    <div class="page-head"><h1>公告</h1></div>
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />
    <div class="card" v-loading="loading">
      <div class="item" v-for="item in list" :key="item.id" @click="openDetail(item)">
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty v-if="list.length === 0" description="暂无公告" />
    </div>
  </div>

  <ContentDetail v-model="detailVisible" :item="current" type-label="公告" />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import http from "@/http";
import ContentDetail from "@/components/ContentDetail.vue";

const list = ref<any[]>([]);
const loading = ref(true);
const error = ref("");
/** 详情弹框 */
const detailVisible = ref(false);
const current = ref<any>(null);

const openDetail = (row: any) => {
  current.value = row;
  detailVisible.value = true;
};

onMounted(async () => {
  try {
    const res = await http.get("/api/news/list", { currentPage: 1, pageSize: 50, type: "2", title: "" });
    if (res && res.code === 200) list.value = res.data.records || [];
  } catch (e) {
    console.error(e);
    error.value = "内容加载失败，请稍后重试";
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.page { display: flex; flex-direction: column; gap: 20px; }
.page-head h1 { font-size: 20px; font-weight: 700; color: var(--text-1); }

.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.item {
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.15s;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); }

  h3 {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-1);
    margin-bottom: 4px;
  }
}

.date { font-size: 12px; color: var(--text-3); }
</style>
