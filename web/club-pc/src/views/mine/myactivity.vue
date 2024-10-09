<template>
  <el-row style="margin-top: 10px" :gutter="10">
    <el-col :span="20">
      <div class="grid-content ep-bg-purple-light"></div>
      <el-card class="box-card">
        <div class="text item">
          <el-row>
            <el-col
              :xs="12"
              :sm="8"
              :md="7"
              v-for="item in tableData"
              :key="item.teamId"
              :offset="1"
            >
              <div style="margin-top: 15px">
                <el-card :body-style="{ padding: '5px' }" shadow="hover">
                  <img :src="item.images" class="image" />
                  <div>
                    <store style="font-size: 12px">
                      {{ item.classGrade }} 级 - {{ item.classMajor }}系 -
                      {{ item.classNumber }}班
                    </store>
                    <br />
                    <div class="bottom clearfix">
                      <time class="time">
                        <span>创建时间:</span>
                        {{ item.classCreateTime }}
                      </time>
                      <el-button class="button"> 查看 </el-button>
                    </div>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </el-col>
    <el-pagination
      style="margin-top: 15px"
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page.sync="parm.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]"
      :page-size="parm.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="parm.total"
      background
    >
    </el-pagination>
  </el-row>
</template>

<script setup lang="ts">
import { ref } from "vue";

// 引入图片资源，用于社团的图片展示
import img1 from "@/assets/jitashe.jpg";
import img2 from "@/assets/boyinshe.jpg";
import img3 from "@/assets/yumaoqiushe.jpg";
import img4 from "@/assets/4.jpg";
import img5 from "@/assets/5.jpg";
import img6 from "@/assets/6.jpg";

// 定义分类数据，存储在响应式变量category中
const category = ref([
  {
    name: "书画协会", // 分类名称
    id: 1, // 分类唯一ID
  },
  {
    name: "文体协会",
    id: 1,
  },
  {
    name: "学术协会",
    id: 1,
  },
  {
    name: "体育协会",
    id: 1,
  },
]);
const tableData = ref([
  {
    teamId: "1", // 社团唯一ID
    classGrade: "2022", // 社团年级
    classMajor: "计算机工程", // 社团专业
    images: img1, // 社团图片
    classNumber: "2207", // 社团班级
    classCreateTime: "2022-09-01", // 社团创建时间
  },
  {
    teamId: "2",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img2,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "3",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img3,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "4",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img4,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "5",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img5,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
  {
    teamId: "6",
    classGrade: "2022",
    classMajor: "计算机工程",
    images: img6,
    classNumber: "2207",
    classCreateTime: "2022-09-01",
  },
]);
const parm = ref({
  currentPage: 1, // 当前页数
  pageSize: 10, // 每页显示条数
  total: 10, // 总条数
});
const sizeChange = (val: number) => {};
const currentChange = (val: number) => {};
</script>

<style scoped lang="scss">
.time{
    font-size: 10px;
    color: #999;
}
.cateitem{
    height: 50px;
    line-height: 50px;
    border-bottom: 1px solid #e4e7ed;
    text-align: center;
    cursor: pointer;
}
.bottom{
    margin-top: 5px;
    line-height: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.image{
    width: 100%;
    display: block;
}
</style>
