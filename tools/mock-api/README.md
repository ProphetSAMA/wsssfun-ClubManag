# 假后端（前端开发用）

没有 MySQL、不启动 `Club-Web` 时，用它把两个前端跑起来看页面。

- 零依赖：只需要 Node 18+，不用 `npm install`
- 接口路径和返回结构和真实后端一致（`{ msg, code, data }`，分页是 `{ records, total }`）
- 数据在内存里：加入/退出社团会改状态，重启即还原
- 封面图由 `/images/team{n}.svg` 现场生成，不依赖外网

## 用法

```bash
node tools/mock-api/server.mjs            # http://localhost:8888
```

两个前端的 `.env.development` 里 `VITE_API_BASE_URL` 默认就指向 `http://localhost:8888`，
所以直接起前端即可：

```bash
cd web/club-pc && npm run dev        # http://localhost:8081
cd web/club-admin && npm run dev     # http://localhost:8080
```

后台用 `admin` / 任意密码登录（登录接口直接返回 mock token）。

## 可调参数

| 环境变量 | 作用 |
| --- | --- |
| `PORT` | 监听端口，默认 8888 |
| `MOCK_DELAY` | 给每个请求加固定延迟（毫秒），用来看 loading 态 |
| `MOCK_FAIL` | 路径前缀，命中后返回 `code: 500`，用来看失败态 |

PowerShell 示例：

```powershell
$env:MOCK_DELAY='1500'; node tools/mock-api/server.mjs      # 慢网络
$env:MOCK_FAIL='/api/news'; node tools/mock-api/server.mjs  # 内容接口报错
```

## 已实现的接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/user/login` | 固定返回管理员 token |
| GET | `/api/user/info` | 当前用户 |
| GET | `/api/user/list` | 24 条假用户，支持分页 |
| GET | `/api/category/list` `/api/category/getSelectList` | 5 个分类 |
| GET | `/api/team/list` `/api/team/getTopTeam` | 8 个社团 |
| GET | `/api/news/list` `getTopActivity` `getTopNotice` `getNewActivity` `getNewNotice` | 活动/新闻/公告，带正文 |
| GET/POST/DELETE | `/api/member/list` `/api/member` `/api/member/{id}` | 成员关系（内存） |
| GET | `/api/health` | 存活检查 |

写接口（`POST/PUT/DELETE`）只实现了成员相关的两个，其余返回 404 提示；
真要做增删改验证，还是起真实后端。
