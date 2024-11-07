<template>
  <el-main>
    <!-- 顶部搜索 -->
    <el-form :model="searchParm" label-width="80px" :inline="true" size="default">
      <el-form-item label="">
        <el-input v-model="searchParm.name" placeholder="请输入社团名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button icon="Close" @click="resetBtn" plain type="danger">重置</el-button>
        <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 弹框 -->
    <AddTeam ref="addTeamRef"></AddTeam>
  </el-main>
</template>

<script setup lang="ts">
import AddTeam from "@/views/team/AddTeam.vue";
import useTeamTable from "@/composable/team/useTeamTable";
import useTeam from "@/composable/team/useTeam";

// 表格
const {searchParm, searchBtn, resetBtn} = useTeamTable()
// CURD
const {addBtn, editBtn, deleteBtn, addTeamRef} = useTeam()
</script>

<style lang="scss" scoped>

</style>