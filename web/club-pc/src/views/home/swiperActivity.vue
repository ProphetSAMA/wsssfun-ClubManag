<template>
  <el-carousel height="250px">
    <el-carousel-item v-for="item in list" :key="item.id">
      <el-image
        style="height: 250px; width: 100%; object-fit: cover"
        :src="item.image ? 'http://localhost:8888' + item.image : defaultImg"
        fit="cover"
      />
    </el-carousel-item>
  </el-carousel>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import defaultImg from "@/assets/jitashe.jpg";
import http from "@/http";

const list = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await http.get("/api/news/getTopActivity");
    if (res && res.code === 200) {
      list.value = res.data || [];
    }
  } catch (e) {
    console.error("获取推荐活动失败", e);
  }
});
</script>
