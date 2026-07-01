<template>
  <div class="home-page">
    <!-- 顶部 banner -->
    <section class="banner">
      <div class="banner-text">
        <h1>发现你的社团</h1>
        <p>探索校园社团生活，找到属于你的精彩</p>
        <el-button @click="$router.push('/club')">浏览社团</el-button>
      </div>
    </section>

    <!-- 推荐社团 -->
    <section class="section">
      <div class="section-head">
        <h2>推荐社团</h2>
        <span class="more" @click="$router.push('/club')">更多</span>
      </div>
      <swiper />
    </section>

    <!-- 推荐活动 + 公告 -->
    <section class="row-2">
      <div class="col">
        <div class="section-head">
          <h2>推荐活动</h2>
          <span class="more" @click="$router.push('/activity')">更多</span>
        </div>
        <div class="card">
          <swiperActivity />
        </div>
      </div>
      <div class="col">
        <div class="section-head">
          <h2>推荐公告</h2>
          <span class="more" @click="$router.push('/notice')">更多</span>
        </div>
        <div class="card">
          <div class="list-item" v-for="(item, i) in noticeList" :key="item.id">
            <span class="idx" :class="{ hot: i < 3 }">{{ i + 1 }}</span>
            <span class="title">{{ item.title }}</span>
            <span class="date">{{ item.createTime }}</span>
          </div>
          <el-empty v-if="noticeList.length === 0" description="暂无公告" :image-size="60" />
        </div>
      </div>
    </section>

    <!-- 最新活动 -->
    <section class="section">
      <div class="section-head">
        <h2>最新活动</h2>
        <span class="more" @click="$router.push('/activity')">更多</span>
      </div>
      <div class="card">
        <div class="list-item" v-for="(item, i) in activityList" :key="item.id">
          <span class="idx" :class="{ hot: i < 3 }">{{ i + 1 }}</span>
          <span class="title">{{ item.title }}</span>
          <span class="date">{{ item.createTime }}</span>
        </div>
        <el-empty v-if="activityList.length === 0" description="暂无活动" :image-size="60" />
      </div>
    </section>

    <!-- 最新新闻 -->
    <section class="section">
      <div class="section-head">
        <h2>最新新闻</h2>
        <span class="more" @click="$router.push('/news')">更多</span>
      </div>
      <div class="card">
        <div class="news-row">
          <div class="news-card" v-for="item in newsList" :key="item.id">
            <el-image
              v-if="item.image"
              class="news-img"
              :src="imgUrl(item.image)"
              fit="cover"
            />
            <div class="news-img placeholder" v-else></div>
            <div class="news-body">
              <h3>{{ item.title }}</h3>
              <span class="date">{{ item.createTime }}</span>
            </div>
          </div>
          <el-empty v-if="newsList.length === 0" description="暂无新闻" :image-size="60" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import swiper from "./swiper.vue";
import swiperActivity from "./swiperActivity.vue";
import http from "@/http";

const BASE = "http://localhost:8888";

/** 从可能包含多图、脏前缀的 image 字段中取第一张有效 URL */
const imgUrl = (image: string) => {
  if (!image) return "";
  let url = image.split(",")[0].trim();
  // 兼容旧数据: "undefined/images/xxx.jpg" → "/images/xxx.jpg"
  if (url.startsWith("undefined")) url = url.replace("undefined", "");
  // 相对路径补全
  if (url.startsWith("/")) url = BASE + url;
  return url;
};

const noticeList = ref<any[]>([]);
const activityList = ref<any[]>([]);
const newsList = ref<any[]>([]);

onMounted(async () => {
  try {
    const [n, a, ne] = await Promise.all([
      http.get("/api/news/getTopNotice"),
      http.get("/api/news/getNewActivity"),
      http.get("/api/news/getNewNotice"),
    ]);
    if (n?.code === 200) noticeList.value = n.data || [];
    if (a?.code === 200) activityList.value = a.data || [];
    if (ne?.code === 200) newsList.value = ne.data || [];
  } catch (e) {
    console.error(e);
  }
});
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* banner */
.banner {
  background: #2c3e6b;
  border-radius: var(--radius-lg);
  padding: 48px 40px;
  color: #fff;

  h1 {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 8px;
  }

  p {
    font-size: 15px;
    opacity: 0.7;
    margin-bottom: 24px;
  }

  .el-button {
    background: rgba(255, 255, 255, 0.15) !important;
    border: 1px solid rgba(255, 255, 255, 0.25) !important;
    color: #fff !important;
    border-radius: 6px !important;
    padding: 8px 24px !important;

    &:hover {
      background: rgba(255, 255, 255, 0.25) !important;
    }
  }
}

/* section */
.section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h2 {
    font-size: 17px;
    font-weight: 600;
    color: var(--text-1);
  }

  .more {
    font-size: 13px;
    color: var(--text-3);
    cursor: pointer;

    &:hover {
      color: var(--primary);
    }
  }
}

/* card */
.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 0;
  overflow: hidden;
}

/* list */
.list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.15s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: var(--bg-hover);
  }
}

.idx {
  width: 22px;
  height: 22px;
  border-radius: 4px;
  background: var(--bg);
  color: var(--text-3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;

  &.hot {
    background: #e74c3c;
    color: #fff;
  }
}

.title {
  flex: 1;
  font-size: 13px;
  color: var(--text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.date {
  font-size: 12px;
  color: var(--text-3);
  flex-shrink: 0;
}

/* row-2 */
.row-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: stretch;

  .col {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .card {
    flex: 1;
  }
}

/* news */
.news-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px;
  justify-content: center;
}

.news-card {
  background: var(--bg);
  border-radius: var(--radius);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
  flex: 0 0 calc(25% - 12px);
  max-width: calc(25% - 12px);

  &:hover {
    box-shadow: var(--shadow-md);
  }
}

.news-img {
  width: 100%;
  height: 130px;

  &.placeholder {
    background: var(--bg);
  }
}

.news-body {
  padding: 12px;

  h3 {
    font-size: 13px;
    font-weight: 600;
    color: var(--text-1);
    margin-bottom: 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

@media (max-width: 768px) {
  .row-2 {
    grid-template-columns: 1fr;
  }
  .news-card {
    flex: 0 0 calc(50% - 8px);
    max-width: calc(50% - 8px);
  }
}
</style>
