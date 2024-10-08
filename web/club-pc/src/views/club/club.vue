<template>
  <div>
    <!-- 带间距的栅格布局行，设置上方边距 -->
    <el-row :gutter="10" style="margin-top: 10px">
      <!-- 左侧分类区域 -->
      <el-col :span="4">
        <el-card class="box-card" :body-style="{ padding: '5px' }">

            <!-- 分类列表，每个分类项会显示在一个div中 遍历category数据源 使用item.id作为唯一键 -->
          <div
            class="text item cateitem"
            v-for="item in category"
            :key="item.id"
          >
            {{ item.name }}
          </div>
        </el-card>
      </el-col>

      <!-- 右侧社团列表区域 -->
      <el-col :span="20">
        <el-card class="box-card" :body-style="{ padding: '5px' }">
          <div class="text item">
            <!-- 社团卡片内容的栅格布局 -->
            <el-row>
                <!-- 每个社团的信息卡片 -->

                <!-- 在超小屏幕上占12列 在小屏幕上占8列 在中等屏幕上占7列 -->
                <!-- 遍历社团数据 以teamId作为唯一键 偏移1列使布局间距更美观 -->
              <el-col
                :xs="12" 
                :sm="8"
                :md="7"
                v-for="item in tableData"
                :key="item.teamId"
                :offset="1"
              >
                <div style="margin-top: 15px">
                    <!-- 社团信息卡片 -->
                  <el-card class="box-card" :body-style="{ padding: '5px' }">
                    <img class="image" :src="item.images" alt="" />
                    <div>
                      <!-- 显示社团的年级、专业和班级信息 -->
                      <strong>
                        {{ item.classGrade }} 级 - {{ item.classMajor }}系 -
                        {{ item.classNumber }}班
                      </strong>
                      <!-- 卡片底部区域 -->
                      <div class="bottom clearfix">
                        <time>
                          <span>创建时间:{{ item.classCreateTime }}</span>
                        </time>
                        <el-button class="button">查看</el-button>
                      </div>
                    </div>
                  </el-card>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

// 引入图片资源，用于社团的图片展示
import img1 from "@/assets/jitashe.jpg";
import img2 from "@/assets/boyinshe.jpg";
import img3 from "@/assets/yumaoqiushe.jpg";
import img4 from "@/assets/4.jpg";
import img5 from "@/assets/5.jpg";
import img6 from "@/assets/6.jpg";

// 定义分类数据，存储在响应式变量category中
const category = ref([
  {
    name: "书画协会", // 分类名称
    id: 1, // 分类唯一ID
  },
  {
    name: "文体协会",
    id: 1,
  },
  {
    name: "学术协会",
    id: 1,
  },
  {
    name: "体育协会",
    id: 1,
  },
]);
const tableData = ref([
  {
    teamId: "1", // 社团唯一ID
    classGrade: "2022", // 社团年级
    classMajor: "计算机工程", // 社团专业
    images: img1, // 社团图片
    classNumber: "2207", // 社团班级
    classCreateTime: "2022-09-01", // 社团创建时间
  },
  {
    teamId: "2",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img2,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "3",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img3,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "4",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img4,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "5",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img5,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "6",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img6,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
]);
</script>

<style scoped lang="scss">
/* 定义时间文本的样式 */
.time {
  font-size: 10px; /* 字体大小 */
  color: #999; /* 字体颜色 */
}

/* 定义分类项的样式 */
.cateitem {
  height: 50px; /* 高度 */
  line-height: 50px; /* 行高 */
  border-bottom: 1px solid #e4e7ed; /* 底部边框 */
  text-align: center; /* 居中对齐 */
  cursor: pointer; /* 鼠标悬浮变成手型 */
}

/* 定义卡片底部样式 */
.bottom {
  margin-top: 5px; /* 顶部外边距 */
  line-height: 12px; /* 行高 */
  display: flex; /* 使用flex布局 */
  justify-content: space-between; /* 子元素两端对齐 */
  align-items: center; /* 垂直居中对齐 */
}

/* 定义图片样式 */
.image {
  width: 100%; /* 图片宽度占满容器 */
  display: block; /* 图片作为块级元素 */
}
</style>
