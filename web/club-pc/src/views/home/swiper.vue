<template>
  <swiper
    :modules="modules"
    :initial-slide="1"
    :slides-per-view="3"
    :centered-slides="true"
    :space-between="20"
    :autoplay="{ disableOnInteraction: false }"
    loop
    class="mySwiper"
  >
    <swiper-slide v-for="item in list" :key="item.id">
      <el-image
        style="height: 250px; width: 100%; object-fit: cover"
        :src="item.image ? 'http://localhost:8888' + item.image : defaultImg"
        fit="cover"
      />
      <div class="slide-title">{{ item.name }}</div>
    </swiper-slide>
  </swiper>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import defaultImg from "@/assets/jitashe.jpg";
import { Autoplay, Navigation, Pagination, Virtual } from "swiper/modules";
import { Swiper, SwiperSlide } from "swiper/vue";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import http from "@/http";

const modules = [Pagination, Navigation, Virtual, Autoplay];
const list = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await http.get("/api/team/getTopTeam");
    if (res && res.code === 200) {
      list.value = res.data || [];
    }
  } catch (e) {
    console.error("获取推荐社团失败", e);
  }
});
</script>

<style scoped lang="scss">
.mySwiper {
  margin-top: 20px;
}
.slide-title {
  text-align: center;
  padding: 8px;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}
</style>
