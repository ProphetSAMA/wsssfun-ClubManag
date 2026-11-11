# 高校社团管理系统（ClubManag）

前后端分离的校园社团平台：一套 **管理后台** 给社团联合会用，一套 **用户门户** 给普通学生浏览社团 / 活动 / 新闻 / 公告。

后端放在 `Club-Web`，前端放在 `web/` 下的两个独立 Vite 应用，三个产物由 Jenkins 分别构建。

## 目录结构

```
Club-Web/                 Spring Boot 3 后端（端口 8888）
  src/main/java/fun/wsss/
    config/               异常处理、JWT、MyBatis-Plus、CORS、拦截器
    utils/                ResultVo / ResultUtils 统一返回
    web/
      user/               用户与登录
      team/               社团
      team_category/      社团分类
      news/               活动 / 新闻 / 公告（同一张表，用 type 区分）
      member/             社团成员
      club/               社团管理员视角（成员、社团活动、报名名单、签到）
      teamapply/          社团成立申请与审核
      signup/             活动报名
      notification/       站内通知
      audit/              操作审计日志
      image/              图片上传
  src/main/resources/
    application.yml       数据源、JWT、上传路径
    mapper/               自定义 SQL（目前只有 member 列表的关联查询）
web/
  club-admin/             管理后台（Vue3 + Element Plus + wangeditor，端口 8080）
  club-pc/                用户门户（Vue3 + Element Plus + swiper，端口 8081）
db/schema.sql             数据库结构（MySQL 8）
db/seed-mock-data.sql     演示数据（和假后端数据一致，可选）
tools/mock-api/           前端开发用的假后端（零依赖，见该目录 README）
Jenkinsfile               构建流水线
```

## 功能

- **用户门户**：注册与登录、首页（推荐社团 / 活动 / 公告 + 最新内容）、社团列表（分类筛选 + 申请成立社团）、社团详情（简介 + 成员 + 加入）、活动 / 新闻 / 公告列表（搜索 + 分页 + 图文详情 + 浏览量 + 名额）、活动报名与取消、顶部通知铃铛与「我的通知」、
  个人中心（头像、资料、密码、密保找回、我的社团、我报名的活动、**社团管理**）。
- **社团管理员（社长 / 社团管理员）**：在门户「个人中心 → 社团管理」里维护社团资料（简介 / 封面）、管理成员（设为管理员 / 移除）、
  发布与编辑社团活动、查看活动报名名单并核销签到。
- **管理后台**：看板（数量 + 近 7 天报名趋势 + 内容类型分布）、用户 / 分类 / 社团 / 成员 / 内容管理、社团成立申请审批、操作日志，
  内容支持草稿与发布、名额设置、批量删除与导出 CSV、报名名单，富文本编辑 + 图片上传与孤儿图片清理。

## 快速开始

### 1. 数据库

```bash
mysql -u root -p < db/schema.sql
```

建出 `club` 库和 9 张表（`sys_user`、`team`、`team_category`、`news`、`member`、`activity_signup`、`team_apply`、`notification`、`audit_log`），并写入 5 条默认分类。
管理员账号需要自己插一条（见 `db/schema.sql` 末尾的注释），`is_admin = 1` 才能登录后台。

想直接看到有内容的页面，可以再导一份演示数据（可重复执行）：

```bash
mysql -u root -p club < db/seed-mock-data.sql
```

它会写入 3 个账号（`admin` / `chenyu` / `lihao`，密码都是 `123456`，首次登录后自动升级为 BCrypt）、
8 个社团、35 条内容（活动/新闻/公告，含 7 天可翻页的量）、社团成员和 1 条报名记录。
数据与 `tools/mock-api` 的假数据一致，方便前端不连后端也能对上。

### 2. 后端

**数据库密码和 JWT 密钥不再写在仓库里**，通过环境变量注入。先准备一份本地 `.env`：

```bash
cp Club-Web/.env.example Club-Web/.env     # 然后填写 JWT_SECRET 与 DB_PASSWORD
```

