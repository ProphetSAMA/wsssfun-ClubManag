<template>
  <el-main>
    <!-- 搜索栏 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input placeholder="请输入姓名" v-model:size="searchParm.nickName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="search" @click="searchBtn">搜索</el-button>
        <el-button icon="delete" @click="resetBtn" plain circle type="danger"></el-button>
        <el-button icon="plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 新增 -->
    <SysDialog
    :title="dialog.title"
    :visible="dialog.visible"
    :width="dialog.width"
    :height="dialog.height"
    @onClose="onClose"
    @onConfirm="onConfirm"
    >
      <template v-slot:content>
      </template>
    </SysDialog>
  </el-main>
</template>

<script setup lang="ts">
import {reactive} from 'vue'
import useDialog from "@/hooks/useDialog.ts";
import SysDialog from "@/components/SysDialog.vue";

const {dialog, onClose, onConfirm, onShow} = useDialog();

// 搜索表单绑定数据
const searchParm = reactive({
  nickName: '',
})

// 新增
const addBtn = () => {
  onShow()
}
// 搜索
const searchBtn = () => {

}
// 重置
const resetBtn = () => {
  searchParm.nickName = ''
}
</script>

<style lang="scss" scoped>

</style>