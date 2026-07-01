<template>
  <div class="mycenter-container">
    <el-card>
      <template #header>
        <span>个人信息</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ user.sex === '0' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="user.isAdmin === 1 ? 'danger' : 'info'">
            {{ user.isAdmin === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import http from '@/http'

const userStore = useUserStore()

const user = reactive({
  username: '',
  nickname: '',
  sex: '',
  phone: '',
  isAdmin: 0
})

onMounted(async () => {
  try {
    const res = await http.get('/api/user/info') as any
    if (res && res.code === 200) {
      Object.assign(user, res.data)
    }
  } catch (e) {
    // 降级：用 store 中的缓存数据
    user.username = userStore.username
    user.nickname = userStore.nickname
    user.isAdmin = userStore.isAdmin
  }
})
</script>

<style scoped>
.mycenter-container {
  padding: 10px;
}
</style>