`JWT_SECRET` 必须提供且不短于 32 字节，否则启动会直接失败（避免带着默认弱密钥上线）。
生成一个随机串：

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Max 256 }))
```

然后启动（脚本会读取 `.env` 再拉起 Spring Boot）：

```powershell
.\scripts\run-backend.ps1
```

用 IDE 启动的话，需要自己在运行配置里设置同样的环境变量。
上传的图片会存到 `UPLOAD_PATH`（默认 `D:/images/`），通过 `/images/**` 对外访问。

可用的环境变量：

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| `JWT_SECRET` | 签发 token 的密钥，**必填**，≥32 字节 | 无（不配启动失败） |
| `JWT_EXPIRATION` | token 有效期（毫秒） | 86400000（24 小时） |
| `DB_USERNAME` / `DB_PASSWORD` / `DB_URL` | 数据库连接 | root / 空 / 本地 club 库 |
| `CORS_ORIGINS` | 允许跨域的前端地址，逗号分隔 | `http://localhost:8080,http://localhost:8081` |
| `UPLOAD_PATH` | 图片上传目录 | `D:/images/` |
| `AUDIT_RETENTION_DAYS` | 操作日志保留天数，0 表示不清理 | 90 |

### 3. 前端

两个应用各跑各的，接口地址由 `VITE_API_BASE_URL` 决定（见各自的 `.env.development`）：

```bash
cd web/club-admin && npm install && npm run dev    # http://localhost:8080
cd web/club-pc    && npm install && npm run dev    # http://localhost:8081
```

构建：`npm run build`（会先跑 `vue-tsc` 类型检查，类型不过就不出包）。

两个前端都用 `unplugin-vue-components` + `unplugin-auto-import` 做 **Element Plus 按需引入**：
模板里用到哪个组件、代码里调到哪个 API，才打包对应代码和样式，语言包通过 `<el-config-provider>` 提供。
插件会生成 `src/auto-imports.d.ts` 和 `src/components.d.ts`（类型声明），**这两个文件要一起提交**，否则新克隆的仓库类型检查不过。

### 4. 只想看前端，不想起后端/数据库？（可选）

仓库里带了一个零依赖的假后端，接口路径和返回结构跟真后端一致：

```bash
node tools/mock-api/server.mjs     # http://localhost:8888，两个前端的 .env.development 默认就指向它
```

它还能模拟慢网络和接口故障（`MOCK_DELAY` / `MOCK_FAIL`），用来看 loading 和失败提示。详见 [tools/mock-api/README.md](tools/mock-api/README.md)。

## 接口约定

所有接口统一返回：

```json
{ "msg": "查询成功", "code": 200, "data": {} }
```

- 除登录和首页/列表类只读接口外，都要在请求头带 `token`（登录返回的 JWT）。
- 分页接口的 `data` 是 MyBatis-Plus 的 `{ records, total, size, current }`。
- 白名单见 `WebMvcConfig#addInterceptors`。

主要接口：

| 模块 | 接口 |
| --- | --- |
| 用户 | `POST /api/user/login`、`POST /api/user/register`、`POST /api/user/refresh`（续签）、`GET /api/user/info`、`PUT /api/user/profile`、`PUT /api/user/password`、`/api/user` 增改删、`GET /api/user/list` |
| 分类 | `/api/category` 增改删、`GET /api/category/list`、`GET /api/category/getSelectList` |
| 社团 | `/api/team` 增改删、`GET /api/team/list`、`GET /api/team/getTopTeam`、`GET /api/team/{id}`、`GET /api/team/{id}/members` |
| 内容 | `/api/news` 增改删、`GET /api/news/list`、`GET /api/news/detail/{id}`（浏览量 +1）、`getTopActivity`、`getTopNotice`、`getNewActivity`、`getNewNotice` |
| 成员 | `/api/member` 增改、`DELETE /api/member/{id}`（逻辑退团）、`GET /api/member/list` |
| 报名 | `POST /api/signup`、`DELETE /api/signup/{newsId}`、`GET /api/signup/my`、`GET /api/signup/count`、`GET /api/signup/list`（管理员） |
| 统计 | `GET /api/stats/overview`（仅管理员） |
| 上传 | `POST /api/upload/uploadImage` → 返回 `/images/xxx.jpg`；`POST /api/upload/avatar`（登录用户，2MB）；`DELETE /api/upload?filename=`；`POST /api/upload/cleanup`（清理孤儿图片） |
| 社团申请 | `POST /api/team-apply`、`GET /api/team-apply/my`、`GET /api/team-apply/list`（管理员）、`PUT /api/team-apply/{id}/approve`、`PUT /api/team-apply/{id}/reject` |
| 社团管理 | `GET /api/club/my`、`/{teamId}/members`、`PUT/DELETE /{teamId}/member/{id}`、`/{teamId}/news` 增改删、`/{teamId}/news/{newsId}/signups`、`.../signup/{id}/checkin` |
| 通知 | `GET /api/notification/my`、`GET /api/notification/unread`、`PUT /api/notification/{id}/read`、`PUT /api/notification/read-all` |
| 账号安全 | `PUT /api/user/avatar`、`GET/PUT /api/user/security-question`、`POST /api/user/forgot/query`、`POST /api/user/forgot/reset` |
| 审计 | `GET /api/audit/list`（仅管理员） |

## 数据约定

- `news.type`：`0` 活动 / `1` 新闻 / `2` 公告；`has_top = '1'` 表示首页推荐。
- `member.role`：`0` 普通成员 / `1` 管理员 / `2` 社长；`status = 0` 表示已退出（逻辑删除，记录保留）。
- `news.status`：`0` 草稿（仅管理员可见）/ `1` 已发布；`news.view_count` 由详情接口自增。
- `activity_signup`：同一用户对同一活动只有一行（`news_id + user_id` 唯一），取消报名把 `status` 置 0；`checked_in` 是签到标记。
- `news.team_id`：社团自建活动所属社团，平台内容为空；`news.max_participants`：活动名额，0 表示不限。
- `team_apply.status`：0 待审 / 1 通过 / 2 驳回；通过时会建社团并把申请人写成 `member.role = 2`。
- `notification`：报名、审核结果、新成员等事件的通知；`audit_log` 只记写操作，不含请求体。
- `image` 字段存相对路径（`/images/xxx.jpg`），前端用 `src/utils/image.ts` 的 `imgUrl()` 拼完整地址。
- 上传接口只信任文件头（`ImageTypeUtils`）：扩展名由 magic bytes 决定（jpg/png/gif/webp），客户端文件名和 content-type 都不参与，单张上限 5MB。
- `image` 字段历史上被写脏过（`undefined/images/xxx.png`、`http://localhost:8888/images/xxx.png`），前端 `imgUrl()` 会兜底；要彻底清干净见 `db/cleanup-image-paths.sql`（附带备份和回滚 SQL，不自动执行）。
- 密码用 BCrypt 存储（`spring-security-crypto`）；老库里遗留的明文密码在**登录成功时自动升级**为哈希，管理员新增/改密也会自动加密，登录接口不再做字符串比较。
- 成员模块的权限在服务端判定：普通用户的 `userId` 一律取自 token（忽略请求体），只能退出自己的记录、不能改角色；`GET /api/member/list` 不再允许匿名访问。见 `MemberController` 与 `MemberAuthTest`。
- 除成员模块外，`POST/PUT/DELETE` 一律要求管理员身份（`AdminInterceptor`），普通用户 token 只能读。见 `AdminWriteAuthTest`。

## 安全说明

- 密钥/密码只通过环境变量提供，`Club-Web/.env` 已被 `.gitignore` 忽略
- **如果你的仓库是公开的，历史上 `application.yml` 里出现过的数据库密码已经泄露，请务必修改 MySQL 密码**
- JWT 使用 `jti` 支持登出：`POST /api/user/logout` 会把当前 token 加入内存黑名单（单实例有效，多实例需换 Redis）
- 同一账号连续登录失败 5 次会锁定 15 分钟（`security.login.max-attempts` / `lock-minutes` 可调）
- CORS 只放行 `CORS_ORIGINS` 里的地址，且不使用 Cookie（鉴权走请求头 `token`）
- 接口限流按「IP + 分组」每分钟计数：登录/注册/找回 20 次、写操作 60 次、读 300 次，超出返回业务码 429；可用 `security.rate-limit.enabled=false` 关闭
- 操作审计：所有写请求记录方法、路径、账号、IP、状态码与耗时（不记请求体），每天 03:30 清理超过 `AUDIT_RETENTION_DAYS` 的记录
- 忘记密码走密保问题（答案 BCrypt 存储，连续答错同样触发锁定），查不到账号与未设密保返回同一句提示，避免账号枚举

## 已知问题 / 待办

- 管理接口的写操作（非 GET）由 `AdminInterceptor` 统一校验 `is_admin`；只读接口保持开放，但 `GET /api/user/list`、`/api/stats/**`、`/api/audit/list` 例外，仅管理员可用。
- 接口文档：启动后访问 `/swagger-ui/index.html`（JSON 见 `/v3/api-docs`）。
- 富文本编辑器（`@wangeditor`）810KB 的 chunk 已改成弹框打开时才加载，但单次打开仍要下载 810KB，后续可考虑换更轻的编辑器或裁剪菜单。
- 限流与 token 黑名单都是单实例内存实现，多实例部署需要换成 Redis。
- 没有邮件 / 短信通道，找回密码只支持密保问题；忘记密保仍需管理员在后台改密码。
- 活动签到目前由社团管理员手动核销，没有扫码 / 定位签到。
- 前端没有单元测试和 E2E，验证依赖脚本化浏览器测试（未入库）。

## 构建

根目录 `Jenkinsfile`：Maven 打 `Club-Web` 的 jar，两个前端并行 `npm install && npm run build`，产物 archive，并发送成功/失败邮件。
