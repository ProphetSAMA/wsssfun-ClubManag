<template>
  <SysDialog
      :title="dialog.title"
      :visible="dialog.visible"
      :width="dialog.width"
      :height="dialog.height"
      @onClose="onClose"
      @onConfirm="onConfirm"
  >
    <template v-slot:content>
    </template>
  </SysDialog>
</template>

<script setup lang="ts">

import useDialog from "@/hooks/useDialog.ts";
import SysDialog from "@/components/SysDialog.vue";

const {dialog, onClose, onConfirm, onShow} = useDialog();

// 显示弹框
const show = () => {
  onShow()
}

// 向外部组件 暴露组件内部方法
defineExpose({
  show
})
</script>

<style scoped>

</style>