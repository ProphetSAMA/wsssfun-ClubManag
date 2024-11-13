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
    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="image" label="社团图片">
        <template #default="scope">
          <el-image style="width: 60px;height: 60px;border-radius: 50%" :src="scope.row.image.split(',')[0]"></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="社团名称"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>
      <el-table-column align="center" width="200px" label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteBtn(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
    @size-change="sizeChange"
    @current-change="currentChange"
    :current-page.sync="searchParm.currentPage"
    :page-sizes="[10, 20, 30, 40]"
    :page-size="searchParm.pageSize"
    layout="total, sizes, prev, pager, next, jumper"
    :total="searchParm.total" background

    >

    </el-pagination>

    <!-- 弹框 -->
    <AddTeam ref="addTeamRef"></AddTeam>
  </el-main>
</template>

<script setup lang="ts">
import AddTeam from "@/views/team/AddTeam.vue";
import useTeamTable from "@/composable/team/useTeamTable";
import useTeam from "@/composable/team/useTeam";

// 表格
const {searchParm, searchBtn, resetBtn, tableList, sizeChange, currentChange, tableHeight} = useTeamTable()
// CURD
const {addBtn, editBtn, deleteBtn, addTeamRef} = useTeam()
</script>

<style lang="scss" scoped>

</style>