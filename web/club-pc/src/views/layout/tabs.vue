<template>
  <div class="tabs">
    <div
      @click="tabClick(item)"
      class="item"
      :class="{ 'active-item': item.value === store.activeName }"
      v-for="(item, index) in tabs"
      :key="index"
    >
      {{ item.title }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { tabsStore } from "@/store/tabs/index";

const store = tabsStore();
const router = useRouter();


// 选项卡数据
const tabs = ref([
  { title: "首页", value: "home" },
  { title: "社团", value: "club" },
  { title: "活动", value: "activity" },
  { title: "新闻", value: "news" },
  { title: "公告", value: "notice" },
  { title: "个人中心", value: "mine" },
]);

// 页面加载时设置 activeName
if (!store.activeName) {
  store.activeName = store.fatable; // 使用 fatable 初始化 activeName
}

onMounted(() => {
  if (!store.activeName) {
    store.activeName = store.fatable; // 使用 fatable 初始化 activeName
  }
});
// 监听 activeName 变化
const tabClick = (item: any) => {
  store.activeName = item.value; // 更新选中的 Tab
  store.fatable = item.value; // 更新 Pinia 状态
  router.push({ name: item.value }); // 路由跳转
};

// 监听 store.fatable 的变化并更新 activeName
watch(
  () => store.fatable,
  (newValue) => {
    store.activeName = newValue;
  }
);
</script>

<style scoped lang="scss">
.tabs {
  display: flex;
  flex-grow: 1;
  font-weight: 600;
  margin-left: 80px;

  .item {
    padding: 0px 30px;
    text-align: center;
    color: #fff;
    font-size: 16px;
    cursor: pointer;
    height: 60px;
    line-height: 60px;
  }

  .active-item {
    padding: 0px 30px;
    text-align: center;
    color: #d9db37;
    font-size: 16px;
    cursor: pointer;
    height: 60px;
    line-height: 60px;
    border-bottom: 2px solid #d9db37;
  }
}
</style>
