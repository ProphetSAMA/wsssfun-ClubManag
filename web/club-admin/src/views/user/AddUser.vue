<template>
  <SysDialog
      :title="dialog.title"
      :visible="dialog.visible"
      :width="dialog.width"
      :height="dialog.height"
      @onClose="onClose"
      @onConfirm="commit"
  >
    <template v-slot:content>
      <el-form :model="addModel" ref="addFormRef" :rules="rules" label-width="80px" :inline="false" size="default">
        <el-form-item prop="nickName" label="姓名">
          <el-input v-model="addModel.nickName"></el-input>
        </el-form-item>
        <el-form-item prop="sex" label="性别">
          <el-radio-group v-model="addModel.sex">
            <el-radio :value="0">男</el-radio>
            <el-radio :value="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="phone" label="联系方式">
          <el-input v-model="addModel.phone"></el-input>
        </el-form-item>
        <el-form-item prop="userName" label="登录账号">
          <el-input v-model="addModel.userName"></el-input>
        </el-form-item>
        <el-form-item prop="passWord" label="密码">
          <el-input v-model="addModel.passWord"></el-input>
        </el-form-item>
        <el-form-item prop="status" label="状态">
          <el-radio-group v-model="addModel.status">
            <el-radio :value="0">启用</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </template>
  </SysDialog>
</template>

<script setup lang="ts">

import useDialog from "@/hooks/useDialog.ts";
import SysDialog from "@/components/SysDialog.vue";
import {User} from "@/api/user/UserModel.ts";
import {reactive, ref} from "vue";
import {ElMessage, FormInstance} from "element-plus";
import {addUserApi} from '@/api/user'

// 表单ref属性
const addFormRef = ref<FormInstance>();
// 弹框属性
const {dialog, onClose, onConfirm, onShow} = useDialog();

// 表单绑定数据
const addModel = reactive<User>({
  userId: '',
  userName: '',
  passWord: '',
  nickName: '',
  phone: '',
  sex: 0, // 默认男
  status: 0 // 默认启用
});

// 表单验证规则
const rules = reactive({
  nickName: [
    {required: true, message: '请输入姓名', trigger: 'blur'},
    {min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur'}
  ],
  sex: [
    {required: true, message: '请选择性别', trigger: 'change'}
  ],
  phone: [
    {required: true, message: '请输入联系方式', trigger: 'blur'},
    {pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'}
  ],
  userName: [
    {required: true, message: '请输入登录账号', trigger: 'blur'},
    {min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur'}
  ],
  passWord: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}
  ],
  status: [
    {required: true, message: '请选择状态', trigger: 'change'}
  ]
});

// 显示弹框
const show = () => {
  dialog.height = 300
  onShow();
};

// 向外部组件暴露组件内部方法
defineExpose({
  show
});

// 提交
const commit = () => {
  // 验证
  addFormRef.value?.validate(async (valid) => {
    console.log(valid);
    // valid为true表示验证通过
    if (valid) {
      // 提交表单
      let res = await addUserApi(addModel)
      if (res && res.code == 200) {
        ElMessage.success(res.msg);
        onClose();
      }
    } else {
      return false;
    }
  });
};
</script>

<style scoped>
</style>
