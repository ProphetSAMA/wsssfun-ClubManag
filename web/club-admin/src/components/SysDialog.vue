<template>
  <el-dialog :title="title"
             :model-value="visible"
             :before-close="onClose"
             append-to-body
             :width="width + 'px'"
             :close-on-click-modal="false"
  >
    <div class="container" :style="{height: height + 'px'}">
      <slot name="content"></slot>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="danger" @click="onClose">取消</el-button>
        <el-button type="primary" @click="onConfirm">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
// 定义弹框属性的数据类型
interface DialogProps {
  title: string, // 标题
  visible: boolean, // 控制弹框显示 / 隐藏
  width: number,
  height: number
}

// 接收父组件传递的值
const props = withDefaults(defineProps<DialogProps>(), {
  title: '标题',
  visible: false,
  width: 500,
  height: 200
})

const emit = defineEmits(['onClose','onConfirm'])
// 弹框关闭
const onClose = () => {
  emit('onClose')
}
// 弹框确定
const onConfirm = () => {
  emit('onConfirm')
}
</script>

<style scoped lang="scss">
.container {
  overflow-x: initial;
  overflow-y: auto;
}

.el-dialog {
  border-top-left-radius: 7px !important;
  border-top-right-radius: 7px !important;
  .el-dialog__header {
    border-top-left-radius: 7px !important;
    border-top-right-radius: 7px !important;
    background-color: #0f4980 !important;
    .el-dialog__title {
      color: white;
      font-size: 16px;
      font-weight: 600;
    }
  }
  .el-dialog__headerbtn {
    .el-dialog__close {
      color: white;
    }
  }
  .el-dialog__body {
    padding: 10px;
  }
  .el-dialog__footer {
    border-top: 1px solid #1a1a1a !important;
    padding: 10px;
  }
}
</style>