<template>
  <div>
    <el-button type="primary" size="default" @click="addBtn">新增</el-button>
    <!--    弹框组件-->
    <SysDialog
        :title="dialog.title"
        :visible="dialog.visible"
        :width="dialog.width"
        :height="dialog.height"
        @onClose="onClose"
        @onConfirm="onConfirm"
    >
      <template v-slot:content>
        弹框测试
      </template>
    </SysDialog>
  </div>
</template>

<script setup>
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog.ts";
// 获取弹框属性
const {dialog, onClose, onConfirm} = useDialog()

// 按钮点击事件
const addBtn = () => {
  dialog.visible = true
};

</script>

<style lang="scss" scoped>

</style>