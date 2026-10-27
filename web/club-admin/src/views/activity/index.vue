<template>
    <el-main>
        <!-- 搜索 -->
        <el-form :model="searchParm" label-width="80px" :inline="true" size="default">

            <!-- 标题 -->
            <el-form-item>
                <el-input v-model="searchParm.title" placeholder="请输入标题" />
            </el-form-item>

            <!-- 类型 -->
            <el-form-item>
                <el-select v-model="searchParm.type" clearable placeholder="请选择类型" popper-class="select-dropdown">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>

            <!-- 发布状态 -->
            <el-form-item>
                <el-select v-model="searchParm.status" clearable placeholder="发布状态" style="width: 130px">
                    <el-option label="已发布" :value="1" />
                    <el-option label="草稿" :value="0" />
                </el-select>
            </el-form-item>

            <!-- 搜索、重置、新增 -->
            <el-form-item>
                <el-button icon="Search" @click="searchBtn">搜索</el-button>
                <el-button type="danger" icon="Close" plain @click="resetBtn">重置</el-button>
                <el-button type="primary" icon="Plus" @click="addBtn">新增</el-button>
            </el-form-item>

        </el-form>

        <!-- 表格 -->
        <el-table v-loading="loading" :height="tableHeight" :data="tableList" border stripe>

            <el-table-column prop="title" label="标题" />

            <el-table-column prop="type" label="类型">
                <template #default="scope">
                    <!-- 根据类型显示标签 -->
                    <el-tag :type="getTagType(scope.row.type)">
                        {{ options.find((item) => item.value === scope.row.type)?.label }}
                    </el-tag>
                </template>
            </el-table-column>

            <!-- 是否推荐首页 -->
            <el-table-column prop="hasTop" label="是否推荐首页">
                <template #default="scope">
                    <!-- 根据是否推荐首页显示标签 -->
                    <el-tag :type="scope.row.hasTop === '1' ? 'success' : 'info'">
                        {{ scope.row.hasTop === "1" ? "是" : "否" }}
                    </el-tag>
                </template>
            </el-table-column>

            <!-- 发布状态 -->
            <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                    <el-tag :type="scope.row.status === 0 ? 'info' : 'success'">
                        {{ scope.row.status === 0 ? "草稿" : "已发布" }}
                    </el-tag>
                </template>
            </el-table-column>

            <!-- 浏览量 -->
            <el-table-column prop="viewCount" label="浏览量" width="90" align="center" />

            <!-- 创建时间 -->
            <el-table-column prop="createTime" label="创建时间" />

            <!-- 操作 -->
            <el-table-column align="center" width="200px" label="操作">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
                    <el-button type="danger" size="small" @click="deleteBtn(scope.row)">删除</el-button>
                </template>
            </el-table-column>

        </el-table>

        <!-- 分页 -->
        <el-pagination @size-change="sizeChange" @current-change="currentChange"
            :current-page="searchParm.currentPage" :page-sizes="[10, 20, 30, 40]"
            :page-size="searchParm.pageSize" layout="total, sizes, prev, pager, next, jumper"
            :total="searchParm.total" background />

        <!-- 新增弹窗 -->
        <AddActivity ref="addActivityRef" @onRefresh="getList" />
    </el-main>
</template>

<script setup lang="ts">
import AddActivity from "./AddActivity.vue";
import useActivityTable from "@/composable/activity/useActivityTable";
import useActivity from "@/composable/activity/useActivity";

const {
    searchParm,
    options,
    tableList,
    loading,
    getList,
    searchBtn,
    resetBtn,
    sizeChange,
    currentChange,
    tableHeight,
} = useActivityTable();
const { addBtn, editBtn, deleteBtn, addActivityRef } = useActivity(getList);

const getTagType = (type: string): "success" | "warning" | "info" => {
    switch (type) {
        case '0':
            return 'success'  // 活动 - 绿色
        case '1':
            return 'warning'  // 新闻 - 黄色
        case '2':
            return 'info'     // 公告 - 灰色
        default:
            return 'info'
    }
}
</script>

<style lang="scss" scoped>
:deep(.el-select) {
    width: 200px;
}
</style>
