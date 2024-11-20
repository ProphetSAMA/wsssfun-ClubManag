<template>
  <el-main>
    <el-form
      :model="searchParm"
      label-width="80px"
      :inline="true"
      size="default"
    >
      <el-form-item>
        <el-input v-model="searchParm.title" placeholder="请输入标题" />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button type="danger" icon="Close" plain @click="resetBtn">重置</el-button>
        <el-button type="primary" icon="Plus" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    <AddActivity ref="addActivityRef" />
  </el-main>
</template>

<script setup lang="ts">
import AddActivity from "./AddActivity.vue";
import useActivityTable from "@/composable/activity/useActivityTable";
import useActivity from "@/composable/activity/useActivity";

const { searchParm, getList, searchBtn, resetBtn } = useActivityTable();
const { addBtn, editBtn, deleteBtn, addActivityRef } = useActivity();


</script>

<style lang="scss" scoped></style>
