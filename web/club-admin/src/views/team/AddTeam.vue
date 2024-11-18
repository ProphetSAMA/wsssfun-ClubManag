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
      <el-form
        :model="addModel"
        ref="addFormRef"
        :rules="rules"
        label-width="80px"
        :inline="false"
        size="default"
      >
        <el-form-item prop="name" label="社团名称">
          <el-input v-model="addModel.name"></el-input>
        </el-form-item>
        <el-form-item prop="categoryId" label="社团分类">
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
        <el-form-item prop="image" label="社团图片">
          <UploadImage
            ref="upImgRef"
            @getImg="getImg"
            :oldUrl="oldUrl"
            :fileList="fileList"
          ></UploadImage>
        </el-form-item>
        <el-form-item prop="details" label="社团详情">
          <div style="border: 1px solid #ccc">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :default-config="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 300px; overflow-y: hidden"
              v-model="valueHtml"
              :defaultConfig="editorConfig"
              :mode="mode"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
    </template>
  </SysDialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch, nextTick } from "vue";
import useDialog from "@/hooks/useDialog";
import SysDialog from "@/components/SysDialog.vue";
import useSelectCategory from "@/composable/team/useSelectCategory";
import UploadImage from "@/components/UploadImage.vue";
import { ElMessage, FormInstance, UploadUserFile } from "element-plus";
import { EditType, NewType } from "@/type/BaseType";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import useEditor from "@/composable/team/useEditor";
import "@wangeditor/editor/dist/css/style.css";
import { addTeamApi, editTeamApi } from "@/api/team";
import { TeamType } from "@/api/team/TeamModel";

// 图片上传的ref属性
const upImgRef = ref();

// 表单ref属性
const addFormRef = ref<FormInstance>();

//文本编辑器
const {
  editorRef,
  editorConfig,
  handleCreated,
  valueHtml,
  toolbarConfig,
  mode,
} = useEditor();

const { selectData, getSelect } = useSelectCategory();
// 图片初始化
const fileList = ref<Array<UploadUserFile>>([]);
// 回显图片
const oldUrl = ref<Array<{ url: string }>>([]);
// 图片地址
const imgUrl = ref<Array<{ url: string }>>([]);
// 弹框属性
const { dialog, onClose, onConfirm, onShow } = useDialog();
// 定义新增、编辑标识
const tags = ref("");
// 显示弹框
const show = (type: string, row: TeamType) => {
  valueHtml.value = "";
  // 清空图片数据
  fileList.value = [];
  oldUrl.value = [];
  // 设置标识为当前选择的EDIT或DELETE
  tags.value = type;
  // 设置窗口尺寸
  dialog.height = 480;
  dialog.width = 1000;
  // 获取下拉数据
  getSelect();
  onShow();
  // 编辑数据回显
  if (type == EditType.EDIT && row) {
    nextTick(() => {
      Object.assign(addModel, row);
      // 处理图片回显
      if (row.image) {
        // 将图片路径按逗号分割成数组
        let imgs = addModel.image.split(",");
        for (let i = 0; i < imgs.length; i++) {
          let img = { name: "", url: "" };
          img.name = imgs[i];
          img.url = imgs[i];
          fileList.value.push(img);
          oldUrl.value.push({ url: imgs[i] });
        }
      }
      // 文本编辑器回显
      valueHtml.value = addModel.details;
    });
  }
  // 清空表单
  addFormRef.value?.resetFields();
};
// 暴露方法,供父组件调用
defineExpose({
  show,
});
// 表单绑定的对象
const addModel = reactive<TeamType>({
  id: "",
  categoryId: "",
  name: "",
  image: "",
  details: "",
});
// 表单验证规则
const rules = {
  name: [{ required: true, message: "请输入社团名称", trigger: "blur" }],
  categoryId: [
    { required: true, message: "请选择社团分类", trigger: "change" },
  ],
  image: [{ required: true, message: "请上传社团图片", trigger: "change" }],
  details: [{ required: true, message: "请输入社团详情", trigger: "blur" }],
};
// 子组件调用的方法
const getImg = (img: NewType) => {
  console.log(img);
  // 处理图片路径
  imgUrl.value = oldUrl.value.concat(img.newImgUrl);
  if (img.deleteUrl.length > 0) {
    // 过滤掉删除的图片路径
    let newArr = imgUrl.value.filter(
      (x) => !img.deleteUrl.some((item) => x.url === item.url)
    );
    imgUrl.value = newArr;
  }
  // 组装为逗号分隔的字符串
  let url = "";
  for (let k = 0; k < imgUrl.value.length; k++) {
    url += imgUrl.value[k].url + ",";
  }
  // 取消末尾逗号
  addModel.image = url.substring(0, url.lastIndexOf(","));
};

// 监听富文本内容
watch(
  () => valueHtml.value,
  (value) => {
    addModel.details = value;
  }
);
// 注册事件
const emits = defineEmits(["onFresh"]);
// 表单提交
const commit = () => {
  console.log(addModel);
  addFormRef.value?.validate(async (valid) => {
    if (valid) {
      let res = null;
      if (tags.value == EditType.ADD) {
        res = await addTeamApi(addModel);
      } else {
        res = await editTeamApi(addModel);
      }
      if (res && res.code == 200) {
        ElMessage.success(res.msg);
        // 刷新表格
        emits("onFresh");
        // 清空图片
        upImgRef.value?.clearImg();
        // 关闭弹窗
        dialog.visible = false;
        onClose();
      }
    }
  });
};
</script>

<style scoped></style>
