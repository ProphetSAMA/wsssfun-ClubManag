<template>
    <!-- 推荐活动轮播图 -->
    <el-carousel height="250px">
        <el-carousel-item v-for="item in swipperList" :key="item">
            <el-image style="height: 250px; width: 100%; object-fit: cover;" :src="item.url"/>
        </el-carousel-item>
    </el-carousel>
</template>

<script setup lang="ts">
import { ref } from "vue";
import img1 from "@/assets/jitashe.jpg";
import img2 from "@/assets/boyinshe.jpg";
import img3 from "@/assets/yumaoqiushe.jpg";
import img4 from "@/assets/4.jpg";
import img5 from "@/assets/5.jpg";
import img6 from "@/assets/6.jpg";

// 推荐活动轮播图
const swipperList = ref([
    { url: img1 },
    { url: img2 },
    { url: img3 },
    { url: img4 },
    { url: img5 },
    { url: img6 }
])
</script>

<style scoped>

</style>