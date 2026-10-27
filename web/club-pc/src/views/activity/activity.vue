<template>
  <div class="page">
    <div class="page-head"><h1>活动</h1></div>

    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="按标题搜索"
        clearable
        style="max-width: 260px"
        @keyup.enter="search"
        @clear="reset"
      />
      <el-button type="primary" @click="search">搜索</el-button>
      <el-button @click="reset">重置</el-button>
    </div>

    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />
    <div class="card" v-loading="loading">
      <div class="item" v-for="item in list" :key="item.id" @click="openDetail(item)">
        <el-image
          v-if="item.image"
          class="item-img"
          :src="imgUrl(item.image)"
          fit="cover"
        />
        <div class="item-body">
          <h3>{{ item.title }}</h3>
          <span class="date">{{ item.createTime }}</span>
        </div>
        <el-button
          size="small"
          :type="signedIds.includes(item.id) ? 'success' : 'primary'"
          :plain="signedIds.includes(item.id)"
          :loading="pendingId === item.id"
          @click.stop="toggleSignup(item)"
        >{{ signedIds.includes(item.id) ? '已报名' : '报名' }}</el-button>
      </div>
      <el-empty
        v-if="list.length === 0"
        :description="keyword ? '没有匹配的活动' : '暂无活动'"
      />
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

  <ContentDetail v-model="detailVisible" :item="current" type-label="活动" />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import { signupApi, cancelSignupApi, mySignupsApi, newsDetailApi } from "@/api/signup";
import { imgUrl } from "@/utils/image";
import ContentDetail from "@/components/ContentDetail.vue";
import { useContentList } from "@/composables/useContentList";

const { list, total, currentPage, size, keyword, loading, error, fetchList, search, reset, pageChange } =
  useContentList("0");

const router = useRouter();
const userStore = useUserStore();

/** 已报名的活动 id */
const signedIds = ref<number[]>([]);
const pendingId = ref(0);

/** 详情弹框；打开时顺带取一次详情，让后端把浏览量 +1 */
const detailVisible = ref(false);
const current = ref<any>(null);
const openDetail = async (row: any) => {
  current.value = row;
  detailVisible.value = true;
  try {
    const res = (await newsDetailApi(row.id)) as any;
    if (res && res.code === 200) current.value = res.data;
  } catch (e) {
    console.error(e);
  }
};

const loadMySignups = async () => {
  if (!userStore.isLoggedIn) return;
  try {
    const res = (await mySignupsApi(1, 200)) as any;
    if (res && res.code === 200) {
      signedIds.value = (res.data.records || []).map((r: any) => r.newsId);
    }
  } catch (e) {
    console.error(e);
  }
};

const toggleSignup = async (row: any) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再报名");
    router.push("/login");
    return;
  }
  pendingId.value = row.id;
  try {
    if (signedIds.value.includes(row.id)) {
      const res = (await cancelSignupApi(row.id)) as any;
      if (res && res.code === 200) {
        signedIds.value = signedIds.value.filter((id) => id !== row.id);
        ElMessage.success(res.msg || "已取消报名");
      }
    } else {
      const res = (await signupApi(row.id)) as any;
      if (res && res.code === 200) {
        signedIds.value = [...signedIds.value, row.id];
        ElMessage.success(res.msg || "报名成功");
      }
    }
  } finally {
    pendingId.value = 0;
  }
};

onMounted(async () => {
  await fetchList();
  await loadMySignups();
});
</script>

<style scoped lang="scss">
.page { display: flex; flex-direction: column; gap: 20px; }
.page-head h1 { font-size: 20px; font-weight: 700; color: var(--text-1); }

.toolbar { display: flex; gap: 10px; align-items: center; }

.pager { justify-content: flex-end; display: flex; }

.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.15s;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); }
}

.item-img {
  width: 110px;
  height: 75px;
  border-radius: 6px;
  flex-shrink: 0;
  object-fit: cover;
}

.item-body {
  flex: 1;
  min-width: 0;

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-1);
    margin-bottom: 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.date { font-size: 12px; color: var(--text-3); }
</style>
