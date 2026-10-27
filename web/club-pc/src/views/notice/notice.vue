<template>
  <div class="page">
    <div class="page-head"><h1>公告</h1></div>

    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="按标题搜索"
        clearable
        style="max-width: 260px"
        @keyup.enter="search"
        @clear="reset"
      />
      <el-button type="primary" @click="search">搜索</el-button>
      <el-button @click="reset">重置</el-button>
    </div>

    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />
    <div class="card" v-loading="loading">
      <div class="item" v-for="item in list" :key="item.id" @click="openDetail(item)">
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty
        v-if="list.length === 0"
        :description="keyword ? '没有匹配的公告' : '暂无公告'"
      />
    </div>

    <el-pagination
      v-if="total > size"
      class="pager"
      layout="total, prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="currentPage"
      background
      @current-change="pageChange"
    />
  </div>

  <ContentDetail v-model="detailVisible" :item="current" type-label="公告" />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import ContentDetail from "@/components/ContentDetail.vue";
import { useContentList } from "@/composables/useContentList";
import { newsDetailApi } from "@/api/signup";

const { list, total, currentPage, size, keyword, loading, error, fetchList, search, reset, pageChange } =
  useContentList("2");

/** 详情弹框 */
const detailVisible = ref(false);
const current = ref<any>(null);
const openDetail = async (row: any) => {
  current.value = row;
  detailVisible.value = true;
  try {
    const res = (await newsDetailApi(row.id)) as any;
    if (res && res.code === 200) current.value = res.data;
  } catch (e) {
    console.error(e);
  }
};

onMounted(fetchList);
</script>

<style scoped lang="scss">
.page { display: flex; flex-direction: column; gap: 20px; }
.page-head h1 { font-size: 20px; font-weight: 700; color: var(--text-1); }

.toolbar { display: flex; gap: 10px; align-items: center; }

.pager { justify-content: flex-end; display: flex; }

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
