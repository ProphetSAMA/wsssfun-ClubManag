<template>
  <el-main>
    <!-- 搜索栏 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input
          placeholder="请输入姓名"
          v-model="searchParm.nickName"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="search" @click="searchBtn">搜索</el-button>
        <el-button
          icon="delete"
          @click="resetBtn"
          plain
          circle
          type="danger"
        ></el-button>
        <el-button icon="plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格-->
    <el-table :data="tableList" border style="width: 100%" stripe>
      <el-table-column prop="nickname" label="姓名"></el-table-column>
      <el-table-column prop="sex" label="性别">
        <template #default="scope">
          <el-tag v-if="scope.row.sex == '0'" type="primary" size="default"
            >男</el-tag
          >
          <el-tag v-if="scope.row.sex == '1'" type="danger" size="default"
            >女</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系方式"></el-table-column>
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
    <AddUser ref="addUserRef" @onFresh="getList" />
  </el-main>
</template>

<script setup lang="ts">
import useUser from "@/composable/user/useUser.ts";
import useUserTable from "@/composable/user/useUserTable.ts";
import AddUser from "@/views/user/AddUser.vue";

// 表格相关操作
const {
  searchParm,
  searchBtn,
  resetBtn,
  tableList,
  getList,
  sizeChange,
  currentChange,
} = useUserTable();

// 新增、编辑、删除
const { addBtn, editBtn, deleteBtn, addUserRef } = useUser(getList);
</script>

<style lang="scss" scoped></style>
