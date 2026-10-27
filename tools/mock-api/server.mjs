#!/usr/bin/env node
/**
 * 前端开发用的假后端（零依赖，只需要 Node 18+）
 *
 * 用途：不启动后端、不连 MySQL 的情况下也能把两个前端跑起来看页面。
 * 接口路径、返回结构和真实后端一致（ResultVo: { msg, code, data }）。
 *
 * 用法：
 *   node tools/mock-api/server.mjs                 # 监听 8888
 *   PORT=9000 node tools/mock-api/server.mjs       # 换端口
 *   MOCK_DELAY=1200 node tools/mock-api/server.mjs # 每个请求延迟 1.2s，用来看 loading 状态
 *   MOCK_FAIL=/api/news node tools/mock-api/server.mjs  # 让匹配到的接口返回 code 500，用来看失败态
 *
 * 数据是内存里的假数据：加入/退出社团会改内存状态，重启即还原。
 */

import http from 'node:http';

const PORT = Number(process.env.PORT || 8888);
const DELAY = Number(process.env.MOCK_DELAY || 0);
const FAIL_PATH = process.env.MOCK_FAIL || '';

// ---------------------------------------------------------------- 假数据

const categories = [
  { id: 1, name: '体育竞技', orderNum: 1 },
  { id: 2, name: '文艺爱好', orderNum: 2 },
  { id: 3, name: '学术科技', orderNum: 3 },
  { id: 4, name: '公益实践', orderNum: 4 },
  { id: 5, name: '语言文化', orderNum: 5 },
];

const teams = [
  ['吉他社', '以琴会友，用音乐点亮校园生活。每周三晚在艺术楼排练，欢迎零基础加入。', 2],
  ['羽毛球社', '挥洒汗水，切磋球技。校队选拔与日常约球同步进行。', 1],
  ['街舞社', 'Breaking / Hiphop / Jazz 三个厂牌，校内演出常客。', 2],
  ['播音主持社', '声音的力量。承担校级大型活动主持与校广播站节目。', 2],
  ['摄影社', '记录光影与瞬间，器材共享，外拍活动每月一次。', 3],
  ['青年志愿者协会', '支教、社区服务、赛事志愿，累计服务时长可认证。', 4],
  ['日语社', '五十音到 N3，一起追番一起学。', 5],
  ['篮球社', '院系联赛主力，训练与友谊赛常年不断。', 1],
].map(([name, details, categoryId], i) => ({
  id: i + 1,
  categoryId,
  name,
  image: '/images/team' + ((i % 8) + 1) + '.svg',
  details,
  createTime: '2025-0' + ((i % 8) + 1) + '-1' + (i % 9),
  hasTop: i < 4 ? '1' : '0',
}));

const detailText = (title) =>
  '<p><strong>' + title + '</strong> 的详细内容。</p>' +
  '<p>时间：本周六 19:00，地点：大学生活动中心报告厅。</p>' +
  '<ul><li>请提前 15 分钟入场</li><li>现场提供饮用水</li></ul>';

// news.type: 0-活动 1-新闻 2-公告
const news = [
  { id: 1, title: '2026 迎新晚会节目征集开始', type: '1', image: '/images/team1.svg', createTime: '2026-06-20', hasTop: '1', details: detailText('迎新晚会') },
  { id: 2, title: '校园歌手大赛决赛名单公布', type: '1', image: '/images/team2.svg', createTime: '2026-06-18', hasTop: '0', details: detailText('歌手大赛') },
  { id: 3, title: '社团文化节：40 个社团齐聚中心广场', type: '1', image: '/images/team3.svg', createTime: '2026-06-12', hasTop: '1', details: detailText('社团文化节') },
  { id: 4, title: '春季篮球联赛开幕', type: '0', image: '/images/team4.svg', createTime: '2026-06-10', hasTop: '1', details: detailText('春季篮球联赛') },
  { id: 5, title: '吉他社专场音乐会', type: '0', image: '/images/team5.svg', createTime: '2026-06-08', hasTop: '0', details: detailText('吉他社音乐会') },
  { id: 6, title: '摄影社外拍：老街印象', type: '0', image: '/images/team6.svg', createTime: '2026-06-05', hasTop: '0', details: detailText('摄影社外拍') },
  { id: 7, title: '关于社团年审工作的通知', type: '2', image: '', createTime: '2026-06-15', hasTop: '1', details: detailText('社团年审') },
  { id: 8, title: '暑期社团活动场地开放安排', type: '2', image: '', createTime: '2026-06-11', hasTop: '0', details: detailText('场地开放安排') },
];

