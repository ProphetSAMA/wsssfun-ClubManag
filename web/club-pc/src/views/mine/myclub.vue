<template>
  <div class="card" v-loading="loading">
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon />
    <div class="card-title">我的社团</div>
    <el-table :data="clubList" stripe>
      <el-table-column label="社团" min-width="160">
        <template #default="scope">
          {{ scope.row.teamName || ("社团 #" + scope.row.teamId) }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.role === 2" type="danger" size="small">社长</el-tag>
          <el-tag v-else-if="scope.row.role === 1" type="warning" size="small">管理员</el-tag>
          <el-tag v-else type="info" size="small">普通成员</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="joinTime" label="加入时间" width="140" />
      <el-table-column label="操作" width="110" align="center">
        <template #default="scope">
          <el-button type="danger" size="small" plain @click="quit(scope.row)">退出</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="clubList.length === 0" description="暂未加入任何社团" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { myTeamsApi, quitTeamApi, type MemberItem } from '@/api/member'

const userStore = useUserStore()
const clubList = ref<MemberItem[]>([])
const loading = ref(true)
const error = ref("")

const getList = async () => {
  loading.value = true
  error.value = ""
  try {
    const res = await myTeamsApi(userStore.userId) as any
    if (res && res.code === 200) clubList.value = res.data.records || []
  } catch (e) {
    console.error(e)
    error.value = "社团信息加载失败，请稍后重试"
  } finally {
    loading.value = false
  }
}

/** 退出社团：二次确认后走逻辑删除，再刷新列表 */
const quit = async (row: MemberItem) => {
  try {
    await ElMessageBox.confirm('确定要退出「' + (row.teamName || '该社团') + '」吗？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  const res = await quitTeamApi(row.id) as any
  if (res?.code === 200) {
    ElMessage.success(res.msg || '已退出')
    await getList()
  }
}

onMounted(getList)
</script>

<style scoped lang="scss">
.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 20px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-light);
}
</style>
