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
import {nextTick, reactive, ref} from "vue";
import {ElMessage, FormInstance} from "element-plus";
import {addCategoryApi, editCategoryApi} from "@/api/category";
import {CategoryModel} from "@/api/category/CategoryModel";
import {EditType, Title} from "@/type/BaseType";

// 表单的Ref属性
const formRef = ref<FormInstance>()

// 获取弹框属性
const {dialog, onClose, onConfirm, onShow} = useDialog()

// 显示弹框
const tags = ref('')

const show = (type: string, row: CategoryModel) => {
  tags.value = type
  // 设置弹框标题
  type == EditType.ADD ? dialog.title = Title.ADD : dialog.title = Title.EDIT
  dialog.height = 100
  onShow()

  // 数据回显
  if (EditType.EDIT == type && row) {
    nextTick(() => {
      Object.assign(addModel, row)
    })
  }
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

// 注册事件
const emits = defineEmits(['onFresh'])

// 表单提交
const commit = () => {
  formRef.value?.validate(async (valid) => {
    // 表单验证通过
    if (valid) {
      let res = null
      // 判断是新增还是编辑
      if (EditType.ADD == tags.value) {
        res = await addCategoryApi(addModel)
      } else {
        res = await editCategoryApi(addModel)
      }
      // 判断是否成功
      if (res && res.code == 200) {
        ElMessage.success(res.msg)
        emits('onFresh')
        onClose()
      }

    }
  })
}
</script>

<style scoped>

</style>