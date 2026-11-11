<template>
  <el-main>
    <el-card shadow="never">
      <el-form :inline="true" class="search">
        <el-form-item label="状态">
          <el-select v-model="status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="社团名称 / 申请人" clearable style="width: 200px"
            @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="name" label="社团名称" min-width="140" />
        <el-table-column prop="categoryName" label="分类" width="110" />
        <el-table-column label="申请人" width="150">
          <template #default="scope">
            <div>{{ scope.row.applicantName || '-' }}</div>
            <div class="phone">{{ scope.row.phone || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success" size="small">已通过</el-tag>
            <el-tag v-else type="info" size="small">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="140" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button size="small" type="primary" @click="approve(scope.row)">通过</el-button>
              <el-button size="small" type="danger" plain @click="openReject(scope.row)">驳回</el-button>
            </template>
            <span v-else class="tip">{{ scope.row.rejectReason || '已处理' }}</span>
          </template>
        </el-table-column>
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

    <el-dialog v-model="rejectDialog" title="驳回申请" width="420px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" maxlength="100" show-word-limit
        placeholder="填写驳回原因，申请人能看到" />
      <template #footer>
        <el-button @click="rejectDialog = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="doReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApplyListApi, approveApplyApi, rejectApplyApi } from '@/api/teamapply'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const status = ref<number | undefined>(undefined)
const keyword = ref('')

const rejectDialog = ref(false)
const rejectReason = ref('')
const rejectTarget = ref<any>(null)
const submitting = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const res = (await getApplyListApi({
      status: status.value,
      keyword: keyword.value,
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
  status.value = undefined
  keyword.value = ''
  search()
}

const onPageChange = (page: number) => {
  currentPage.value = page
  getList()
}

const approve = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确认通过「${row.name}」的成立申请吗？通过后会自动创建社团，并把申请人设为社长。`, '提示', {
      type: 'warning',
      confirmButtonText: '通过',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  const res = (await approveApplyApi(row.id)) as any
  if (res?.code === 200) {
    ElMessage.success(res.msg || '已通过')
    getList()
  }
}

const openReject = (row: any) => {
  rejectTarget.value = row
  rejectReason.value = ''
  rejectDialog.value = true
}

const doReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写驳回原因')
    return
  }
  submitting.value = true
  try {
    const res = (await rejectApplyApi(rejectTarget.value.id, rejectReason.value.trim())) as any
    if (res?.code === 200) {
      ElMessage.success(res.msg || '已驳回')
      rejectDialog.value = false
      getList()
    }
  } finally {
    submitting.value = false
  }
}

onMounted(getList)
</script>

<style scoped lang="scss">
.search { margin-bottom: 8px; }
.pager { margin-top: 12px; justify-content: flex-end; display: flex; }
.phone { font-size: 12px; color: #909399; }
.tip { font-size: 12px; color: #909399; }
</style>
