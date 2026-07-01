<template>
  <el-container class="myContainer">
    <el-aside class="myAside" width="auto">
      <Menu />
    </el-aside>
    <el-container>
      <el-header class="myHeader">
        <Collapse />
        <BreadCrumb />
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.nickname || userStore.username }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="myMain">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import Menu from "@/layout/menu.vue";
import Collapse from "@/layout/Collapse.vue";
import BreadCrumb from "@/layout/BreadCrumb.vue";

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.myContainer {
  height: 100%;

  .myAside {
    background-color: #0f4980;
    @media (max-width: 768px) {
      display: none;
    }
  }

  .myHeader {
    display: flex;
    align-items: center;
    background-color: #0f4980;
    color: white;
    padding: 0 10px;
  }

  .myMain {
    background-color: #f0f0f0;
    padding: 0;
  }
}

.header-right {
  margin-left: auto;
}

.user-info {
  display: flex;
  align-items: center;
  color: white;
  cursor: pointer;
  font-size: 14px;
  gap: 4px;
}
</style>
