<template>
  <div>
    <el-row :gutter="10" style="margin-top: 10px">
      <!-- 左侧分类区域 -->
      <el-col :span="4">
        <el-card class="box-card" :body-style="{ padding: '5px' }">
          <div
            class="text item cateitem"
            v-for="item in categoryList"
            :key="item.id"
            :class="{ active: selectedCategory === item.id }"
            @click="selectCategory(item.id)"
          >
            {{ item.name }}
          </div>
          <el-empty v-if="categoryList.length === 0" description="暂无分类" :image-size="40" />
        </el-card>
      </el-col>

      <!-- 右侧社团列表区域 -->
      <el-col :span="20">
        <el-card class="box-card" :body-style="{ padding: '15px' }">
          <el-row :gutter="16">
            <el-col
              :xs="12" :sm="8" :md="6"
              v-for="item in teamList"
              :key="item.id"
            >
              <el-card class="team-card" :body-style="{ padding: '0' }">
                <el-image
                  class="team-image"
                  :src="item.image ? 'http://localhost:8888' + item.image : defaultImg"
                  fit="cover"
                />
                <div class="team-info">
                  <div class="team-name">{{ item.name }}</div>
                  <div class="team-time">创建时间: {{ item.createTime }}</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="teamList.length === 0" description="暂无社团" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import defaultImg from "@/assets/jitashe.jpg";
import http from "@/http";

const categoryList = ref<any[]>([]);
const teamList = ref<any[]>([]);
const selectedCategory = ref<number | null>(null);

const getCategoryList = async () => {
  try {
    const res = await http.get("/api/category/getSelectList");
    if (res && res.code === 200) {
      categoryList.value = res.data || [];
    }
  } catch (e) {
    console.error("获取分类失败", e);
  }
};

const getTeamList = async () => {
  try {
    const params: any = { currentPage: 1, pageSize: 50 };
    const res = await http.get("/api/team/list", params);
    if (res && res.code === 200) {
      teamList.value = res.data.records || [];
    }
  } catch (e) {
    console.error("获取社团列表失败", e);
  }
};

const selectCategory = (id: number) => {
  selectedCategory.value = id;
  // TODO: 按分类筛选社团
};

onMounted(() => {
  getCategoryList();
  getTeamList();
});
</script>

<style scoped lang="scss">
.cateitem {
  height: 50px;
  line-height: 50px;
  border-bottom: 1px solid #e4e7ed;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;

  &:hover,
  &.active {
    color: #409eff;
    background-color: #ecf5ff;
  }
}

.team-card {
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.team-image {
  width: 100%;
  height: 150px;
  display: block;
}

.team-info {
  padding: 10px;

  .team-name {
    font-size: 14px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 5px;
  }

  .team-time {
    font-size: 12px;
    color: #909399;
  }
}
</style>
