<template>
  <el-icon @click="iconClick" :size="28" style="cursor: pointer;">
    <component :is="status ? Expand : Fold"></component>
  </el-icon>
</template>

<script setup lang="ts">
import { Expand, Fold } from "@element-plus/icons-vue";
import { computed } from "vue";
import { useMenuStore } from "@/store/menu";

const store = useMenuStore();
const status = computed(() => {
  return store.getIsCollapse;
});
const iconClick = () => {
  store.setIsCollapse();
};

// export default defineComponent({
//   data() {
//     return {
//       // 使用 data 选项来定义状态
//       store: useMenuStore(),
//     };
//   },
//   computed: {
//     // 使用 computed 选项来定义计算属性
//     status() {
//       return this.store.getIsCollapse; // 获取 collapsible 状态
//     },
//     Expand() {
//       return Expand; // 返回 Expand 组件
//     },
//     Fold() {
//       return Fold; // 返回 Fold 组件
//     },
//   },
//   methods: {
//     // 定义方法来处理点击事件
//     iconClick() {
//       this.store.setIsCollapse(); // 切换 collapsible 状态
//     },
//   },
// });
</script>

<style scoped lang="scss">
</style>
