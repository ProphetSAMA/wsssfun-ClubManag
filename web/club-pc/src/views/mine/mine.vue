<template>
  <div class="mine-page">
    <div class="page-head"><h1>个人中心</h1></div>
    <div class="layout">
      <aside class="sidebar">
        <div
          class="nav"
          :class="{ active: activeName === item.id }"
          v-for="item in menu"
          :key="item.id"
          @click="btnClick(item.id)"
        >{{ item.name }}</div>
      </aside>
      <div class="content">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { tabsStore } from '@/store/tabs/index';

const router = useRouter();
const store = tabsStore();
const activeName = computed(() => store.mineTabs);

const btnClick = (id: string) => {
  store.mineTabs = id;
  router.push(id);
};

const menu = ref([
  { name: "个人信息", id: "/mine/mycenter" },
  { name: "我的社团", id: "/mine/myclub" },
  { name: "我的活动", id: "/mine/myactivity" },
]);
</script>

<style scoped lang="scss">
.mine-page { display: flex; flex-direction: column; gap: 20px; }
.page-head h1 { font-size: 20px; font-weight: 700; color: var(--text-1); }

.layout {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 20px;
}

.sidebar {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 8px;
  height: fit-content;
  position: sticky;
  top: calc(var(--header-h) + 24px);
}

.nav {
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  color: var(--text-2);
  cursor: pointer;
  transition: all 0.15s;

  &:hover { background: var(--bg-hover); }
  &.active { background: var(--primary-bg); color: var(--primary); font-weight: 500; }
}

@media (max-width: 768px) {
  .layout { grid-template-columns: 1fr; }
}
</style>
