<template>
    <SysDialog :title="dialog.title" :visible="dialog.visible" :width="dialog.width" :height="dialog.height"
        @on-close="onClose" @on-confirm="commit">

        <template v-slot:content>
            <!-- 表单 -->
            <el-form :model="addModel" ref="addFormRef" :rules="rules" label-width="80px" :inline="false"
                size="default">
                <el-row>
                    <el-col :span="12" :offset="0">
                        <el-form-item type="title" label="标题">
                            <el-input v-model="addModel.title"> </el-input>
                        </el-form-item>
                    </el-col>
                    <!-- 类型 -->
                    <el-col :span="12" :offset="0">
                        <el-form-item label="类型" prop="type">
                            <el-radio-group v-model="addModel.type">
                                <el-radio :value="0">活动</el-radio>
                                <el-radio :value="1">新闻</el-radio>
                                <el-radio :value="2">公告</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 首页推荐 -->
                <el-form-item label="首页推荐" prop="hasTop">
                    <el-radio-group v-model="addModel.hasTop">
                        <el-radio :value="0">否</el-radio>
                        <el-radio :value="1">是</el-radio>
                    </el-radio-group>
                </el-form-item>

                <!-- 封面图 -->
                <el-form-item prop="image" label="封面图">
                    <UploadImage ref="upImgRef" @getImg="getImg" :oldUrl="oldUrl" :fileList="fileList" />
                </el-form-item>

                <!-- 社团详情 -->
                <el-form-item prop="details" label="社团详情">
                    <div style="border: 1px solid #ccc">
                        <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef"
                            :default-config="toolbarConfig" :mode="mode" />
                        <Editor style="height: 300px; overflow-y: hidden" v-model="valueHtml"
                            :defaultConfig="editorConfig" :mode="mode" @onCreated="handleCreated" />
                    </div>
                </el-form-item>

            </el-form>
        </template>
    </SysDialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from "vue";
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import UploadImage from "@/components/UploadImage.vue";
import { EditType, NewType, Title } from "@/type/BaseType";
import {
    ElMessage,
    FormInstance,
    UploadFile,
    UploadUserFile,
} from "element-plus";

import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import useEditor from "@/composable/team/useEditor";
import "@wangeditor/editor/dist/css/style.css";
import { addNewsApi, editNewsApi, deleteNewsApi, getNewsListApi } from "@/api/news";

// 表单ref属性
const addFormRef = ref<FormInstance>();

// 显示弹框
const tags = ref("");

//文本编辑器
const {
    editorRef,
    editorConfig,
    handleCreated,
    valueHtml,
    toolbarConfig,
    mode,
} = useEditor();

// 图片初始化
const fileList = ref<Array<UploadUserFile>>([]);
// 回显图片
const oldUrl = ref<Array<{ url: string }>>([]);
// 图片地址
const imgUrl = ref<Array<{ url: string }>>([]);

// 获取弹窗实例
const { dialog, onClose, onConfirm, onShow } = useDialog();

// 显示弹框
const show = (type: EditType, row?: NewsType) => {
    dialog.width = 1200;
    dialog.height = 600;
    
    // 使用严格的枚举比较
    if (type === EditType.ADD) {
        dialog.title = Title.ADD;
        // 重置表单
        addModel.id = "";
        addModel.type = "";
        addModel.createId = "";
        addModel.image = "";
        addModel.title = "";
        addModel.details = "";
        addModel.hasTop = "";
        fileList.value = [];
        oldUrl.value = [];
        valueHtml.value = "";
    } else {
        dialog.title = Title.EDIT;
        if (row) {
            nextTick(() => {
                Object.assign(addModel, row);
                // 处理图片回显
                if (row.image) {
                    fileList.value = [];
                    oldUrl.value = [];
                    const imgs = row.image.split(",");
                    imgs.forEach((img: string) => {
                        fileList.value.push({ name: img, url: img });
                        oldUrl.value.push({ url: img });
                    });
                }
                valueHtml.value = row.details;
            });
        }
    }
    
    onShow();
};

// 暴露方法
defineExpose({
    show,
});

//表单绑定的对象
const addModel = reactive({
    id: "",
    type: "",
    createId: "",
    image: "",
    title: "",
    details: "",
    hasTop: "",
});

// 表单验证规则
const rules = reactive({
    title: [{ required: true, message: "请输入标题", trigger: "blur" }],
    type: [{ required: true, message: "请选择类型", trigger: "blur" }],
    hasTop: [{ required: true, message: "请选择首页推荐", trigger: "blur" }],
    image: [{ required: true, message: "请上传封面图", trigger: "blur" }],
    details: [{ required: true, message: "请输入社团详情", trigger: "blur" }],
});

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
// 注册刷新事件
const emits = defineEmits(['onRefresh'])
// 提交
const commit = () => {
    console.log(addModel);
    // 只在表单验证通过后提交
    addFormRef.value?.validate(async (valid) => {
        if (valid) {
            let res = null
            if (tags.value === EditType.ADD) {
                res = await addNewsApi(addModel);
            } else {
                res = await editNewsApi(addModel);
            }
            if (res && res.code == 200) {
                // 信息提示
                ElMessage.success(res.msg);
                // 刷新列表
                emits('onRefresh')
                // 关闭弹框
                onClose();
            }
        }
    });
};
</script>

<style lang="scss" scoped></style>
