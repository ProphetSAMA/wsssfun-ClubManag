<template>
  <el-dialog
    v-model="visible"
    :title="item?.title || item?.name || '详情'"
    width="720px"
    align-center
    class="detail-dialog"
  >
    <div class="meta">
      <el-tag v-if="typeLabel" size="small" type="info">{{ typeLabel }}</el-tag>
      <span class="date">{{ item?.createTime }}</span>
    </div>
    <el-image v-if="item?.image" class="cover" :src="imgUrl(item.image)" fit="cover" />
    <div class="body" v-html="bodyHtml"></div>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { imgUrl } from "@/utils/image";

/**
 * 内容详情弹框：社团 / 活动 / 新闻 / 公告共用。
 *
 * 后端 details 是管理后台富文本编辑器存的 HTML，这里直接渲染。
 */
const props = defineProps<{
  modelValue: boolean;
  item: any | null;
  typeLabel?: string;
}>();

const emit = defineEmits<{ "update:modelValue": [boolean] }>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit("update:modelValue", value),
});

const bodyHtml = computed(() => {
  const details = props.item?.details;
  return details && String(details).trim()
    ? details
    : '<p class="empty">暂无详细内容</p>';
});
</script>

<style scoped lang="scss">
.meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.date {
  font-size: 12px;
  color: var(--text-3);
}

.cover {
  width: 100%;
  max-height: 300px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.body {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-2);
  max-height: 50vh;
  overflow-y: auto;

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 6px;
  }

  :deep(p) { margin: 0 0 10px; }
  :deep(.empty) { color: var(--text-3); }

  :deep(h1), :deep(h2), :deep(h3) {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-1);
    margin: 16px 0 8px;
  }

  :deep(ul), :deep(ol) {
    padding-left: 20px;
    margin: 0 0 10px;
  }

  :deep(a) { color: var(--primary); }
}
</style>
