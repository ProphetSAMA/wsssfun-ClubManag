<template>
  <div class="info-card">
    <div class="card-title">个人信息</div>
    <el-descriptions :column="1" border class="info-desc">
      <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ user.sex === '0' ? '男' : '女' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag :type="user.isAdmin === 1 ? 'danger' : 'info'" size="small">
          {{ user.isAdmin === 1 ? '管理员' : '普通用户' }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import http from '@/http'

const userStore = useUserStore()
const user = reactive({ username: '', nickname: '', sex: '', phone: '', isAdmin: 0 })

onMounted(async () => {
  try {
    const res = await http.get('/api/user/info') as any
    if (res && res.code === 200) Object.assign(user, res.data)
  } catch {
    user.username = userStore.username
    user.nickname = userStore.nickname
    user.isAdmin = userStore.isAdmin
  }
})
</script>

<style scoped lang="scss">
.info-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  padding: var(--space-lg);
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--space-lg);
  padding-bottom: var(--space-md);
  border-bottom: 1px solid var(--border-light);
}

.info-desc {
  :deep(.el-descriptions__label) {
    width: 100px;
    font-weight: 500;
  }
}
</style>
