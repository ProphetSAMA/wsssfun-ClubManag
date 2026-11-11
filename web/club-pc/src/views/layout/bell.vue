<template>
  <el-popover placement="bottom-end" :width="330" trigger="click" @show="load">
    <template #reference>
      <div class="bell" title="通知">
        <el-badge :value="unread" :hidden="unread === 0" :max="99">
          <el-icon :size="20"><Bell /></el-icon>
        </el-badge>
      </div>
    </template>

    <div class="panel">
      <div class="panel-head">
        <span>通知</span>
        <el-button link size="small" :disabled="unread === 0" @click="readAll">全部已读</el-button>
      </div>
      <div v-if="list.length === 0" class="empty">暂无通知</div>
      <div
        v-for="item in list"
        :key="item.id"
        class="item"
        :class="{ unread: item.isRead === 0 }"
        @click="open(item)"
      >
        <div class="row">
          <span class="title">{{ item.title }}</span>
          <span class="time">{{ item.createTime }}</span>
        </div>
        <div v-if="item.content" class="content">{{ item.content }}</div>
      </div>
      <div class="panel-foot">
        <el-button link size="small" @click="goAll">查看全部</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { myNotificationsApi, unreadCountApi, readNotificationApi, readAllNotificationApi } from "@/api/notification";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();
const unread = ref(0);
const list = ref<any[]>([]);

const loadUnread = async () => {
  if (!userStore.token) {
    unread.value = 0;
    return;
  }
  const res = (await unreadCountApi()) as any;
  if (res?.code === 200) {
    unread.value = Number(res.data) || 0;
  }
};

const load = async () => {
  const res = (await myNotificationsApi(1, 5)) as any;
  if (res?.code === 200) {
    list.value = res.data.records || [];
  }
  loadUnread();
};

const open = async (item: any) => {
  if (item.isRead === 0) {
    await readNotificationApi(item.id);
    item.isRead = 1;
    unread.value = Math.max(0, unread.value - 1);
  }
  if (item.link) {
    router.push(item.link);
  }
};

const readAll = async () => {
  const res = (await readAllNotificationApi()) as any;
  if (res?.code === 200) {
    ElMessage.success(res.msg || "已全部标记为已读");
    list.value.forEach((item: any) => { item.isRead = 1; });
    unread.value = 0;
  }
};

const goAll = () => {
  router.push("/mine/message");
};

onMounted(loadUnread);
</script>

<style scoped lang="scss">
.bell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  transition: background 0.15s;

  &:hover { background: rgba(255, 255, 255, 0.12); }
}

.panel { max-height: 420px; overflow-y: auto; }

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-1);
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-light);
}

.item {
  padding: 10px 4px;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;

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

.row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.title { font-size: 13px; color: var(--text-1); }
.time { font-size: 11px; color: var(--text-3); flex-shrink: 0; }
.content { font-size: 12px; color: var(--text-2); margin-top: 4px; line-height: 1.5; }
.empty { font-size: 13px; color: var(--text-3); text-align: center; padding: 24px 0; }
.panel-foot { text-align: center; padding-top: 8px; }
</style>
