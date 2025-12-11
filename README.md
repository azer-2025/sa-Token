# 🚀 Sa-Token RBAC Admin System | 极简权限管理系统

> **"权限管理，从未如此丝滑。"** 😎
>
> 一个基于 **Spring Boot** + **Sa-Token** + **Vue 3** + **Element Plus** 的前后端分离权限管理系统。 主打轻量、优雅、开箱即用！

## 📸 项目概览 (Project Overview)

这是一个标准的 **RBAC (Role-Based Access Control)** 权限模型实现。 前端采用清新的 Vue3 组合式 API 写法，后端利用 Sa-Token 强大的鉴权能力，实现了从**登录认证**到**细粒度权限控制**的全流程。

## 🛠️ 技术栈大揭秘 (Tech Stack)

### 🦁 后端 (Backend) - 稳如磐石

- **核心框架**: `Spring Boot 3.x` - 现代 Java 开发的标准。
- **权限引擎**: `Sa-Token` - 史上最轻量级 Java 权限认证框架 (登录、鉴权、Session 会话)。
- **ORM 框架**: `MyBatis Plus` - 极其方便的数据库操作，CRUD 不用写 SQL。
- **数据库**: `MySQL 8.0` - 数据持久化。
- **工具库**: `Lombok` - 告别 Getter/Setter 样板代码。

### ⚡ 前端 (Frontend) - 颜值在线

- **核心框架**: `Vue 3` (Composition API) - 响应式数据的最佳实践。
- **构建工具**: `Vite` - 极速冷启动，秒级热更新。
- **UI 组件库**: `Element Plus` - 经典的饿了么 UI 库 Vue3 版本。
- **状态管理**: `Pinia` - Vuex 的继任者，更轻、更直观。
- **路由管理**: `Vue Router 4` - 页面跳转管理。
- **网络请求**: `Axios` - 统一拦截器封装，处理 Token 自动携带。
- **图表展示**: `ECharts` - 首页炫酷的数据可视化。

## 🧩 核心功能与方法详解 (Features & API Details)

### 1. 🔐 认证模块 (Authentication)

*负责用户的登入、登出及身份识别，是系统的守门员。*

| 后端方法 (LoginController) | 前端交互 (Login.vue / UserStore) | 功能描述                                                     |
| -------------------------- | -------------------------------- | ------------------------------------------------------------ |
| `POST /auth/login`         | `userStore.login()`              | **登录接口**。1. 接收用户名/密码。2. `UserService` 校验账号密码。3. **核心**：调用 `StpUtil.login(id)` 生成 Token。4. 返回 Token 及用户信息。 |
| `POST /auth/logout`        | `userStore.logout()`             | **注销接口**。调用 `StpUtil.logout()`，服务端清除 Session，前端清除 localStorage。 |
| `GET /auth/info`           | `onMounted` / 路由守卫           | **获取当前用户信息**。通过 `StpUtil.getLoginIdAsLong()` 获取当前登录 ID，回显用户资料。 |

### 2. 👥 用户管理模块 (User Management)

*实现了完整的 CRUD，并且每个操作都加装了 Sa-Token 的权限锁！*

| 后端方法 (UserController)  | 权限注解 (@SaCheckPermission) | 功能描述                                                     |
| -------------------------- | ----------------------------- | ------------------------------------------------------------ |
| `GET /user/list`           | `"USER_VIEW"`                 | **全量列表**。获取所有用户数据。                             |
| `GET /user/page`           | `"USER_VIEW"`                 | **分页查询**。支持 `pageNum`, `pageSize`，配合前端 Element Pagination 组件。 |
| `POST /user/add`           | `"USER_ADD"`                  | **新增用户**。检查用户名唯一性，写入数据库。                 |
| `PUT /user/edit`           | `"USER_EDIT"`                 | **编辑用户**。更新用户基础信息、角色分配。                   |
| `DELETE /user/delete/{id}` | `"USER_DELETE"`               | **删除用户**。物理删除指定 ID 的用户。                       |

> **💡 技术亮点**： 全局异常处理器 (`GlobalExceptionHandler`) 会自动捕获 `NotPermissionException`。 如果你没有 `"USER_DELETE"` 权限却强行调用删除接口，系统会直接由后端抛出 403 错误，安全感拉满！🛡️

### 3. 🛡️ 权限鉴权引擎 (RBAC Engine)

*虽然你看不到 Controller，但它们在 `service` 和 `config` 层默默工作。*

- **SaTokenConfigure.java**:
  - 注册了 `SaInterceptor`，开启了注解式鉴权功能 (`@SaCheckPermission` 生效的关键)。
- **SaTokenPermissionConfig.java**:
  - 实现了 `StpInterface` 接口。
  - **核心逻辑**: 当 Sa-Token 需要验证权限时，会自动调用 `getPermissionList` 方法，从数据库 (`RolePermissionMapper`) 实时拉取当前用户的权限列表。
- **GlobalExceptionHandler.java**:
  - 统一处理 `NotLoginException` (未登录 -> 401) 和 `NotPermissionException` (无权限 -> 403)。

## 📂 项目结构 (Project Structure)

```
.
├── 后端 (Backend)
│   ├── controller      // 🛂 控制层 (Web API)
│   ├── service         // 🧠 业务逻辑层
│   ├── mapper          // 💾 数据访问层 (MyBatis)
│   ├── entity          // 📦 实体类 (User, Role, Permission)
│   ├── config          // ⚙️ 配置类 (SaToken, Cors, MVC)
│   └── exception       // 🚨 全局异常处理
│
└── 前端 (Frontend)
    ├── src/api         // 📡 Axios 请求封装
    ├── src/views       // 🖼️ 页面 (Login, Dashboard, UserMgmt)
    ├── src/stores      // 🏪 状态管理 (Pinia)
    └── src/router      // 🚦 路由配置
```

## 🏃‍♂️ 快速开始 (Getting Started)

### 1. 数据库准备

执行 `main/resources/db/schema.sql` 脚本，这将创建 `user`, `role`, `permission` 等表，并预置管理员账号。

> 🔑 **默认账号**: `admin` / `admin123` 👤 **普通账号**: `user` / `user123`

### 2. 后端启动 (Spring Boot)

1. 修改 `application.yml` 中的数据库连接信息。
2. 运行 `SaTokenApplication.java`。
3. 服务将在 `localhost:10011` 启动。

### 3. 前端启动 (Vue 3)

```
# 进入前端目录
cd frontend

# 安装依赖 (推荐 pnpm 或 yarn，速度飞起 🚀)
npm install

# 启动开发服务器
npm run dev
```

访问控制台输出的地址 (通常是 `http://localhost:5173`)，开启你的权限管理之旅！

## 🤝 贡献 (Contributing)

欢迎提交 PR 或 Issue！如果你觉得这个项目对你有帮助，请给一个小小的 Star ⭐️，这对我是莫大的鼓励！

*Powered by Azer | 2025*
