# 高校社团管理系统（ClubManag）

前后端分离的校园社团平台，为「社团联合会 + 社团 + 学生」三方提供一套完整的线上协作流程：

- **管理后台**：社团联合会用于管理用户、分类、社团、成员、内容，审批社团成立申请，查看看板与操作日志；
- **用户门户**：学生浏览社团与活动、报名、加入社团、查看通知，社长在这里管理自己的社团；
- **后端 API**：为两套前端统一提供数据和权限校验。

项目采用 MIT 许可（Copyright © 2024 Wsssfun）。

## 项目简介

- **后端** `Club-Web`：Spring Boot 3.3.4 + Java 17 + MyBatis-Plus 3.5.7 + MySQL 8 + JJWT，运行在 `8888` 端口；
- **管理后台** `web/club-admin`：Vue 3 + TypeScript + Element Plus + Pinia + wangeditor 富文本，运行在 `8080` 端口；
- **用户门户** `web/club-pc`：Vue 3 + TypeScript + Element Plus + Pinia + Swiper，运行在 `8081` 端口。

三个单元相互独立，由 Jenkinsfile 分别构建。

## 目录结构

```
Club-Web/                    后端（端口 8888）
  src/main/java/fun/wsss/
    config/                  异常处理、JWT、MyBatis-Plus、CORS、拦截器
    utils/                   ResultVo / ResultUtils 统一返回
    web/
      user/                  用户、登录、密保、头像
      team/                  社团
      team_category/         社团分类
      news/                  活动 / 新闻 / 公告（同一张表，用 type 区分）
      member/                社团成员
      club/                  社团管理员视角（成员、活动、报名名单、签到）
      teamapply/             社团成立申请与审核
      signup/                活动报名
      notification/          站内通知
      audit/                 操作审计日志
      image/                 图片上传
  src/main/resources/
    application.yml          数据源、JWT、上传路径、CORS 等配置
    mapper/                  自定义 SQL
web/
  club-admin/                管理后台（端口 8080）
  club-pc/                   用户门户（端口 8081）
db/
  schema.sql                 全量建库建表
  seed-mock-data.sql         演示数据（可选，与假后端对齐）
  migration-*.sql            针对存量库的增量迁移
  cleanup-image-paths.sql    清洗历史脏图片路径（不自动执行）
tools/mock-api/              零依赖假后端（前端开发用）
scripts/run-backend.ps1      后端启动脚本（读取 .env）
Jenkinsfile                  构建流水线
```

## 主要功能

### 用户门户

- 注册、登录、退出、token 自动续签、修改资料与密码、设置头像；
- 忘记密码时通过密保问题自助重置；
- 浏览社团（分类筛选、详情、成员列表）、加入 / 退出社团、申请成立新社团；
- 浏览活动 / 新闻 / 公告（搜索、分页、图文详情、浏览量、活动名额）；
- 活动报名 / 取消报名，查看「我报名的活动」与签到状态；
- 站内通知（顶部铃铛 + 我的通知）：报名成功、审批结果、新成员加入等；
- 个人中心：头像、资料、密码、密保、我的社团、我的活动、社团管理。

### 社团管理员（社长 / 社团管理员）

在门户「个人中心 → 社团管理」中：

- 维护社团资料（简介、封面）；
- 管理成员（设为社团管理员、移出成员）；
- 发布、编辑、删除本社团的活动；
- 查看活动报名名单并核销签到。

### 管理后台

- 看板：核心指标 + 近 7 天报名趋势 + 内容类型分布；
- 用户 / 分类 / 社团 / 成员 / 内容管理；
- 内容支持草稿、发布、浏览量、名额设置、批量删除、导出 CSV、报名名单；
- 社团成立申请审批（通过后自动建社团并把申请人设为社长）；
- 操作日志（谁在什么时间调了什么接口）。

## 安装与运行

### 1. 准备数据库（MySQL 8）

```bash
mysql -u root -p < db/schema.sql
```

脚本会创建 `club` 库和 9 张表（`sys_user`、`team_category`、`team`、`news`、`member`、`activity_signup`、`team_apply`、`notification`、`audit_log`），并写入 5 条默认分类。

如需演示数据，再执行（可重复执行）：

```bash
mysql -u root -p club < db/seed-mock-data.sql
```

### 2. 启动后端

数据库密码和 JWT 密钥**不写在仓库里**，通过环境变量注入。先准备本地 `.env`：

```bash
cp Club-Web/.env.example Club-Web/.env   # 然后填写 JWT_SECRET 与 DB_PASSWORD
```

`JWT_SECRET` 为必填项，且不少于 32 字节，否则启动会直接失败。生成随机串：

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Max 256 }))
```

启动（脚本会读取 `.env` 再拉起 Spring Boot）：

```powershell
.\scripts\run-backend.ps1
```

使用 IDE 启动时，需要在运行配置里设置同样的环境变量。

### 3. 启动前端

两个应用相互独立，接口地址由各自的 `.env.development` 中的 `VITE_API_BASE_URL` 决定（默认 `http://localhost:8888`）：

