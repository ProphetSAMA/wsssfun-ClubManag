<template>
  <nav class="nav-tabs">
    <div
      @click="tabClick(item)"
      class="nav-item"
      :class="{ active: item.value === activeName }"
      v-for="item in visibleTabs"
      :key="item.value"
    >
      {{ item.title }}
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { tabsStore } from "@/store/tabs/index";
import { useUserStore } from "@/store/user";

const store = tabsStore();
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const tabs = ref([
  { title: "首页", value: "home" },
  { title: "社团", value: "club" },
  { title: "活动", value: "activity" },
  { title: "新闻", value: "news" },
  { title: "公告", value: "notice" },
  { title: "个人中心", value: "mine", requiresAuth: true },
]);

const visibleTabs = computed(() =>
  tabs.value.filter(item => !item.requiresAuth || userStore.isLoggedIn)
);

// 根据当前路由计算高亮项
const activeName = computed(() => {
  const name = route.name as string;
  // 子路由（如 mycenter）映射到父级 mine
  if (name && ['mycenter', 'myclub', 'myactivity'].includes(name)) {
    return 'mine';
  }
  return name || 'home';
});

const tabClick = (item: any) => {
  router.push({ name: item.value });
};
</script>

<style scoped lang="scss">
.nav-tabs {
  display: flex;
  align-items: center;
  gap: 2px;
  margin-left: 40px;
  flex-grow: 1;
}

.nav-item {
  padding: 6px 18px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 6px;
  transition: color 0.2s, background 0.2s;
  white-space: nowrap;

  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.1);
  }

  &.active {
    color: #fff;
    background: rgba(255, 255, 255, 0.15);
  }
}
</style>
