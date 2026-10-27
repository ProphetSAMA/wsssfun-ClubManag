<template>
  <div class="card" v-loading="loading">
    <div class="card-title">我报名的活动</div>
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />

    <div class="list">
      <div class="item" v-for="item in list" :key="item.id">
        <el-image
          v-if="item.image"
          class="item-img"
          :src="imgUrl(item.image)"
          fit="cover"
        />
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="meta">
            活动时间 {{ item.activityTime || '—' }}
            <template v-if="item.signupTime"> · 报名于 {{ item.signupTime }}</template>
          </span>
        </div>
        <el-button
          size="small"
          type="danger"
          plain
          :loading="pendingId === item.newsId"
          @click="cancel(item)"
        >取消报名</el-button>
      </div>
      <el-empty v-if="!loading && list.length === 0" description="还没有报名任何活动" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { imgUrl } from '@/utils/image'
import { mySignupsApi, cancelSignupApi } from '@/api/signup'

const list = ref<any[]>([])
const loading = ref(true)
const error = ref('')
const pendingId = ref(0)

const getList = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = (await mySignupsApi(1, 100)) as any
    if (res && res.code === 200) {
      list.value = res.data.records || []
    }
  } catch (e) {
    console.error(e)
    error.value = '报名信息加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const cancel = async (row: any) => {
  pendingId.value = row.newsId
  try {
    const res = (await cancelSignupApi(row.newsId)) as any
    if (res && res.code === 200) {
      ElMessage.success(res.msg || '已取消报名')
      await getList()
    }
  } finally {
    pendingId.value = 0
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

.list {
  display: flex;
  flex-direction: column;
}

.item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-light);

  &:last-child { border-bottom: none; }
}

.item-img {
  width: 100px;
  height: 68px;
  border-radius: 6px;
  flex-shrink: 0;
}

.item-body {
  flex: 1;
  min-width: 0;

  h3 {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-1);
    margin-bottom: 4px;
  }
}

.meta { font-size: 12px; color: var(--text-3); }

.err { margin-bottom: 12px; }
</style>
