<template>
  <!-- 推荐社团 -->
  <el-row>
    <el-col :span="24">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span class="moretitle">推荐社团</span>
            <el-button class="button" text @click="$router.push('/club')">
              更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div><swiper /></div>
      </el-card>
    </el-col>
  </el-row>

  <!-- 活动和公告 -->
  <el-row :gutter="20">
    <el-col :span="8">
      <el-card class="box-card" :body-style="{ height: '250px', padding: '10px' }">
        <template #header>
          <div class="card-header">
            <span class="moretitle">推荐活动</span>
            <el-button class="button" text @click="$router.push('/activity')">
              更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div><swiperActivity /></div>
      </el-card>
    </el-col>
    <el-col :span="16">
      <el-card class="box-card" :body-style="{ height: '250px', padding: '10px' }">
        <template #header>
          <div class="card-header">
            <span class="moretitle">推荐公告</span>
            <el-button class="button" text @click="$router.push('/notice')">
              更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="text item">
          <div
            class="notice"
            v-for="(item, index) in noticeList"
            :key="item.id"
          >
            <div class="title">{{ index + 1 + "、" + item.title }}</div>
            <div class="date">{{ item.createTime }}</div>
          </div>
          <el-empty v-if="noticeList.length === 0" description="暂无公告" :image-size="60" />
        </div>
      </el-card>
    </el-col>
  </el-row>

  <el-row>
    <el-col :span="24">
      <el-card class="box-card" :body-style="{ height: '250px', padding: '10px' }">
        <template #header>
          <div class="card-header">
            <span class="moretitle">最新活动</span>
            <el-button class="button" text @click="$router.push('/activity')">
              更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="text item">
          <div
            class="notice"
            v-for="(item, index) in activityList"
            :key="item.id"
          >
            <div class="title">{{ index + 1 + "、" + item.title }}</div>
            <div class="date">{{ item.createTime }}</div>
          </div>
          <el-empty v-if="activityList.length === 0" description="暂无活动" :image-size="60" />
        </div>
      </el-card>
    </el-col>
  </el-row>

  <el-row>
    <el-col :span="24">
      <el-card class="box-card" :body-style="{ height: '250px', padding: '10px' }">
        <template #header>
          <div class="card-header">
            <span class="moretitle">最新新闻</span>
            <el-button class="button" text @click="$router.push('/news')">
              更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="text item">
          <div
            class="notice"
            v-for="(item, index) in newsList"
            :key="item.id"
          >
            <div class="title">{{ index + 1 + "、" + item.title }}</div>
            <div class="date">{{ item.createTime }}</div>
          </div>
          <el-empty v-if="newsList.length === 0" description="暂无新闻" :image-size="60" />
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import swiper from "./swiper.vue";
import swiperActivity from "./swiperActivity.vue";
import http from "@/http";

const noticeList = ref<any[]>([]);
const activityList = ref<any[]>([]);
const newsList = ref<any[]>([]);

onMounted(async () => {
  try {
    const [noticeRes, activityRes, newsRes] = await Promise.all([
      http.get("/api/news/getTopNotice"),
      http.get("/api/news/getNewActivity"),
      http.get("/api/news/getNewNotice"),
    ]);
    if (noticeRes?.code === 200) noticeList.value = noticeRes.data || [];
    if (activityRes?.code === 200) activityList.value = activityRes.data || [];
    if (newsRes?.code === 200) newsList.value = newsRes.data || [];
  } catch (e) {
    console.error("获取首页数据失败", e);
  }
});
</script>

<style scoped lang="scss">
.box-card {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.moretitle {
  color: coral;
  font-weight: 600;
}
.text {
  font-size: 14px;
  color: #606266;
}
.item {
  margin-bottom: 18px;
}
.notice {
  cursor: pointer;
  display: flex;
  height: 30px;
  line-height: 30px;
  .title {
    font-size: 14px;
    font-weight: 600;
    color: #606266;
  }
  .date {
    font-size: 14px;
    margin-left: 20px;
    color: #909399;
  }
}
</style>
