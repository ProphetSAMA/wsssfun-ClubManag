<template>
  <div class="info-card">
    <div class="card-title">我的社团</div>
    <el-table :data="clubList" stripe class="club-table">
      <el-table-column prop="teamId" label="社团ID" width="100" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.role === 2" type="danger" size="small">社长</el-tag>
          <el-tag v-else-if="scope.row.role === 1" type="warning" size="small">管理员</el-tag>
          <el-tag v-else type="info" size="small">普通成员</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="joinTime" label="加入时间" />
    </el-table>
    <el-empty v-if="clubList.length === 0" description="暂未加入任何社团" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import http from '@/http'

const userStore = useUserStore()
const clubList = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await http.get('/api/member/list', { currentPage: 1, pageSize: 50, userId: userStore.userId }) as any
    if (res && res.code === 200) clubList.value = res.data.records || []
  } catch (e) { console.error('获取我的社团失败', e) }
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

.club-table {
  :deep(th) {
    background: var(--bg-hover) !important;
  }
}
</style>
