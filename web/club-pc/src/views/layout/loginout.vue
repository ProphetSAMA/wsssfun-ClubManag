<template>
  <!-- 已登录：显示用户下拉菜单 -->
  <el-dropdown v-if="userStore.isLoggedIn" style="padding: 0 20px; cursor: pointer" @command="handleCommand">
    <span class="el-dropdown-link">
      <img class="userimg" src="@/assets/user.jpg" />
      <span class="username">{{ userStore.nickname || userStore.username }}</span>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="mine">个人中心</el-dropdown-item>
        <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <!-- 未登录：显示登录按钮 -->
  <el-button v-else type="text" class="login-btn" @click="$router.push('/login')">
    登录
  </el-button>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command: string) => {
  if (command === 'mine') {
    router.push('/mine/mycenter')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/home')
  }
}
</script>

<style scoped lang="scss">
.userimg {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  vertical-align: middle;
}

.username {
  color: #fff;
  margin-left: 6px;
  font-size: 14px;
}

.login-btn {
  color: #fff !important;
  font-size: 14px;
  padding: 0 20px;
}
</style>
