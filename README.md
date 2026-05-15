# 便利店管理系统

## 项目结构
- `frontend/` - Vue 3 + Vite 前端
- `backend/` - Spring Boot 后端
- `data/` - JSON数据文件

## 启动说明

### 后端启动
1. 进入backend目录
2. 使用Maven构建并运行：
```bash
cd backend
mvn spring-boot:run
```
后端将在 http://localhost:8080 启动

### 前端启动
1. 进入frontend目录
2. 安装依赖并运行：
```bash
cd frontend
npm install
npm run dev
```
前端将在 http://localhost:5173 启动

## 默认账号
- 管理员: admin / admin123
- 用户: user1 / user123 或 user2 / user123

## 功能说明
- 登录界面根据账号角色跳转不同页面
- 管理员可以管理商品（增删改）和查看/删除所有购买记录
- 用户可以浏览商品（按分类）和查看自己的购买记录
- 数据持久化存储在数据库中
