<template>
  <div class="page">
    <div class="page-head"><h1>新闻</h1></div>
    <div class="grid">
      <div class="card" v-for="item in list" :key="item.id">
        <el-image
          v-if="item.image"
          class="card-img"
          :src="'http://localhost:8888' + item.image"
          fit="cover"
        />
        <div class="card-img placeholder" v-else></div>
        <div class="card-body">
          <h3>{{ item.title }}</h3>
          <span class="date">{{ item.createTime }}</span>
        </div>
      </div>
      <el-empty v-if="list.length === 0" description="暂无新闻" class="empty-full" />
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
  } catch (e) { console.error(e); }
});
</script>

<style scoped lang="scss">
.page { display: flex; flex-direction: column; gap: 20px; }
.page-head h1 { font-size: 20px; font-weight: 700; color: var(--text-1); }

.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;

  &:hover { box-shadow: var(--shadow-md); }
}

.card-img {
  width: 100%;
  height: 130px;

  &.placeholder { background: var(--bg); }
}

.card-body {
  padding: 10px 12px;

  h3 {
    font-size: 13px;
    font-weight: 600;
    color: var(--text-1);
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.date { font-size: 12px; color: var(--text-3); }

@media (max-width: 768px) {
  .grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
