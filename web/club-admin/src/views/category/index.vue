<template>
  <el-main>
    <!-- 搜索栏 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input
          v-model="searchParm.name"
          placeholder="请输入分类名称"
        ></el-input>
      </el-form-item>

      <el-form-item>
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button icon="Close" type="danger" plain @click="resetBtn"
          >重置</el-button
        >
        <el-button icon="Plus" type="primary" @click="addBtn">创建</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格-->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="orderNum" label="序号"></el-table-column>
      <el-table-column prop="name" label="分类名称"></el-table-column>
      <el-table-column label="操作" width="220" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editBtn(scope.row)"
            >编辑</el-button
          >
          <el-button type="danger" size="small" @click="deleteBtn(scope.row)"
            >删除</el-button
          >
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
      :total="searchParm.total"
      background
    >
    </el-pagination>
    <!-- 新增弹框 -->
    <AddCategory ref="addFormRef" @onFresh="getList"></AddCategory>
  </el-main>
</template>

<script setup lang="ts">
import AddCategory from "@/views/category/AddCategory.vue";
import useCategory from "@/composable/category/useCategory";
import useTableCategory from "@/composable/category/useTableCategory";
import { getListApi } from "@/api/user";
const {
  searchParm,
  tableList,
  sizeChange,
  currentChange,
  tableHeight,
  searchBtn,
  resetBtn,
  getList,
} = useTableCategory();
const { addBtn, deleteBtn, editBtn, addFormRef } = useCategory(getList);
</script>

<style lang="scss" scoped></style>
