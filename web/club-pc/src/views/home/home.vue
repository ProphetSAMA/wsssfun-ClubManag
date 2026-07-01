<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-content">
        <h1>发现你的社团</h1>
        <p>探索丰富多彩的校园社团生活，找到属于你的精彩</p>
        <el-button class="hero-btn" size="large" @click="$router.push('/club')">
          浏览社团 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>
    </section>

    <!-- 推荐社团 -->
    <section class="section">
      <div class="section-header">
        <h2>⭐ 推荐社团</h2>
        <el-button text class="more-btn" @click="$router.push('/club')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <swiper />
    </section>

    <!-- 双栏：推荐活动 + 推荐公告 -->
    <section class="section">
      <div class="dual-grid">
        <div class="grid-left">
          <div class="section-header">
            <h2>🔥 推荐活动</h2>
            <el-button text class="more-btn" @click="$router.push('/activity')">
              更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="card-body">
            <swiperActivity />
          </div>
        </div>
        <div class="grid-right">
          <div class="section-header">
            <h2>📢 推荐公告</h2>
            <el-button text class="more-btn" @click="$router.push('/notice')">
              更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="card-body list-body">
            <div
              class="list-item"
              v-for="(item, index) in noticeList"
              :key="item.id"
            >
              <span class="list-index" :class="{ hot: index < 3 }">{{ index + 1 }}</span>
              <span class="list-title">{{ item.title }}</span>
              <span class="list-date">{{ item.createTime }}</span>
            </div>
            <el-empty v-if="noticeList.length === 0" description="暂无公告" :image-size="60" />
          </div>
        </div>
      </div>
    </section>

    <!-- 最新活动 -->
    <section class="section">
      <div class="section-header">
        <h2>📅 最新活动</h2>
        <el-button text class="more-btn" @click="$router.push('/activity')">
          更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="card-body list-body">
        <div
          class="list-item"
          v-for="(item, index) in activityList"
          :key="item.id"
        >
          <span class="list-index" :class="{ hot: index < 3 }">{{ index + 1 }}</span>
          <span class="list-title">{{ item.title }}</span>
          <span class="list-date">{{ item.createTime }}</span>
        </div>
        <el-empty v-if="activityList.length === 0" description="暂无活动" :image-size="60" />
      </div>
    </section>

    <!-- 最新新闻 -->
    <section class="section">
      <div class="section-header">
        <h2>📰 最新新闻</h2>
        <el-button text class="more-btn" @click="$router.push('/news')">
          更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="news-grid">
        <div class="news-card" v-for="item in newsList" :key="item.id">
          <el-image
            v-if="item.image"
            class="news-img"
            :src="'http://localhost:8888' + item.image"
            fit="cover"
          />
          <div class="news-img placeholder" v-else>📰</div>
          <div class="news-body">
            <h3>{{ item.title }}</h3>
            <span class="news-date">{{ item.createTime }}</span>
          </div>
        </div>
        <el-empty v-if="newsList.length === 0" description="暂无新闻" :image-size="60" />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import swiper from "./swiper.vue";
import swiperActivity from "./swiperActivity.vue";
import http from "@/http";

const noticeList = ref<any[]>([]);
const activityList = ref<any[]>([]);
const newsList = ref<any[]>([]);

onMounted(async () => {
  try {
    const [noticeRes, activityRes, newsRes] = await Promise.all([
      http.get("/api/news/getTopNotice"),
      http.get("/api/news/getNewActivity"),
      http.get("/api/news/getNewNotice"),
    ]);
    if (noticeRes?.code === 200) noticeList.value = noticeRes.data || [];
    if (activityRes?.code === 200) activityList.value = activityRes.data || [];
    if (newsRes?.code === 200) newsList.value = newsRes.data || [];
  } catch (e) {
    console.error("获取首页数据失败", e);
  }
});
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
}

/* Hero */
.hero {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a78bfa 100%);
  border-radius: var(--radius-xl);
  padding: 60px 48px;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    right: -40px;
    top: -40px;
    width: 300px;
    height: 300px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
  }

  .hero-content {
    position: relative;
    z-index: 1;
  }

  h1 {
    font-size: 36px;
    font-weight: 800;
    margin-bottom: 12px;
  }

  p {
    font-size: 17px;
    opacity: 0.85;
    margin-bottom: 28px;
  }
}

.hero-btn {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px !important;
  background: rgba(255, 255, 255, 0.2) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #fff !important;
  backdrop-filter: blur(10px);

  &:hover {
    background: rgba(255, 255, 255, 0.3) !important;
  }
}

/* Section */
.section {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h2 {
    font-size: 20px;
    font-weight: 700;
    color: var(--text-primary);
  }
}

.more-btn {
  color: var(--text-secondary) !important;
  font-weight: 500;

  &:hover {
    color: var(--primary) !important;
  }
}

/* Card body */
.card-body {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  padding: var(--space-lg);
  box-shadow: var(--shadow-sm);
}

/* Dual grid */
.dual-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-lg);

  .grid-left,
  .grid-right {
    display: flex;
    flex-direction: column;
    gap: var(--space-md);
  }
}

/* List */
.list-body {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: var(--space-md) var(--space-lg);
}

.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-light);
  transition: background 0.2s;
  cursor: pointer;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: var(--bg-hover);
    margin: 0 calc(-1 * var(--space-lg));
    padding-left: var(--space-lg);
    padding-right: var(--space-lg);
  }
}

.list-index {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: var(--bg-hover);
  color: var(--text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;

  &.hot {
    background: linear-gradient(135deg, #f59e0b, #ef4444);
    color: #fff;
  }
}

.list-title {
  flex: 1;
  font-size: 14px;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.list-date {
  font-size: 13px;
  color: var(--text-muted);
  flex-shrink: 0;
}

/* News grid */
.news-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

.news-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }
}

.news-img {
  width: 100%;
  height: 140px;
  object-fit: cover;

  &.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--primary-bg);
    font-size: 36px;
  }
}

.news-body {
  padding: 14px;

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.news-date {
  font-size: 12px;
  color: var(--text-muted);
}

/* Responsive */
@media (max-width: 768px) {
  .dual-grid {
    grid-template-columns: 1fr;
  }
  .news-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .hero {
    padding: 40px 24px;
    h1 { font-size: 28px; }
  }
}
</style>
