<template>
  <div class="tabs">
    <div
        @click="$enevt => tabClick(item)"
      class="item"
      :class="{ 'active-item': item.value == activeName }"
      v-for="(item, index) in tabs"
    >
      {{ item.title }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { tabsStore } from '@/store/tabs/index'
const store = tabsStore()
const router = useRouter();
const activeName = computed(()=>{
    return store.mineTabs
})

const tabs = ref([
  {
    title: "首页",
    value: "home",
  },
  {
    title: "社团",
    value: "club",
  },
  {
    title: "活动",
    value: "activity",
  },
  {
    title: "新闻",
    value: "news",
  },
  {
    title: "公告",
    value: "notice",
  },
  {
    title: "个人中心",
    value: "mine",
  },
]);
const tabClick = (item:any) => {
  activeName.value = item.value;
  router.push({ name: item.value });
};
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
