<template>
  <div class="user-area">
    <el-dropdown v-if="userStore.isLoggedIn" trigger="click" @command="handleCommand">
      <span class="user-btn">
        <span class="avatar">{{ initial }}</span>
        <span class="name">{{ userStore.nickname || userStore.username }}</span>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="mine">个人中心</el-dropdown-item>
          <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <span v-else class="login-link" @click="$router.push('/login')">登录</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const initial = computed(() => {
  const n = userStore.nickname || userStore.username || ''
  return n.charAt(0) || 'U'
})

const handleCommand = (cmd: string) => {
  if (cmd === 'mine') router.push('/mine/mycenter')
  else if (cmd === 'logout') {
    userStore.logout()
    router.push('/home')
  }
}
</script>

<style scoped lang="scss">
.user-area {
  flex-shrink: 0;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 6px;
  transition: background 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}

.name {
  color: #fff;
  font-size: 13px;
}

.login-link {
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  cursor: pointer;
  padding: 4px 12px;

  &:hover {
    color: #fff;
  }
}
</style>