// BULK: 再造 27 条，让每个类型的条数都超过一页（pageSize=10）
for (let i = 0; i < 27; i++) {
  news.push({
    id: 100 + i,
    title: ['示例活动', '示例新闻', '示例公告'][i % 3] + ' ' + (i + 1),
    type: String(i % 3),
    image: i % 4 === 0 ? '' : '/images/team' + ((i % 8) + 1) + '.svg',
    createTime: '2026-05-' + String((i % 28) + 1).padStart(2, '0'),
    hasTop: '0',
    details: detailText('示例内容 ' + (i + 1)),
  });
}

// 内容状态与浏览量（真实后端有这两列，这里补默认值）
news.forEach((n) => {
  n.status ??= 1;
  n.viewCount ??= 0;
});

const users = [
  { userId: 1, username: 'admin', nickname: '系统管理员', phone: '13800000000', sex: 0, status: 1, isAdmin: 1 },
  { userId: 12, username: 'chenyu', nickname: '陈雨', phone: '13811112222', sex: 1, status: 1, isAdmin: 0 },
  { userId: 15, username: 'lihao', nickname: '李昊', phone: '13822223333', sex: 0, status: 1, isAdmin: 0 },
];

/** 活动报名（内存）：{ newsId, userId, status } */
const signups = [];

/** 当前登录用户（改资料会改它，重启重置；密码初始为 123456） */
const currentUser = { ...users[0], password: '123456' };

/** 成员关系：加入/退出会改它，重启重置 */
let nextMemberId = 1;
const memberships = [];

// ---------------------------------------------------------------- 工具

const gradients = ['6366f1', '0ea5e9', 'f59e0b', '10b981', 'ef4444', '8b5cf6', '14b8a6', 'ec4899'];

/** 生成占位封面图，免得依赖外网 or 本地图片 */
function coverSvg(name, index) {
  const color = gradients[index % gradients.length];
  return '<svg xmlns="http://www.w3.org/2000/svg" width="640" height="400">' +
    '<defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">' +
    '<stop offset="0" stop-color="#' + color + '"/><stop offset="1" stop-color="#0f172a"/></linearGradient></defs>' +
    '<rect width="640" height="400" fill="url(#g)"/>' +
    '<circle cx="540" cy="70" r="120" fill="#ffffff" opacity="0.10"/>' +
    '<text x="40" y="215" font-family="Microsoft YaHei, sans-serif" font-size="46" fill="#ffffff" font-weight="700">' + name + '</text>' +
    '</svg>';
}

function send(res, body, type = 'application/json;charset=UTF-8') {
  res.writeHead(200, {
    'Content-Type': type,
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': '*',
    'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
  });
  res.end(body);
}

const ok = (res, data, msg = '查询成功') => send(res, JSON.stringify({ msg, code: 200, data }));

/** 读请求体 JSON 后交给回调处理 */
function readJson(req, res, fn) {
  let raw = '';
  req.on('data', (chunk) => { raw += chunk; });
  req.on('end', () => {
    let body = {};
    try {
      body = JSON.parse(raw || '{}');
    } catch (e) {
      return fail(res, '参数错误');
    }
    fn(body);
  });
}
const fail = (res, msg) => send(res, JSON.stringify({ msg, code: 500, data: null }));

