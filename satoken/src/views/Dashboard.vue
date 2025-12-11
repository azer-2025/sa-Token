<template>
  <el-container class="dashboard-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <div class="logo-content">
          <i class="logo-icon">S</i>
          <span class="logo-text">Sa-Token</span>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        :router="true"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/profile">
          <el-icon><UserFilled /></el-icon>
          <span>个人资料</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主体区域 -->
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-left">
          <div class="header-title">Sa-Token 权限管理系统</div>
        </div>
        <div class="header-right">
          <div class="notification">
            <el-badge :value="3" class="item">
              <el-icon class="bell-icon"><Bell /></el-icon>
            </el-badge>
          </div>
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" :icon="getUserAvatarIcon" class="user-avatar" />
              <div class="user-details">
                <div class="username">{{ userStore.getUserInfo.username || '未知用户' }}</div>
                <div class="user-role">{{ getUserRole }}</div>
              </div>
              <el-icon class="arrow-down"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
      
      <!-- 底部 -->
      <el-footer class="footer">
        <div class="footer-content">
          <div class="footer-left">© 2025 Sa-Token 权限管理系统</div>
          <div class="footer-right">Powered by Spring Boot & Vue 3</div>
        </div>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, UserFilled, ArrowDown, Odometer, Bell } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = ref('')

// 计算用户头像图标
const getUserAvatarIcon = computed(() => {
  const roleId = userStore.getUserInfo.roleId
  if (roleId === 1) return 'User'
  if (roleId === 2) return 'UserFilled'
  return 'User'
})

// 计算用户角色
const getUserRole = computed(() => {
  const roleId = userStore.getUserInfo.roleId
  if (roleId === 1) return '管理员'
  if (roleId === 2) return '普通用户'
  return '未知角色'
})

// 监听路由变化，设置当前激活的菜单
watch(
  () => route.path,
  (newPath) => {
    activeMenu.value = newPath
  },
  { immediate: true }
)

// 处理菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
}

// 处理用户命令（个人资料、系统设置、退出登录）
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/dashboard/profile')
      break
    case 'settings':
      ElMessage.info('系统设置功能待开发')
      break
    case 'logout':
      handleLogout()
      break
    default:
      break
  }
}

// 退出登录
const handleLogout = async () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (error) {
      console.error('退出登录出错:', error)
      ElMessage.error('退出登录失败')
    }
  }).catch(() => {
    // 用户取消退出
  })
}

// 组件挂载时获取用户信息
onMounted(async () => {
  if (!userStore.getIsLoggedIn) {
    const result = await userStore.fetchUserInfo()
    if (!result.success) {
      router.push('/login')
    }
  }
})
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.sidebar {
  background: linear-gradient(135deg, #2c3e50, #34495e);
  transition: width 0.3s ease;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.3);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-content {
  display: flex;
  align-items: center;
  color: white;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #3498db, #8e44ad);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  margin-right: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
  height: calc(100% - 60px);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #ffffff, #f8f9fa);
  color: #303133;
  border-bottom: 1px solid #eaeaea;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #3498db, #8e44ad);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
}

.notification {
  margin-right: 20px;
}

.bell-icon {
  font-size: 20px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
}

.bell-icon:hover {
  color: #3498db;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f0f0f0;
}

.user-avatar {
  margin-right: 10px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.user-role {
  font-size: 12px;
  color: #999;
}

.arrow-down {
  margin-left: 8px;
  font-size: 14px;
  color: #999;
}

.main-content {
  background: linear-gradient(135deg, #f0f2f5, #e1e5ea);
  padding: 20px;
  flex: 1;
  overflow: auto;
}

.footer {
  height: 50px;
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 13px;
  border-top: 1px solid #eaeaea;
  background: linear-gradient(135deg, #ffffff, #f8f9fa);
}

.footer-content {
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding: 0 20px;
}

.footer-left, .footer-right {
  display: flex;
  align-items: center;
}
</style>