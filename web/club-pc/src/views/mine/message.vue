<template>
  <div class="msg-page">
    <div class="head">
      <span class="title">我的通知</span>
      <el-button size="small" :disabled="unread === 0" @click="readAll">全部标为已读</el-button>
    </div>

    <div class="card" v-loading="loading">
      <div v-if="list.length === 0" class="empty">还没有通知</div>
      <div
        v-for="item in list"
        :key="item.id"
        class="item"
        :class="{ unread: item.isRead === 0 }"
        @click="open(item)"
      >
        <div class="row">
          <span class="title">{{ item.title }}</span>
          <el-tag :type="typeTag(item.type)" size="small">{{ typeLabel(item.type) }}</el-tag>
          <span class="time">{{ item.createTime }}</span>
        </div>
        <div v-if="item.content" class="content">{{ item.content }}</div>
      </div>
    </div>

    <el-pagination
      v-if="total > size"
      class="pager"
      layout="total, prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="currentPage"
      background
      @current-change="pageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { myNotificationsApi, unreadCountApi, readNotificationApi, readAllNotificationApi } from '@/api/notification'

const router = useRouter()
const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const size = ref(10)
const unread = ref(0)

const typeLabel = (type: string) =>
  ({ signup: '报名', apply: '申请', member: '社团', system: '系统' } as Record<string, string>)[type] || '通知'

const typeTag = (type: string) => {
  if (type === 'apply') return 'warning'
  if (type === 'signup') return 'success'
  if (type === 'member') return 'primary'
  return 'info'
}

const load = async () => {
  loading.value = true
  try {
    const res = (await myNotificationsApi(currentPage.value, size.value)) as any
    if (res?.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    }
    const un = (await unreadCountApi()) as any
    if (un?.code === 200) unread.value = Number(un.data) || 0
  } finally {
    loading.value = false
  }
}

const open = async (item: any) => {
  if (item.isRead === 0) {
    await readNotificationApi(item.id)
    item.isRead = 1
    unread.value = Math.max(0, unread.value - 1)
  }
  if (item.link) router.push(item.link)
}

const readAll = async () => {
  const res = (await readAllNotificationApi()) as any
  if (res?.code === 200) {
    ElMessage.success(res.msg || '已全部标记为已读')
    list.value.forEach((item: any) => { item.isRead = 1 })
    unread.value = 0
  }
}

const pageChange = (page: number) => {
  currentPage.value = page
  load()
}

onMounted(load)
</script>

<style scoped lang="scss">
.msg-page { display: flex; flex-direction: column; gap: 12px; }

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title { font-size: 15px; font-weight: 600; color: var(--text-1); }

.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 4px 16px;
}

.item {
  padding: 14px 0;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); }
  &.unread .title { font-weight: 600; }
  &.unread .title::before {
    content: "";
    display: inline-block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #f56c6c;
    margin-right: 6px;
    vertical-align: middle;
  }
}

.row { display: flex; align-items: center; gap: 10px; }
.time { margin-left: auto; font-size: 12px; color: var(--text-3); }
.content { font-size: 13px; color: var(--text-2); margin-top: 6px; }
.empty { text-align: center; color: var(--text-3); font-size: 13px; padding: 32px 0; }
.pager { justify-content: flex-end; display: flex; }
</style>
