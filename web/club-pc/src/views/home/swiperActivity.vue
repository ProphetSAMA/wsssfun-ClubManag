<template>
  <el-carousel height="200px" :interval="4000" indicator-position="outside">
    <el-carousel-item v-for="item in list" :key="item.id">
      <el-image
        class="carousel-img"
        :src="item.image ? imgUrl(item.image) : defaultImg"
        fit="cover"
      />
    </el-carousel-item>
  </el-carousel>
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

<style scoped>
.carousel-img {
  width: 100%;
  height: 200px;
  border-radius: 8px;
}
</style>
