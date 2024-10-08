<template>
  <!-- 推荐社团 -->
  <el-row>
    <el-col :span="24">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span class="moretitle">推荐社团</span>
            <el-button class="button" text
              >更多
              <el-icon>
                <ArrowRight />
              </el-icon>
            </el-button>
          </div>
        </template>
        <div>
          <swiper></swiper>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <!-- 活动和新闻 -->
  <el-row :gutter="20">
    <!-- 推荐活动 -->
    <el-col :span="8" :offset="0">
      <el-card
        class="box-card"
        :body-style="{ height: '250px', padding: '10px' }"
      >
        <template #header>
          <div class="card-header">
            <span class="moretitle">推荐活动</span>
            <el-button class="button" text
              >更多<el-icon><ArrowRight /></el-icon
            ></el-button>
          </div>
        </template>
        <div>
          <swiperActivity></swiperActivity>
        </div>
      </el-card>
    </el-col>
    <!-- 新闻 -->
    <el-col :span="16" :offset="0">
      <el-card
        class="box-card"
        :body-style="{ height: '250px', padding: '10px' }"
      >
        <template #header>
          <div class="card-header">
            <span class="moretitle">新闻</span>
            <el-button class="button" text
              >更多<el-icon><ArrowRight /></el-icon
            ></el-button>
          </div>
        </template>
        <div class="text item">
          <div class="notice" v-for="(item, index) in noticeList">
            <div class="title">{{ index + 1 + "、" + item.title }}</div>
            <div class="date">{{ item.date }}</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <el-row>
    <el-col :span="24" :offset="0">
        <el-card
        class="box-card"
        :body-style="{ height: '250px', padding: '10px' }"
      >
        <template #header>
          <div class="card-header">
            <span class="moretitle">最新活动</span>
            <el-button class="button" text
              >更多<el-icon><ArrowRight /></el-icon
            ></el-button>
          </div>
        </template>
        <div class="text item">
          <div class="notice" v-for="(item, index) in noticeList">
            <div class="title">{{ index + 1 + "、" + item.title }}</div>
            <div class="date">{{ item.date }}</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <el-row>
    <el-col :span="24" :offset="0">
        <el-card
        class="box-card"
        :body-style="{ height: '250px', padding: '10px' }"
      >
        <template #header>
          <div class="card-header">
            <span class="moretitle">最新新闻</span>
            <el-button class="button" text
              >更多<el-icon><ArrowRight /></el-icon
            ></el-button>
          </div>
        </template>
        <div class="text item">
          <div class="notice" v-for="(item, index) in noticeList">
            <div class="title">{{ index + 1 + "、" + item.title }}</div>
            <div class="date">{{ item.date }}</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref } from "vue";
import swiper from "./swiper.vue";
import swiperActivity from "./swiperActivity.vue";
const noticeList = ref([
  {
    title: "关于举办2021年度社团联谊活动的通知",
    date: "2021-10-10",
  },
  {
    title: "关于举办2022年度社团联谊活动的通知",
    date: "2022-10-10",
  },
  {
    title: "关于举办2023年度社团联谊活动的通知",
    date: "2023-10-10",
  },
  {
    title: "关于举办2024年度社团联谊活动的通知",
    date: "2024-10-10",
  },
]);
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
.moreactivity {
  display: flex;
  align-items: center;
  cursor: pointer;
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
