<template>
  <!-- 带间距的栅格布局行，设置上方边距 -->
  <el-row :gutter="10" style="margin-top: 10px">
    <!-- 左侧分类区域 -->
    <el-col :span="4">
      <el-card class="box-card" :body-style="{ padding: '5px' }">
        <!-- 分类列表，每个分类项会显示在一个div中 遍历category数据源 使用item.id作为唯一键 -->
        <div
          @click="btnClick(item.id)"
          class="text item cateitem"
          v-for="item in category"
          :class="{ 'active-item': item.id == activeName }"
        >
          <RouterLink :to="item.id">{{ item.name }}</RouterLink>
        </div>
      </el-card>
    </el-col>

    <!-- 右侧社团列表区域 -->
    <el-col :span="20">
      <RouterView />
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { tabsStore } from '@/store/tabs/index'
const store = tabsStore()

const activeName = computed(()=>{
    return store.mineTabs
})
const btnClick = (id:string)=>{
    // activeName.value = id;
    store.mineTabs = id;
}

// 定义分类数据，存储在响应式变量category中
const category = ref([
  {
    name: "个人中心",
    id: "/mine/mycenter",
  },
  {
    name: "我的社团",
    id: "/mine/myclub",
  },
  {
    name: "我的活动",
    id: "/mine/myactivity",
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
  a {
    height: 50px;
    line-height: 50px;
    color: #999;
    text-align: center;
    cursor: pointer;
    text-decoration: none;
  }
}
.active-item {
  a {
    color: #f3af28;
  }
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
