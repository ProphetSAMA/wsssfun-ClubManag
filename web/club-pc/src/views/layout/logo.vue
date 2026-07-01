<template>
  <div class="logo" @click="$router.push('/home')">
    <span class="text">社团平台</span>
  </div>
</template>

<style scoped lang="scss">
.logo {
  cursor: pointer;
  flex-shrink: 0;

  .text {
    font-size: 17px;
    font-weight: 600;
    color: #fff;
    letter-spacing: 1px;
  }
}
</style>