/** MyBatis-Plus 风格的分页结构 */
function paged(list, url, defaultSize = 10) {
  const current = Number(url.searchParams.get('currentPage') || 1);
  const size = Number(url.searchParams.get('pageSize') || defaultSize);
  return { records: list.slice((current - 1) * size, current * size), total: list.length };
}

/** 后台列表用：造 24 行，方便测分页 */
function fakeRows(page, size, prefix, build) {
  const all = Array.from({ length: 24 }, (_, i) => build(i, page, size));
  return { records: all.slice((page - 1) * size, page * size), total: all.length };
}

// ---------------------------------------------------------------- 路由

function handle(req, res, url, path) {
  if (FAIL_PATH && path.startsWith(FAIL_PATH)) {
    return fail(res, 'mock 故障注入: ' + path);
  }

  const cover = path.match(/^\/images\/team(\d+)\.svg$/);
  if (cover) {
    return send(res, coverSvg(teams[Number(cover[1]) - 1].name, Number(cover[1])), 'image/svg+xml');
  }

  // 用户
  if (path === '/api/user/login' && req.method === 'POST') {
    return ok(res, { token: 'mock-token', userId: 1, username: 'admin', nickname: '系统管理员', isAdmin: 1 }, '登录成功');
  }
  if (path === '/api/user/info') {
    const { password, ...safe } = currentUser;
    return ok(res, safe);
  }
  if (path === '/api/user/register' && req.method === 'POST') {
    return readJson(req, res, (body) => {
      const username = String(body.username || '').trim();
      if (!/^[A-Za-z0-9_]{3,20}$/.test(username)) return fail(res, '用户名只能包含字母、数字和下划线，长度 3-20');
      if (!body.password || body.password.length < 6) return fail(res, '密码至少 6 位');
      if (users.some((u) => u.username === username)) return fail(res, '用户名已存在');
      users.push({
        userId: users.length + 1,
        username,
        nickname: body.nickname || username,
        phone: '',
        sex: 0,
        status: 1,
        isAdmin: 0,
      });
      return ok(res, null, '注册成功');
    });
  }
  if (path === '/api/user/profile' && req.method === 'PUT') {
    return readJson(req, res, (body) => {
      if (!body.nickname) return fail(res, '昵称不能为空');
      currentUser.nickname = body.nickname;
      currentUser.phone = body.phone;
      currentUser.sex = body.sex;
      return ok(res, null, '保存成功');
    });
  }
  if (path === '/api/user/password' && req.method === 'PUT') {
    return readJson(req, res, (body) => {
      if (!body.oldPassword || !body.newPassword) return fail(res, '原密码和新密码不能为空');
      if (body.newPassword.length < 6) return fail(res, '新密码至少 6 位');
      if (body.oldPassword !== currentUser.password) return fail(res, '原密码不正确');
      currentUser.password = body.newPassword;
      return ok(res, null, '密码修改成功');
    });
  }
  if (path === '/api/user/list') {
    const page = Number(url.searchParams.get('currentPage') || 1);
    const size = Number(url.searchParams.get('pageSize') || 10);
    const username = (url.searchParams.get('username') || '').trim();
    const nickname = (url.searchParams.get('nickName') || '').trim();
    const phone = (url.searchParams.get('phone') || '').trim();
    const isAdmin = url.searchParams.get('isAdmin');

    let rows = Array.from({ length: 24 }, (_, i) => ({
      userId: i + 1,
      username: 'u' + (i + 1),
      nickname: '用户' + (i + 1),
      phone: '1380000' + String(1000 + i),
      sex: 0,
      status: 1,
      isAdmin: i === 0 ? 1 : 0,
    }));
    if (username) rows = rows.filter((r) => r.username.includes(username));
    if (nickname) rows = rows.filter((r) => r.nickname.includes(nickname));
    if (phone) rows = rows.filter((r) => r.phone.includes(phone));
    if (isAdmin !== null && isAdmin !== '') rows = rows.filter((r) => String(r.isAdmin) === isAdmin);

    return ok(res, { records: rows.slice((page - 1) * size, page * size), total: rows.length });
  }

  // 分类
  if (path === '/api/category/list') return ok(res, { records: categories, total: categories.length });
  if (path === '/api/category/getSelectList') {
    return ok(res, categories.map((c) => ({ label: c.name, value: c.id })));
  }

  // 社团
  const membersMatch = path.match(/^\/api\/team\/(\d+)\/members$/);
  if (membersMatch) {
    const teamId = Number(membersMatch[1]);
    const names = ['陈雨', '李昊', '王思妍', '张一鸣', '林小满'];
    return ok(res, names.map((name, i) => ({
      userName: name,
      role: i === 0 ? 2 : (i === 1 ? 1 : 0),
      joinTime: '2025-0' + ((i % 8) + 1) + '-1' + i,
    })).slice(0, 3 + (teamId % 3)));
  }
  const teamDetail = path.match(/^\/api\/team\/(\d+)$/);
  if (teamDetail) {
    const team = teams.find((t) => t.id === Number(teamDetail[1]));
    return team ? ok(res, team) : fail(res, '社团不存在');
  }
  if (path === '/api/team/list') return ok(res, paged(teams, url));

  // 看板统计
  if (path === '/api/stats/overview') {
    const byType = ['0', '1', '2'].map((type) => ({
      label: { '0': '活动', '1': '新闻', '2': '公告' }[type],
      count: news.filter((n) => n.type === type).length,
    }));
    const trend = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date(Date.now() - i * 86400000);
      const key = String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
      trend.push({ date: key, count: (i * 3) % 5 });
    }
    return ok(res, {
      userCount: users.length,
      teamCount: teams.length,
      categoryCount: categories.length,
      newsCount: news.length,
      signupCount: signups.filter((x) => x.status === 1).length,
      newsByType: byType,
      teamByCategory: categories.map((c) => ({ label: c.name, count: teams.filter((t) => t.categoryId === c.id).length })),
      signupTrend: trend,
    });
  }
  if (path === '/api/team/getTopTeam') return ok(res, teams.filter((t) => t.hasTop === '1'));

  // 活动 / 新闻 / 公告
  const detailMatch = path.match(/^\/api\/news\/detail\/(\d+)$/);
  if (detailMatch) {
    const newsId = Number(detailMatch[1]);
    const item = news.find((n) => n.id === newsId);
    if (!item) return fail(res, '内容不存在');
    item.viewCount = (item.viewCount || 0) + 1;
    return ok(res, item);
  }
  if (path === '/api/news/list') {
    const type = url.searchParams.get('type');
    const title = (url.searchParams.get('title') || '').trim();
    let rows = type ? news.filter((n) => n.type === type) : news;
    if (title) rows = rows.filter((n) => n.title.includes(title));
    return ok(res, paged(rows, url));
  }
  const topMap = {
    '/api/news/getTopActivity': (n) => n.type === '0' && n.hasTop === '1',
    '/api/news/getTopNotice': (n) => n.type === '2' && n.hasTop === '1',
    '/api/news/getNewActivity': (n) => n.type === '0',
    '/api/news/getNewNotice': (n) => n.type === '1',
  };
  if (topMap[path]) return ok(res, news.filter(topMap[path]).slice(0, 8));

  // 活动报名
  if (path === '/api/signup' && req.method === 'POST') {
    return readJson(req, res, (body) => {
      const newsId = Number(body.newsId);
      if (!newsId) return fail(res, '请选择要报名的活动');
      const exist = signups.find((x) => x.newsId === newsId && x.userId === currentUser.userId);
      if (exist && exist.status === 1) return fail(res, '已经报过名了');
      if (exist) {
        exist.status = 1;
        exist.signupTime = '2026-10-27 15:00';
        return ok(res, null, '报名成功');
      }
      signups.push({ newsId, userId: currentUser.userId, status: 1, signupTime: '2026-10-27 15:00' });
      return ok(res, null, '报名成功');
    });
  }
  const cancelMatch = path.match(/^\/api\/signup\/(\d+)$/);
  if (cancelMatch && req.method === 'DELETE') {
    const newsId = Number(cancelMatch[1]);
    const exist = signups.find((x) => x.newsId === newsId && x.userId === currentUser.userId && x.status === 1);
    if (!exist) return fail(res, '还没有报名这个活动');
    exist.status = 0;
    return ok(res, null, '已取消报名');
  }
  if (path === '/api/signup/my') {
    const rows = signups
      .filter((x) => x.userId === currentUser.userId && x.status === 1)
      .map((x) => {
        const n = news.find((item) => item.id === x.newsId) || {};
        return {
          id: x.newsId,
          newsId: x.newsId,
          title: n.title,
          image: n.image,
          activityTime: n.createTime,
          signupTime: x.signupTime,
          status: x.status,
        };
      });
    return ok(res, { records: rows, total: rows.length });
  }
  if (path === '/api/signup/count') {
    const newsId = Number(url.searchParams.get('newsId'));
    return ok(res, signups.filter((x) => x.newsId === newsId && x.status === 1).length);
  }

  // 成员
  if (path === '/api/member/list') {
    const userId = url.searchParams.get('userId');
    if (userId) {
      const rows = memberships.filter((m) => m.userId === Number(userId) && m.status === 1);
      return ok(res, paged(rows, url, 50));
    }
    const page = Number(url.searchParams.get('currentPage') || 1);
    const size = Number(url.searchParams.get('pageSize') || 10);
    return ok(res, fakeRows(page, size, '成员', (i, p) => ({
      id: 1000 + i,
      teamId: teams[i % teams.length].id,
      teamName: teams[i % teams.length].name,
      userId: i + 1,
      userName: '成员-P' + p + '-' + (i + 1),
      role: i % 3,
      status: 1,
      joinTime: '2025-09-12',
    })));
  }
  if (path === '/api/member' && req.method === 'POST') {
    let raw = '';
    req.on('data', (chunk) => { raw += chunk; });
    req.on('end', () => {
      let body = {};
      try {
        body = JSON.parse(raw || '{}');
      } catch (e) {
        return fail(res, '参数错误');
      }
      const duplicated = memberships.some(
        (m) => m.teamId === body.teamId && m.userId === body.userId && m.status === 1
      );
      if (duplicated) return fail(res, '该用户已是社团成员');
      const team = teams.find((t) => t.id === body.teamId);
      memberships.push({
        id: nextMemberId++,
        teamId: body.teamId,
        userId: body.userId,
        teamName: team ? team.name : '社团 #' + body.teamId,
        userName: users[0].nickname,
        role: 0,
        status: 1,
        joinTime: new Date().toISOString().slice(0, 10),
      });
      return ok(res, null, '加入成功');
    });
    return;
  }
  const quit = path.match(/^\/api\/member\/(\d+)$/);
  if (quit && req.method === 'DELETE') {
    const row = memberships.find((m) => m.id === Number(quit[1]));
    if (!row) return fail(res, '成员不存在');
    row.status = 0;
    return ok(res, null, '退出成功');
  }

  if (path === '/api/health') return ok(res, { status: 'up' });

  return send(res, JSON.stringify({ msg: 'mock 未实现: ' + req.method + ' ' + path, code: 404, data: null }));
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');
  if (req.method === 'OPTIONS') return send(res, '', 'text/plain');
  if (DELAY > 0) {
    setTimeout(() => handle(req, res, url, url.pathname), DELAY);
    return;
  }
  handle(req, res, url, url.pathname);
});

server.listen(PORT, () => {
  console.log('mock api listening on http://localhost:' + PORT);
  if (DELAY) console.log('  延迟: ' + DELAY + 'ms');
  if (FAIL_PATH) console.log('  故障注入: ' + FAIL_PATH);
});
