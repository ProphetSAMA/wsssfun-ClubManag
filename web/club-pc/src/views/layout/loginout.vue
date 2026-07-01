<template>
  <el-dropdown style="padding: 0 20px; cursor: pointer" @command="handleCommand">
    <span class="el-dropdown-link">
      <img class="userimg" src="@/assets/user.jpg" />
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="mine">个人中心</el-dropdown-item>
        <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const handleCommand = (command: string) => {
  if (command === 'mine') {
    router.push('/mine/mycenter')
  } else if (command === 'logout') {
    localStorage.removeItem('token')
    router.push('/home')
  }
}
</script>

<style scoped lang="scss">
.userimg {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>
