<template>
    <view class="news-item" v-for="(item, index) in newList" :key="index">
      <view v-if="item.image">
        <el-image class="image" :src="item.image" />
      </view>
      <view class="news-intr">
        <view class="news-title">
          <text>{{ item.name }}</text>
        </view>
        <view class="news-info">
          <view class="news-time">
            <text>{{ item.create_time }}</text>
          </view>
  
          <view class="news-type">
            <text>{{ item.real_num }}浏览</text>
          </view>
        </view>
      </view>
    </view>
    <el-pagination
      style="margin-top: 15px"
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page.sync="parm.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]"
      :page-size="parm.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="parm.total"
      background
    >
    </el-pagination>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive } from "vue";
  
  import img1 from "@/assets/jitashe.jpg";
  import img2 from "@/assets/boyinshe.jpg";
  import img3 from "@/assets/yumaoqiushe.jpg";
  import img4 from "@/assets/4.jpg";
  import img5 from "@/assets/5.jpg";
  import img6 from "@/assets/6.jpg";
  
  // 活动列表
  const newList = ref([
    {
      create_time: "2024-10-9", //日期
      image: img1, // 封面
      real_num: "213", // 浏览量
      name: "新闻1", // 标题
    },
    {
      create_time: "2024-10-9",
      image: img2,
      real_num: "213",
      name: "新闻2",
    },
  ]);
  const parm = reactive({
    currentPage: 1, // 当前页数
    pageSize: 10, // 每页显示条数
    total: 3, // 总条数
  });
  // 页容量改变时触发
  const sizeChange = (val: number) => {};
  const currentChange = (val: number) => {};
  </script>
  
  <style scoped lang="scss">
  .news-item {
    display: flex;
    height: 110px;
    padding: 30px 0;
    border-bottom: 1px solid #e4e7ed;
    cursor: pointer;
    
    .image {
      height: 100px;
      width: 200px;
      border-radius: 10px;
    }
    
    .news-intr {
      margin-left: 15px;
  
      .news-info {
        display: flex;
        font-size: 28rpx;
        color: #999999;
        flex: 1; /* 占据足够宽度 */
        
        .news-time {
          text-align: left;
        }
        
        .news-type {
          margin-left: auto; /* 将 news-type 靠右 */
          text-align: right;
        }
      }
    }
  }
  
  </style>
  