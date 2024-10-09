<script setup lang="ts">
import logo from '@/assets/logo.png'
</script>

<template>
<div class="myLogo">
  <img class="logo" :src="logo"/>
  <span class="title">社团管理系统</span>
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
    width: 60px;
    margin-left: 10px;
    margin-right: 5px;
  }
  .title{
    width: 150px;
    height: 50px;
    align-items: center;
  }
}
</style>