<template>
  <div class="swiper-wrap">
    <swiper
      :modules="modules"
      :slides-per-view="4"
      :space-between="14"
      :autoplay="{ disableOnInteraction: false, delay: 3000 }"
      loop
    >
      <swiper-slide v-for="item in list" :key="item.id">
        <div class="team-card">
          <el-image
            class="team-img"
            :src="item.image ? imgUrl(item.image) : defaultImg"
            fit="cover"
          />
          <div class="team-name">{{ item.name }}</div>
        </div>
      </swiper-slide>
    </swiper>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import defaultImg from "@/assets/jitashe.jpg";
import { Autoplay } from "swiper/modules";
import { Swiper, SwiperSlide } from "swiper/vue";
import "swiper/css";
import http from "@/http";

const BASE = "http://localhost:8888";
const imgUrl = (image: string) => {
  if (!image) return "";
  let url = image.split(",")[0].trim();
  if (url.startsWith("undefined")) url = url.replace("undefined", "");
  if (url.startsWith("/")) url = BASE + url;
  return url;
};
const modules = [Autoplay];
const list = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await http.get("/api/team/getTopTeam");
    if (res && res.code === 200) list.value = res.data || [];
  } catch (e) { console.error(e); }
});
</script>

<style scoped lang="scss">
.swiper-wrap {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 16px;
}

.team-card {
  cursor: pointer;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: var(--shadow-md);
    border-radius: var(--radius);
    overflow: hidden;
  }
}

.team-img {
  width: 100%;
  height: 170px;
  border-radius: var(--radius) var(--radius) 0 0;
}

.team-name {
  padding: 10px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-1);
  text-align: center;
  background: var(--bg-white);
}
</style>
