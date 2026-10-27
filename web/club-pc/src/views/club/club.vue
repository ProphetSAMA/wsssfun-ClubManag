<template>
  <div class="club-page">
    <div class="page-head">
      <h1>社团</h1>
    </div>
    <div class="layout">
      <aside class="sidebar">
        <div class="sidebar-title">分类</div>
        <div
          class="cat"
          :class="{ active: selectedCategory === null }"
          @click="selectCategory(null)"
        >全部</div>
        <div
          class="cat"
          :class="{ active: selectedCategory === item.value }"
          v-for="item in categoryList"
          :key="item.value"
          @click="selectCategory(item.value)"
        >{{ item.label }}</div>
      </aside>
      <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon />
      <div class="card" v-loading="loading">
        <div class="item" v-for="item in teamList" :key="item.id" @click="openDetail(item)">
          <el-image
            class="item-img"
            :src="item.image ? imgUrl(item.image) : defaultImg"
            fit="cover"
          />
          <div class="item-body">
            <div class="item-name">{{ item.name }}</div>
            <span class="date">{{ item.createTime }}</span>
          </div>
          <el-button
            v-if="joinedIds.includes(item.id)"
            size="small"
            plain
            disabled
          >已加入</el-button>
          <el-button
            v-else
            size="small"
            type="primary"
            :loading="joiningId === item.id"
            @click.stop="joinTeam(item)"
          >加入</el-button>
        </div>
        <el-empty v-if="teamList.length === 0" description="暂无社团" />
      </div>
    </div>
  </div>

  <ContentDetail v-model="detailVisible" :item="current" type-label="社团" />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import defaultImg from "@/assets/jitashe.jpg";
import http from "@/http";
import { imgUrl } from "@/utils/image";
import { useUserStore } from "@/store/user";
import { joinTeamApi, myTeamsApi } from "@/api/member";
import ContentDetail from "@/components/ContentDetail.vue";

const router = useRouter();
const userStore = useUserStore();

const categoryList = ref<any[]>([]);
const teamList = ref<any[]>([]);
const selectedCategory = ref<number | null>(null);
/** 已加入的社团 id，用来把按钮置成"已加入" */
const joinedIds = ref<number[]>([]);
/** 详情弹框 */
const detailVisible = ref(false);
const current = ref<any>(null);

const openDetail = (row: any) => {
  current.value = row;
  detailVisible.value = true;
};
const joiningId = ref(0);
const loading = ref(true);
const error = ref("");

onMounted(async () => {
  try {
    const [c, t] = await Promise.all([
      http.get("/api/category/getSelectList"),
      http.get("/api/team/list", { currentPage: 1, pageSize: 50 }),
    ]);
    if (c?.code === 200) categoryList.value = c.data || [];
    if (t?.code === 200) teamList.value = t.data.records || [];
  } catch (e) {
    console.error(e);
    error.value = "社团列表加载失败，请稍后重试";
  } finally {
    loading.value = false;
  }
  await loadJoined();
});

/** 登录后才需要知道"我加入了哪些社团" */
const loadJoined = async () => {
  if (!userStore.isLoggedIn) return;
  try {
    const res = await myTeamsApi(userStore.userId) as any;
    if (res?.code === 200) joinedIds.value = (res.data?.records || []).map((m: any) => m.teamId);
  } catch (e) { console.error(e); }
};

/** 加入社团：未登录先引导登录 */
const joinTeam = async (team: any) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再加入社团");
    router.push("/login");
    return;
  }
  joiningId.value = team.id;
  try {
    const res = await joinTeamApi({ teamId: team.id, userId: userStore.userId }) as any;
    if (res?.code === 200) {
      ElMessage.success(res.msg || "加入成功");
      joinedIds.value = [...joinedIds.value, team.id];
    }
  } catch (e) {
    console.error(e);
  } finally {
    joiningId.value = 0;
  }
};

const selectCategory = (id: number | null) => {
  selectedCategory.value = id;
};
</script>

<style scoped lang="scss">
.club-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-head h1 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-1);
}

.layout {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 20px;
}

.sidebar {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 12px;
  height: fit-content;
  position: sticky;
  top: calc(var(--header-h) + 24px);
}

.sidebar-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-1);
  padding-bottom: 10px;
  margin-bottom: 6px;
  border-bottom: 1px solid var(--border-light);
}

.cat {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: var(--text-2);
  cursor: pointer;
  transition: all 0.15s;

  &:hover { background: var(--bg-hover); }
  &.active { background: var(--primary-bg); color: var(--primary); font-weight: 500; }
}

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
}

.item-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: 6px;
}

.date { font-size: 12px; color: var(--text-3); }

@media (max-width: 768px) {
  .layout { grid-template-columns: 1fr; }
}
</style>
