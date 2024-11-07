<template>

  <SysDialog
      :title="dialog.title"
      :height="dialog.height"
      :width="dialog.width"
      :visible="dialog.visible"
      @on-close="onClose"
      @on-confirm="onConfirm"
  >
    <template v-slot:content>
      <el-form :model="addModel" ref="addFormRef" :rules="rules" label-width="80px" :inline="false" size="default">
        <el-form-item label="社团分类">
          <el-select
              v-model="addModel.categoryId"
              placeholder="请选择分类"
              style="width: 100%"
          >
            <el-option
                v-for="item in selectData"
                :key="item['value']"
                :label="item['label']"
                :value="item['value']"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </template>
  </SysDialog>
</template>

<script setup lang="ts">

import useDialog from "@/hooks/useDialog";
import SysDialog from "@/components/SysDialog.vue";
import {reactive} from "vue";
import useSelectCategory from "@/composable/team/useSelectCategory";

const {selectData, getSelect} = useSelectCategory()

// 弹框属性
const {dialog, onClose, onConfirm, onShow} = useDialog()
// 显示弹框
const show = () => {
  getSelect()
  onShow()
}
// 暴露方法,供父组件调用
defineExpose({
  show
})
// 表单绑定的对象
const addModel = reactive({
  id: "",
  categoryId: "",
  name: "",
  image: "",
  details: ""
})
// 表单验证规则
const rules = {
  name: [
    {required: true, message: "请输入社团名称", trigger: "blur"}
  ]
}
</script>

<style scoped>

</style>