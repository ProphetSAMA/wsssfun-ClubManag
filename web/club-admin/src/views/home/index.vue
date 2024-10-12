<template>
  <div>
    <el-button type="primary" size="default" @click="addBtn">新增</el-button>
    <!--    弹框组件-->
    <SysDialog
        :title="dialog.title"
        :visible="dialog.visible"
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
import {reactive} from "vue";
import useDialog from "@/hooks/useDialog.ts";
// 获取弹框属性
const {dialog, onClose, onConfirm} = useDialog()

// // 弹框属性
// const dialog = reactive({
//   title: '新增',
//   visible: false,
// })

// 按钮点击事件
const addBtn = () => {
  dialog.visible = true
};

// const onclose = () => {
//   dialog.visible = false
// }
// const onConfirm = () => {
//   dialog.visible = false
// }
</script>

<style lang="scss" scoped>

</style>