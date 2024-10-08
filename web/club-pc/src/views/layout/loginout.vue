<template>
    <el-dropdown style="padding: 0px 20px;cursor: pointer;">
    <span class="el-dropdown-link">
      <img class="userimg" src="@/assets/user.jpg"/>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item>修改密码</el-dropdown-item>
        <el-dropdown-item>退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">

</script>

<style scoped lang="scss">
.userimg {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>