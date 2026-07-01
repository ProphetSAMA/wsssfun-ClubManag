<template>
  <div class="notice-container">
    <div class="news-item" v-for="item in list" :key="item.id">
      <el-image
        v-if="item.image"
        class="image"
        :src="'http://localhost:8888' + item.image"
        fit="cover"
      />
      <div class="news-intr">
        <div class="news-title">{{ item.title }}</div>
        <div class="news-info">
          <span class="news-time">{{ item.createTime }}</span>
        </div>
      </div>
    </div>
    <el-empty v-if="list.length === 0" description="暂无公告" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import http from "@/http";

const list = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await http.get("/api/news/list", {
      currentPage: 1,
      pageSize: 50,
      type: "2",
      title: "",
    });
    if (res && res.code === 200) {
      list.value = res.data.records || [];
    }
  } catch (e) {
    console.error("获取公告列表失败", e);
  }
});
</script>

<style scoped lang="scss">
.notice-container {
  padding: 10px;
}

.news-item {
  display: flex;
  height: 110px;
  padding: 15px 0;
  border-bottom: 1px solid #e4e7ed;
  cursor: pointer;

  .image {
    height: 100px;
    width: 200px;
    border-radius: 10px;
    flex-shrink: 0;
  }

  .news-intr {
    margin-left: 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .news-title {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
    }

    .news-info {
      font-size: 13px;
      color: #909399;
    }
  }
}
</style>
