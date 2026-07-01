<template>
  <nav class="nav-tabs">
    <div
      @click="tabClick(item)"
      class="nav-item"
      :class="{ active: item.value === store.activeName }"
      v-for="item in visibleTabs"
      :key="item.value"
    >
      {{ item.title }}
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { tabsStore } from "@/store/tabs/index";
import { useUserStore } from "@/store/user";

const store = tabsStore();
const router = useRouter();
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

watch(() => store.fatable, (v) => { store.activeName = v; });
</script>

<style scoped lang="scss">
.nav-tabs {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 48px;
  flex-grow: 1;
}

.nav-item {
  padding: 8px 20px;
  color: rgba(255, 255, 255, 0.75);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.25s ease;
  white-space: nowrap;
  position: relative;

  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.12);
  }

  &.active {
    color: #fff;
    background: rgba(255, 255, 255, 0.18);
    font-weight: 600;
  }
}
</style>
