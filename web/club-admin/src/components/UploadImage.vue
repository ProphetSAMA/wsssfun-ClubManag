<template>
  <el-upload
      action="#"
      list-type="picture-card"
      :auto-upload="false"
      ref="uploadRef"
      :on-change="uploadFile"
      :on-remove="handleRemove"
      :file-list="fileList"
      :limit="3"
      :on-exceed="moreLimit"
  >
    <el-icon>
      <Plus/>
    </el-icon>
  </el-upload>
  <el-dialog v-model="dialogVisible">
    <img w-full :src="dialogImageUrl" alt="Image"/>
  </el-dialog>
</template>

<script setup lang="ts">
import {UploadImageApi} from "@/api/img";
import {Plus} from "@element-plus/icons-vue";
import {ref} from "vue";
import {NewType} from "@/type/BaseType";
import {ElMessage, UploadFile, UploadUserFile, UploadFiles} from "element-plus";
import * as process from "node:process";

// 注册事件
const emits = defineEmits(['getImg'])
// 图片上传组件的ref属性
const uploadRef = ref()
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
type PropType = {
  fileList: UploadFile[]
  oldUrl: Array<{ url: string }>
}
// 接收父组件传递的参数
const props = withDefaults(defineProps<PropType>(),{
  fileList: () => [],
  oldUrl: () => []
})

// 返回给父组件的数据
const newImgRes = ref<NewType>({
  newImgUrl: [],
  deleteUrl: []
})

// 删除图片
const handleRemove = (file: UploadFile) => {
  if (props.oldUrl.some(item => item.url === file.name)) {
    newImgRes.value.deleteUrl.push({url: file.name})
    emits('getImg', newImgRes.value)
  } else {
    let images = newImgRes.value.newImgUrl.filter((item) => item.url !== file.name)
    newImgRes.value.newImgUrl = images
    emits('getImg', newImgRes.value)
  }
};

// 文件个数超出限制
const moreLimit = (files: File[], uploadFiles: UploadFiles) => {
  ElMessage.warning('最多只能上传' + uploadFiles.length + '张图片')
};

// 上传数据提交
const uploadFile = async (file: any) => {
  // 判断选择的文件是否为图片类型
  const typeArr = ['image/jpg', 'image/jpeg', 'image/png', 'image/gif']
  const isImg = typeArr.indexOf(file.raw.type) !== -1
  const isMore5M = file.size / 1024 / 1024 < 5

  // 判断图片格式是否正确
  if (!isImg) {
    ElMessage.warning('上传图片只能是 JPG、JPEG、PNG、GIF 格式!')
    uploadRef.value?.clearFiles()
    return
  }
  // 判断图片大小是否超过5M
  if (!isMore5M) {
    ElMessage.warning('上传图片大小不能超过 5MB!')
    uploadRef.value?.clearFiles()
    return
  }
  // 组装上传数据
  const formData = new FormData()
  formData.append('file', file.raw)
  // 提交数据
  const res = await UploadImageApi(formData)
  if (res && res.code == 200 && res.data) {
    ElMessage.success('上传成功')
    file.name = import.meta.env.BASE_API + res.data
    // 设置到返回子组件的数据中
    newImgRes.value.newImgUrl.push({
      url: import.meta.env.BASE_API + res.data
    })
    emits('getImg', newImgRes.value)
  }
};
// 清空数据
const clearImg = () => {
  uploadRef.value?.clearFiles()
  newImgRes.value.newImgUrl = []
  newImgRes.value.deleteUrl = []
}
defineExpose({
  clearImg
})

</script>

<style scoped>

</style>