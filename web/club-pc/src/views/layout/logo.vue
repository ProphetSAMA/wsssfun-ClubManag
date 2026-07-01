<template>
  <div class="logo" @click="$router.push('/home')">
    <div class="logo-icon">🏛️</div>
    <div class="logo-text">社团平台</div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.9;
  }
}

.logo-icon {
  font-size: 28px;
  line-height: 1;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1px;
}
</style>
