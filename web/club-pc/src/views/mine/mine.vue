<template>
  <div class="mine-page">
    <div class="page-header">
      <h1>👤 个人中心</h1>
    </div>
    <div class="mine-layout">
      <aside class="mine-sidebar">
        <div
          class="side-item"
          :class="{ active: activeName === item.id }"
          v-for="item in category"
          :key="item.id"
          @click="btnClick(item.id)"
        >
          <span class="side-icon">{{ item.icon }}</span>
          {{ item.name }}
        </div>
      </aside>
      <div class="mine-content">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { tabsStore } from '@/store/tabs/index';

const store = tabsStore();
const activeName = computed(() => store.mineTabs);

const btnClick = (id: string) => {
  store.mineTabs = id;
};

const category = ref([
  { name: "个人信息", id: "/mine/mycenter", icon: "👤" },
  { name: "我的社团", id: "/mine/myclub", icon: "🏛️" },
  { name: "我的活动", id: "/mine/myactivity", icon: "🎯" },
]);
</script>

<style scoped lang="scss">
.mine-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
}

.page-header {
  h1 { font-size: 28px; font-weight: 800; color: var(--text-primary); }
}

.mine-layout {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: var(--space-lg);
}

.mine-sidebar {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  padding: var(--space-md);
  height: fit-content;
  position: sticky;
  top: calc(var(--header-height) + var(--space-xl));
}

.side-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: var(--bg-hover);
    color: var(--text-primary);
  }

  &.active {
    background: var(--primary-bg);
    color: var(--primary);
    font-weight: 600;
  }
}

.side-icon {
  font-size: 16px;
}

.mine-content {
  min-width: 0;
}

@media (max-width: 768px) {
  .mine-layout { grid-template-columns: 1fr; }
}
</style>