```bash
cd web/club-admin && npm install && npm run dev    # http://localhost:8080
cd web/club-pc    && npm install && npm run dev    # http://localhost:8081
```

构建：`npm run build`（先跑 `vue-tsc` 类型检查，类型不过不会出包）。

> 两个前端用 `unplugin-vue-components` + `unplugin-auto-import` 做 Element Plus 按需引入，
> 会生成 `src/auto-imports.d.ts` 和 `src/components.d.ts`，**这两个声明文件需要一起提交**，
> 否则新克隆的仓库类型检查会失败。

### 4. 只想看前端，不想起后端 / 数据库（可选）

仓库自带一个零依赖假后端，接口路径和返回结构与真后端一致：

```bash
node tools/mock-api/server.mjs   # http://localhost:8888
```

它还支持模拟慢网络和接口故障（环境变量 `MOCK_DELAY` / `MOCK_FAIL`），用于查看 loading 与失败提示，详见 [tools/mock-api/README.md](tools/mock-api/README.md)。

## 使用示例

### 创建管理员账号

`schema.sql` 末尾附有注释掉的语句，解开并修改密码后执行（明文密码会在首次登录成功后自动升级为 BCrypt）：

```sql
INSERT INTO sys_user (username, password, nick_name, sex, phone, status, is_admin)
VALUES ('admin', 'your-password', '系统管理员', '0', '', 1, 1);
```

### 登录接口示例

```bash
curl -X POST http://localhost:8888/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

返回的 `data.token` 放入后续请求的 `token` 请求头中。

### 典型业务流

1. 学生提交「成立社团」申请（`POST /api/team-apply`）；
2. 管理员在后台「社团申请」中通过，系统自动创建社团并把申请人设为社长；
3. 社长在门户「社团管理」发布活动（可设名额）；
4. 其他学生报名活动，社长收到通知；
5. 社长在报名名单中核销签到。

## 配置说明

### 后端环境变量

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| `JWT_SECRET` | 签发 token 的密钥，**必填**，≥ 32 字节 | 无（缺失则启动失败） |
| `JWT_EXPIRATION` | token 有效期（毫秒） | 86400000（24 小时） |
| `DB_URL` | 数据库连接串 | `jdbc:mysql://localhost:3306/club?...` |
| `DB_USERNAME` / `DB_PASSWORD` | 数据库账号 / 密码 | `root` / 空 |
| `CORS_ORIGINS` | 允许跨域的前端地址，逗号分隔 | `http://localhost:8080,http://localhost:8081` |
| `UPLOAD_PATH` | 图片上传目录 | `D:/images/` |
| `AUDIT_RETENTION_DAYS` | 操作日志保留天数，0 表示不清理 | 90 |

### 后端可调安全参数（application.yml）

| 参数 | 说明 | 默认值 |
| --- | --- | --- |
| `security.login.max-attempts` | 连续登录失败锁定阈值 | 5 |
| `security.login.lock-minutes` | 锁定分钟数 | 15 |
| `security.rate-limit.enabled` | 是否开启限流 | true |
| `security.rate-limit.auth-per-minute` | 登录 / 注册 / 找回每分钟上限 | 20 |
| `security.rate-limit.write-per-minute` | 写操作每分钟上限 | 60 |
| `security.rate-limit.read-per-minute` | 读操作每分钟上限 | 300 |

### 上传目录

图片上传后通过 `/images/**` 对外访问，路径由 `UPLOAD_PATH` 决定。上传接口只信任文件头（jpg / png / gif / webp），单张上限 5MB（头像 2MB、社团封面 3MB）。

## 接口约定

所有接口统一返回：

```json
{ "msg": "查询成功", "code": 200, "data": {} }
```

- 除登录、注册和首页 / 列表类只读接口外，其余接口需在请求头携带 `token`（登录返回的 JWT）；
- 分页接口的 `data` 为 MyBatis-Plus 结构 `{ records, total, size, current }`；
- 启动后可在浏览器访问 `/swagger-ui/index.html` 查看交互式文档（JSON 见 `/v3/api-docs`）。

主要接口：

