<template>
  <div class="detail-page" v-loading="loading">
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />

    <div class="back">
      <el-button text @click="$router.push('/club')">← 返回社团列表</el-button>
    </div>

    <div class="card" v-if="team">
      <div class="head">
        <el-image class="cover" :src="team.image ? imgUrl(team.image) : defaultImg" fit="cover" />
        <div class="head-info">
          <h1>{{ team.name }}</h1>
          <p class="meta">成立时间 {{ team.createTime || '—' }} · 成员 {{ members.length }} 人</p>
          <el-button
            v-if="joined"
            size="small"
            plain
            disabled
          >已加入</el-button>
          <el-button
            v-else
            size="small"
            type="primary"
            :loading="joining"
            @click="join"
          >加入社团</el-button>
        </div>
      </div>

      <div class="section">
        <div class="section-title">社团简介</div>
        <div class="rich" v-html="detailsHtml"></div>
      </div>

      <div class="section">
        <div class="section-title">成员（{{ members.length }}）</div>
        <div class="members" v-if="members.length">
          <div class="member" v-for="(m, i) in members" :key="i">
            <span class="name">{{ m.userName || '（未命名）' }}</span>
            <el-tag v-if="m.role === 2" type="danger" size="small">社长</el-tag>
            <el-tag v-else-if="m.role === 1" type="warning" size="small">管理员</el-tag>
            <el-tag v-else type="info" size="small">成员</el-tag>
            <span class="join">{{ m.joinTime }}</span>
          </div>
        </div>
        <el-empty v-else description="暂无成员" :image-size="60" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import defaultImg from "@/assets/jitashe.jpg";
import http from "@/http";
import { imgUrl } from "@/utils/image";
import { useUserStore } from "@/store/user";
import { joinTeamApi, myTeamsApi } from "@/api/member";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const team = ref<any>(null);
const members = ref<any[]>([]);
const loading = ref(true);
const error = ref("");
const joining = ref(false);
const joined = ref(false);

const teamId = computed(() => Number(route.params.id));

const detailsHtml = computed(() =>
  team.value && team.value.details ? team.value.details : "<p>暂无简介</p>"
);

const load = async () => {
  loading.value = true;
  error.value = "";
  try {
    const [t, m] = await Promise.all([
      http.get(`/api/team/${teamId.value}`),
      http.get(`/api/team/${teamId.value}/members`),
    ]) as any[];
    if (t?.code === 200) team.value = t.data;
    else error.value = t?.msg || "社团不存在";
    if (m?.code === 200) members.value = m.data || [];
  } catch (e) {
    console.error(e);
    error.value = "社团信息加载失败，请稍后重试";
  } finally {
    loading.value = false;
  }
};

const loadJoined = async () => {
  if (!userStore.isLoggedIn) return;
  try {
    const res = (await myTeamsApi(userStore.userId)) as any;
    if (res?.code === 200) {
      joined.value = (res.data?.records || []).some((m: any) => m.teamId === teamId.value);
    }
  } catch (e) {
    console.error(e);
  }
};

const join = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再加入社团");
    router.push("/login");
    return;
  }
  joining.value = true;
  try {
    const res = (await joinTeamApi({ teamId: teamId.value, userId: userStore.userId })) as any;
    if (res?.code === 200) {
      ElMessage.success(res.msg || "加入成功");
      joined.value = true;
      await load();
    }
  } finally {
    joining.value = false;
  }
};

onMounted(async () => {
  await load();
  await loadJoined();
});
</script>

<style scoped lang="scss">
.detail-page { display: flex; flex-direction: column; gap: 12px; }
.back { display: flex; }
.err { margin-bottom: 8px; }

.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 20px;
}

.head {
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-light);
  margin-bottom: 20px;
}

.cover {
  width: 260px;
  height: 170px;
  border-radius: var(--radius);
  flex-shrink: 0;
}

.head-info {
  flex: 1;
  min-width: 0;

  h1 {
    font-size: 22px;
    font-weight: 700;
    color: var(--text-1);
    margin-bottom: 8px;
  }
}

.meta { font-size: 13px; color: var(--text-3); margin-bottom: 16px; }

.section { margin-bottom: 24px; }

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-light);
}

.rich {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-2);

  :deep(img) { max-width: 100%; border-radius: 6px; }
  :deep(p) { margin: 0 0 10px; }
}

.members {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 10px;
}

.member {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid var(--border-light);
  border-radius: 6px;

  .name { font-size: 13px; color: var(--text-1); }
  .join { margin-left: auto; font-size: 12px; color: var(--text-3); }
}

@media (max-width: 768px) {
  .head { flex-direction: column; }
  .cover { width: 100%; }
}
</style>
