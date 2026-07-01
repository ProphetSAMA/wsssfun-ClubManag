<template>
  <el-row :gutter="10" style="margin-top: 10px">
    <el-col :span="4">
      <el-card class="box-card" :body-style="{ padding: '5px' }">
        <div
          @click="btnClick(item.id)"
          class="text item cateitem"
          :class="{ 'active-item': item.id === activeName }"
          v-for="item in category"
          :key="item.id"
        >
          {{ item.name }}
        </div>
      </el-card>
    </el-col>
    <el-col :span="20">
      <RouterView />
    </el-col>
  </el-row>
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
  { name: "个人中心", id: "/mine/mycenter" },
  { name: "我的社团", id: "/mine/myclub" },
  { name: "我的活动", id: "/mine/myactivity" },
]);
</script>

<style scoped lang="scss">
.cateitem {
  height: 50px;
  line-height: 50px;
  border-bottom: 1px solid #e4e7ed;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;

  &:hover,
  &.active-item {
    color: #409eff;
    background-color: #ecf5ff;
  }
}
</style>
