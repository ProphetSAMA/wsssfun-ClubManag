<template>
  <div class="page">
    <div class="page-head"><h1>活动</h1></div>
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />
    <div class="card" v-loading="loading">
      <div class="item" v-for="item in list" :key="item.id" @click="openDetail(item)">
        <el-image
          v-if="item.image"
          class="item-img"
          :src="imgUrl(item.image)"
          fit="cover"
        />
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty v-if="list.length === 0" description="暂无活动" />
    </div>
  </div>

  <ContentDetail v-model="detailVisible" :item="current" type-label="活动" />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import http from "@/http";
import { imgUrl } from "@/utils/image";
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
    const res = await http.get("/api/news/list", { currentPage: 1, pageSize: 50, type: "0", title: "" });
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

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-1);
    margin-bottom: 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.date { font-size: 12px; color: var(--text-3); }
</style>
