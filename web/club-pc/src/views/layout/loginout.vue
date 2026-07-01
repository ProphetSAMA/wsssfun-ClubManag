<template>
  <div class="user-area">
    <!-- 已登录 -->
    <el-dropdown v-if="userStore.isLoggedIn" trigger="click" @command="handleCommand">
      <div class="user-avatar-btn">
        <div class="avatar">{{ (userStore.nickname || userStore.username || 'U').charAt(0) }}</div>
        <span class="username">{{ userStore.nickname || userStore.username }}</span>
        <el-icon class="arrow"><ArrowDown /></el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="mine">
            <el-icon><User /></el-icon>个人中心
          </el-dropdown-item>
          <el-dropdown-item command="logout" divided>
            <el-icon><SwitchButton /></el-icon>退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 未登录 -->
    <el-button v-else class="login-btn" @click="$router.push('/login')">
      登录
    </el-button>
  </div>
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
.user-area {
  flex-shrink: 0;
}

.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 40px;
  transition: background 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.12);
  }
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.username {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.arrow {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.login-btn {
  background: rgba(255, 255, 255, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #fff !important;
  border-radius: 20px !important;
  padding: 6px 20px !important;
  font-weight: 500 !important;

  &:hover {
    background: rgba(255, 255, 255, 0.25) !important;
  }
}
</style>
