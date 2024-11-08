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
        <el-form-item label="社团图片">
          <UploadImage @getImg="getImg" :oldUrl="oldUrl" :fileList="fileList"></UploadImage>
        </el-form-item>
      </el-form>
    </template>
  </SysDialog>
</template>

<script setup lang="ts">

import {reactive, ref} from "vue";
import useDialog from "@/hooks/useDialog";
import SysDialog from "@/components/SysDialog.vue";
import useSelectCategory from "@/composable/team/useSelectCategory";
import UploadImage from "@/components/UploadImage.vue";
import UploadUserFile from "element-plus";
import {NewType} from "@/type/BaseType";

const {selectData, getSelect} = useSelectCategory()
// 图片初始化
const fileList = ref<Array<UploadUserFile>>([])
// 回显图片
const oldUrl = ref<Array<{ url: string }>>([])
// 图片地址
const imgUrl = ref<Array<{url: string}>>([])
// 弹框属性
const {dialog, onClose, onConfirm, onShow} = useDialog()
// 显示弹框
const show = () => {
  dialog.height = 480
  dialog.width = 1000
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
// 子组件调用的方法
const getImg = (img: NewType) => {

}
</script>

<style scoped>

</style>