| 模块 | 接口 |
| --- | --- |
| 用户与登录 | `POST /api/user/login`、`POST /api/user/register`、`POST /api/user/refresh`、`POST /api/user/logout`、`GET /api/user/info`、`PUT /api/user/profile`、`PUT /api/user/password`、`/api/user` 增改删、`GET /api/user/list` |
| 账号安全 | `PUT /api/user/avatar`、`GET/PUT /api/user/security-question`、`POST /api/user/forgot/query`、`POST /api/user/forgot/reset` |
| 分类 | `/api/category` 增改删、`GET /api/category/list`、`GET /api/category/getSelectList` |
| 社团 | `/api/team` 增改删、`GET /api/team/list`、`GET /api/team/getTopTeam`、`GET /api/team/{id}`、`GET /api/team/{id}/members` |
| 社团申请 | `POST /api/team-apply`、`GET /api/team-apply/my`、`GET /api/team-apply/list`（管理员）、`PUT /api/team-apply/{id}/approve`、`PUT /api/team-apply/{id}/reject` |
| 社团管理 | `GET /api/club/my`、`GET /api/club/{teamId}/members`、`PUT/DELETE /api/club/{teamId}/member/{id}`、`GET/POST /api/club/{teamId}/news`、`PUT/DELETE /api/club/{teamId}/news/{id}`、`GET .../news/{newsId}/signups`、`PUT .../signup/{id}/checkin` |
| 内容 | `/api/news` 增改删、`GET /api/news/list`、`GET /api/news/detail/{id}`（浏览量 +1）、`getTopActivity`、`getTopNotice`、`getNewActivity`、`getNewNotice` |
| 成员 | `POST /api/member`（加入）、`PUT /api/member`（改角色）、`DELETE /api/member/{id}`（退团）、`GET /api/member/list` |
| 报名 | `POST /api/signup`、`DELETE /api/signup/{newsId}`、`GET /api/signup/my`、`GET /api/signup/count`、`GET /api/signup/list`（管理员） |
| 通知 | `GET /api/notification/my`、`GET /api/notification/unread`、`PUT /api/notification/{id}/read`、`PUT /api/notification/read-all` |
| 统计 | `GET /api/stats/overview`（仅管理员） |
| 审计 | `GET /api/audit/list`（仅管理员） |
| 上传 | `POST /api/upload/uploadImage`、`POST /api/upload/avatar`、`POST /api/upload/club`、`DELETE /api/upload?filename=`、`POST /api/upload/cleanup` |

## 数据约定

- `news.type`：`0` 活动 / `1` 新闻 / `2` 公告；`has_top = '1'` 表示首页推荐。
- `news.status`：`0` 草稿（仅管理员可见）/ `1` 已发布；`news.view_count` 由详情接口自增。
- `news.team_id`：社团自建活动所属社团，平台内容为空；`news.max_participants`：活动名额，`0` 表示不限。
- `member.role`：`0` 普通成员 / `1` 社团管理员 / `2` 社长；`status = 0` 表示已退出（逻辑删除，记录保留）。
- `team_apply.status`：`0` 待审 / `1` 通过 / `2` 驳回；通过时创建社团并把申请人设为 `member.role = 2`。
- `activity_signup`：同一用户对同一活动只有一行（`news_id + user_id` 唯一），取消报名把 `status` 置 0；`checked_in` 为签到标记。
- `notification` 存放报名、审批、新成员等事件的通知；`audit_log` 只记写操作，不含请求体。
- `image` / `avatar` 字段存相对路径（`/images/xxx.jpg`），前端用 `src/utils/image.ts` 的 `imgUrl()` 拼完整地址。
- 密码用 BCrypt 存储（`spring-security-crypto`）；历史明文密码在登录成功时自动升级为哈希。
- 权限在服务端判定：普通用户的 `userId` 一律取自 token（忽略请求体）；除成员模块外，写操作（POST / PUT / DELETE）要求管理员身份。

## 安全说明

- 密钥与密码只通过环境变量提供，`Club-Web/.env` 已被 `.gitignore` 忽略；
- **若仓库是公开的，历史上提交过的数据库密码已泄露，请务必更换 MySQL 密码**；
- JWT 通过 `jti` 支持登出（`POST /api/user/logout` 将 token 加入内存黑名单，单实例有效，多实例需换 Redis）；
- 同一账号连续登录失败 5 次锁定 15 分钟；
- CORS 只放行 `CORS_ORIGINS` 中的地址，且不使用 Cookie（鉴权走请求头 `token`）；
- 接口限流按「IP + 分组」每分钟计数，超出返回业务码 429；
- 操作审计：记录所有写请求的方法、路径、账号、IP、状态码与耗时（不记请求体），每天 03:30 清理超过保留期的记录；
- 忘记密码走密保问题（答案 BCrypt 存储，连续答错触发锁定），查不到账号与未设密保返回同一句提示，避免账号枚举。

## 已知问题 / 待办

- 限流与 token 黑名单为单实例内存实现，多实例部署需要换成 Redis；
- 无邮件 / 短信通道，找回密码仅支持密保问题；
- 活动签到目前由社团管理员手动核销，没有扫码 / 定位签到；
- 富文本编辑器（`@wangeditor`）约 810KB 的 chunk 已改为按需加载，但单次打开仍需下载，可考虑换更轻的编辑器或裁剪菜单；
- 前端暂无单元测试与 E2E，验证依赖脚本化浏览器测试（未入库）。

## 构建

根目录 `Jenkinsfile`：Maven 打包 `Club-Web` 的 jar，两个前端并行 `npm install && npm run build`，产物归档，并发送成功 / 失败邮件。

## 许可

[MIT](LICENSE) © 2024 Wsssfun
