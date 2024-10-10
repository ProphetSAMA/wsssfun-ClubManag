<script setup lang="ts">
import logo from '@/assets/logo.png'
import {useMenuStore} from "@/store/menu";
import { ref, watch} from "vue";

const store = useMenuStore();

// 展开 Title延迟出现
const show = ref(true)
watch(
    () => store.getIsCollapse,
    (collapse:boolean) => {
      if(!collapse){
        setTimeout(() => {
          show.value = !collapse
        }, 300)
      }else {
        show.value = !collapse
      }
    })

</script>

<template>
<div class="myLogo">
  <img class="logo" :src="logo" alt="Logo"/>
  <span v-if="show" class="title">社团管理系统</span>
</div>
</template>

<style scoped lang="scss">
.myLogo{
  height: 60px;
  background-color: #679ed5;
  color: white;
  display: flex;
  align-items: center;
  .logo{
    height: 60px;
    width: 64px;
  }
  .title{
    width: 150px;
    height: 50px;
    align-items: center;
  }
}
</style>