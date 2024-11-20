<template>
  <SysDialog 
    :title="dialog.title"
    :visible="dialog.visible"
    :width="dialog.width"
    :height="dialog.height"
    @on-close="onClose"
    @on-confirm="onConfirm"
    @on-show="onShow"
  >
    <template v-slot:content>

    </template>
  </SysDialog>
</template>

<script setup lang="ts">
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import { on } from "events";

// 获取弹窗实例
const { dialog, onClose, onConfirm, onShow } = useDialog();

// 显示弹框
const show = () => {
    onShow();
}

// 暴露方法
defineExpose({
    show,
})
</script>

<style lang="scss" scoped></style>
