<template>
  <div class="swiper-wrap">
    <swiper
      :modules="modules"
      :slides-per-view="4"
      :space-between="16"
      :autoplay="{ disableOnInteraction: false, delay: 3000 }"
      loop
    >
      <swiper-slide v-for="item in list" :key="item.id">
        <div class="team-card">
          <el-image
            class="team-img"
            :src="item.image ? 'http://localhost:8888' + item.image : defaultImg"
            fit="cover"
          />
          <div class="team-info">
            <div class="team-name">{{ item.name }}</div>
          </div>
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

const modules = [Autoplay];
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
.swiper-wrap {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  padding: var(--space-lg);
  box-shadow: var(--shadow-sm);
}

.team-card {
  border-radius: var(--radius-sm);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }
}

.team-img {
  width: 100%;
  height: 180px;
  display: block;
}

.team-info {
  padding: 12px;
  background: #fff;
}

.team-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  text-align: center;
}
</style>
