<template>
  <div class="myclub-container">
    <el-card>
      <template #header>
        <span>我的社团</span>
      </template>
      <el-table :data="clubList" stripe>
        <el-table-column prop="teamId" label="社团ID" width="100" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 2" type="danger">社长</el-tag>
            <el-tag v-else-if="scope.row.role === 1" type="warning">管理员</el-tag>
            <el-tag v-else type="info">普通成员</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" />
      </el-table>
      <el-empty v-if="clubList.length === 0" description="暂未加入任何社团" />
    </el-card>
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
    const res = await http.get('/api/member/list', {
      currentPage: 1,
      pageSize: 50,
      userId: userStore.userId
    }) as any
    if (res && res.code === 200) {
      clubList.value = res.data.records || []
    }
  } catch (e) {
    console.error('获取我的社团失败', e)
  }
})
</script>

<style scoped>
.myclub-container {
  padding: 10px;
}
</style>
