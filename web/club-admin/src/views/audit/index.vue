<template>
  <el-main>
    <el-card shadow="never">
      <el-form :inline="true" class="search">
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="账号 / 接口路径" clearable style="width: 220px"
            @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="方法">
          <el-select v-model="method" placeholder="全部" clearable style="width: 120px">
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" border stripe size="small">
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column prop="username" label="账号" width="130">
          <template #default="scope">{{ scope.row.username || '（未登录）' }}</template>
        </el-table-column>
        <el-table-column label="方法" width="90" align="center">
          <template #default="scope">
            <el-tag :type="methodTag(scope.row.method)" size="small">{{ scope.row.method }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="接口" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column label="耗时" width="90" align="right">
          <template #default="scope">{{ scope.row.durationMs }} ms</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center" />
      </el-table>

      <el-pagination
        class="pager"
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        background
        @current-change="onPageChange"
      />
    </el-card>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAuditListApi } from '@/api/audit'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const keyword = ref('')
const method = ref<string | undefined>(undefined)

const methodTag = (value: string) => {
  if (value === 'DELETE') return 'danger'
  if (value === 'PUT') return 'warning'
  return 'primary'
}

const getList = async () => {
  loading.value = true
  try {
    const res = (await getAuditListApi({
      keyword: keyword.value,
      method: method.value,
      currentPage: currentPage.value,
      pageSize: pageSize.value
    })) as any
    if (res?.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

const search = () => {
  currentPage.value = 1
  getList()
}

const reset = () => {
  keyword.value = ''
  method.value = undefined
  search()
}

const onPageChange = (page: number) => {
  currentPage.value = page
  getList()
}

onMounted(getList)
</script>

<style scoped lang="scss">
.search { margin-bottom: 8px; }
.pager { margin-top: 12px; justify-content: flex-end; display: flex; }
</style>
