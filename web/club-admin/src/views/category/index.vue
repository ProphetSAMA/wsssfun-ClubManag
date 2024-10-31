<template>
  <el-main>
    <!-- 搜索栏 -->
    <el-form :model="searchParm" :inline="true" size="default">

      <el-form-item>
        <el-input v-model="searchParm.name" placeholder="请输入分类名称"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button icon="Search">搜索</el-button>
        <el-button icon="Close" type="danger" plain>重置</el-button>
        <el-button icon="Plus" type="primary" @click="addBtn">创建</el-button>
      </el-form-item>

    </el-form>

    <!-- 新增弹框 -->
    <AddCategory ref="addFormRef"></AddCategory>

  </el-main>

</template>

<script setup lang="ts">
import AddCategory from "@/views/category/AddCategory.vue";
import useCategory from "@/composable/category/useCategory";
import useTableCategory from "@/composable/category/useTableCategory";

const {addBtn, deleteBtn, editBtn, addFormRef} = useCategory()
const {searchParm} = useTableCategory()
</script>

<style lang="scss" scoped>

</style>