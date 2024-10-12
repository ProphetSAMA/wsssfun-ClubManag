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
  <AddUser ref="addUserRef"/>
  </el-main>
</template>

<script setup lang="ts">
import {reactive, ref} from 'vue'
import useUser from "@/composable/user/useUser.ts";
import useUserTable from "@/composable/user/useUserTable.ts";
import AddUser from "@/views/user/AddUser.vue";

// 新增、编辑、删除
const {addBtn, editBtn, deleteBtn, addUserRef} = useUser()
// 表格相关操作
const {searchParm, searchBtn, resetBtn} = useUserTable()


</script>

<style lang="scss" scoped>

</style>