<template>
  <div>
    <el-button @click="addBtn" type="primary"  icon="Plus" size="default">新增</el-button>
    <el-icon :size="20">
      <Edit />
    </el-icon>
  <div>{{store.count}}</div>
  </div>
</template>

<script setup lang="ts">
import {Edit} from "@element-plus/icons-vue";
import { useTestStore } from "@/store/test";

const store = useTestStore();

// 点击
const addBtn = () => {
  // store.setCount(store.count + 1);
  // store.$patch({ count: store.count + 1 });
  // store.$patch((state) => {
  //   state.count += 1;
  // });
}
</script>

<style scoped lang="scss">

</style>