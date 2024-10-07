<template>
    <div class="logo">
        <img :src="logo" />
        <div class="title">商丘学院社团信息化平台</div>
    </div>
</template>

<script setup lang="ts">
import logo from '@/assets/logo.png'
</script>

<style scoped lang="scss">
.logo {
    display: flex;
    align-items: center;
    padding-left: 40px;
    margin-top: 10px;
    img {
        width:40px;
        height:40px;
    }
    .title {
        font-size: 20px;
        color: #fff;
        font-weight: 600;
        font-family: 'Microsoft YaHei';
        margin-left: 6px;
    }
}
</style>