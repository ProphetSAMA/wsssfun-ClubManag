<template>
<swiper
    :modules="modules"
    :initial-slide="1"
    :slides-per-view="3"
    :centeredSlides="true"
    :spaceBetween="20"
    :autoplay="{ disableOnInteraction: false }"
    loop
    class="mySwiper"
    @swiper="setSwiperRef"
>
<swiper-slide class="swiper-slide" v-for="(item, index) in list" :key="index">
    <el-image style="height: 250px;width: 100%; object-fit: cover;" :src="item.url"/>
</swiper-slide>
</swiper>
</template>
<script setup lang="ts">
import { ref } from "vue";
import img1 from "@/assets/jitashe.jpg";
import img2 from "@/assets/boyinshe.jpg";
import img3 from "@/assets/yumaoqiushe.jpg";
import img4 from "@/assets/4.jpg";
import img5 from "@/assets/5.jpg";
import img6 from "@/assets/6.jpg";
// 引入Swiper核心和所需模块
import { Autoplay, Navigation, Pagination, Virtual } from "swiper/modules";

// 引入Swiper组件
import { Swiper, SwiperSlide } from "swiper/vue";

// 引入Swiper样式(按需导入)
import "swiper/css";
import "swiper/css/navigation"; // 底部圆点
import "swiper/css/pagination"; // 左右箭头
import "swiper/css/scrollbar"; // 滚动条

const modules = [Pagination, Navigation, Virtual, Autoplay]
let swiperRef = null;
const setSwiperRef = (swiper: any) => {
    swiperRef = swiper;
};
const list = ref([{ url: img1 }, { url: img2 }, { url: img3 }, { url: img4 }, { url: img5 }, { url: img6 }]);
</script>
