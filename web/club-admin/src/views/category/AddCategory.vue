<template>
  <SysDialog
      :title="dialog.title"
      :height="dialog.height"
      :width="dialog.width"
      :visible="dialog.visible"
      @on-close="onClose"
      @on-confirm="commit"
  >
    <template v-slot:content>

      <el-form :model="addModel" ref="formRef" :rules="rules" label-width="80px" :inline="false" size="default">

        <el-form-item prop="name" label="分类名称">
          <el-input v-model="addModel.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>

        <el-form-item prop="orderNum" label="分类序号">
          <el-input v-model="addModel.orderNum" placeholder="请输入分类序号"></el-input>
        </el-form-item>

      </el-form>

    </template>
  </SysDialog>

</template>

<script setup lang="ts">
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import {reactive, ref} from "vue";
import {FormInstance} from "element-plus";

// 表单的Ref属性
const formRef = ref<FormInstance>()

// 获取弹框属性
const {dialog, onClose, onConfirm, onShow} = useDialog()
// 显示弹框
const show = () => {
  dialog.height = 100
  onShow()
  // 清空表单
  formRef.value?.resetFields()
};
// 暴露出去给外部组件使用
defineExpose({
  show
})

// 弹框绑定的属性
const addModel = reactive({
  id: '',
  name: '',
  orderNum: ""
})

// 表单验证规则
const rules = reactive({
  name: [
    {required: true, message: '请输入分类名称', trigger: 'blur'}
  ]
})

// 表单提交
const commit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      onClose()
    }
  })
}
</script>

<style scoped>

</style>