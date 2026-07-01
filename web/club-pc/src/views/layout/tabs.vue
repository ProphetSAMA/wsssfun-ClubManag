<template>
  <div class="tabs">
    <div
      @click="tabClick(item)"
      class="item"
      :class="{ 'active-item': item.value === store.activeName }"
      v-for="(item, index) in visibleTabs"
      :key="index"
    >
      {{ item.title }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { tabsStore } from "@/store/tabs/index";
import { useUserStore } from "@/store/user";

const store = tabsStore();
const router = useRouter();
const userStore = useUserStore();

// 选项卡数据
const tabs = ref([
  { title: "首页", value: "home" },
  { title: "社团", value: "club" },
  { title: "活动", value: "activity" },
  { title: "新闻", value: "news" },
  { title: "公告", value: "notice" },
  { title: "个人中心", value: "mine", requiresAuth: true },
]);

// 根据登录状态过滤可见的 tabs
const visibleTabs = computed(() => {
  return tabs.value.filter(item => {
    if (item.requiresAuth) {
      return userStore.isLoggedIn;
    }
    return true;
  });
});

// 页面加载时设置 activeName
if (!store.activeName) {
  store.activeName = store.fatable;
}

onMounted(() => {
  if (!store.activeName) {
    store.activeName = store.fatable;
  }
});

const tabClick = (item: any) => {
  store.activeName = item.value;
  store.fatable = item.value;
  router.push({ name: item.value });
};

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
    padding: 0 30px;
    text-align: center;
    color: #fff;
    font-size: 16px;
    cursor: pointer;
    height: 60px;
    line-height: 60px;
  }

  .active-item {
    color: #d9db37;
    border-bottom: 2px solid #d9db37;
  }
}
</style>
