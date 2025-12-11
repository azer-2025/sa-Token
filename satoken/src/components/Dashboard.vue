<template>
  <el-container class="dashboard-container">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="sidebar">
      <el-menu :default-active="activeMenu" class="sidebar-menu" @select="handleMenuSelect" background-color="#304156"
        text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="users">
          <el-icon>
            <User />
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="profile">
          <el-icon>
            <UserFilled />
          </el-icon>
          <span>个人资料</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主体区域 -->
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-title">管理系统</div>
        <div class="user-actions">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="30" icon="User" />
              <span class="username">{{ userInfo.username }}</span>
              <el-icon>
                <ArrowDown />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <!-- 用户管理页面 -->
        <div v-if="activeMenu === 'users'" class="content-panel">
          <div class="panel-header">
            <h2>用户管理</h2>
            <el-button type="primary" @click="showAddUserDialog = true">
              <el-icon>
                <Plus />
              </el-icon>
              新增用户
            </el-button>
          </div>

          <el-table :data="userList" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="roleId" label="角色" :formatter="formatRole" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 个人资料页面 -->
        <div v-if="activeMenu === 'profile'" class="content-panel">
          <h2>个人资料</h2>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户ID">{{ userInfo.userId }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ formatRole(null, null, userInfo.roleId) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-main>
    </el-container>
  </el-container>

  <!-- 新增/编辑用户对话框 -->
  <el-dialog :title="editingUser ? '编辑用户' : '新增用户'" v-model="showAddUserDialog" width="500px">
    <el-form :model="userForm" :rules="userFormRules" ref="userFormRef" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="userForm.username" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!editingUser">
        <el-input v-model="userForm.password" type="password" />
      </el-form-item>
      <el-form-item label="角色" prop="roleId">
        <el-select v-model="userForm.roleId" placeholder="请选择角色">
          <el-option label="管理员" :value="1" />
          <el-option label="普通用户" :value="2" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showAddUserDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, UserFilled, ArrowDown, Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 当前激活的菜单
const activeMenu = ref('users')

// 用户信息
const userInfo = reactive({
  userId: '',
  username: '',
  roleId: ''
})

// 用户列表
const userList = ref([])
const loading = ref(false)

// 控制新增用户对话框显示
const showAddUserDialog = ref(false)

// 是否正在编辑用户
const editingUser = ref(false)

// 用户表单
const userForm = reactive({
  id: '',
  username: '',
  password: '',
  roleId: 2 // 默认普通用户
})

// 用户表单引用
const userFormRef = ref()

// 用户表单验证规则
const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 格式化角色显示
const formatRole = (row, column, cellValue) => {
  // 如果有 roleName 字段，优先使用它
  if (row && row.roleName) {
    return row.roleName
  }

  // 否则根据 roleId 判断
  switch (cellValue) {
    case 1:
      return '管理员'
    case 2:
      return '普通用户'
    default:
      return '未知'
  }
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
}

// 处理用户命令（个人资料、退出登录）
const handleUserCommand = (command) => {
  if (command === 'profile') {
    activeMenu.value = 'profile'
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await axios.get('/auth/info')
    const result = response.data

    if (result.code === 200) {
      Object.assign(userInfo, result.data)
    } else {
      // 未登录或登录过期，跳转到登录页
      router.push('/login')
    }
  } catch (error) {
    console.error('获取用户信息出错:', error)
    router.push('/login')
  }
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    loading.value = true
    const response = await axios.get('/user/list')
    const result = response.data

    if (result.code === 200) {
      userList.value = result.data
    }
  } catch (error) {
    console.error('获取用户列表出错:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await axios.post('/auth/logout')

    // 清除本地token并跳转到登录页
    localStorage.removeItem('satoken')
    router.push('/login')
    ElMessage.success('退出成功')
  } catch (error) {
    console.error('退出登录出错:', error)
    ElMessage.error('退出登录失败')
  }
}

// 处理编辑用户
const handleEdit = (user) => {
  editingUser.value = true
  Object.assign(userForm, user)
  showAddUserDialog.value = true
}

// 处理删除用户
const handleDelete = (userId) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.delete(`/user/delete/${userId}`)
      const result = response.data

      if (result.code === 200) {
        ElMessage.success('删除成功')
        // 重新获取用户列表
        fetchUserList()
      } else {
        ElMessage.error(result.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除用户出错:', error)
      ElMessage.error('删除用户失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 保存用户（新增或编辑）
const saveUser = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (editingUser.value) {
          // 编辑用户
          response = await axios.put('/user/edit', userForm)
        } else {
          // 新增用户
          response = await axios.post('/user/add', userForm)
        }

        const result = response.data

        if (result.code === 200) {
          ElMessage.success(`${editingUser.value ? '编辑' : '新增'}成功`)
          // 关闭对话框
          showAddUserDialog.value = false
          // 重置表单
          resetUserForm()
          // 重新获取用户列表
          fetchUserList()
        } else {
          ElMessage.error(result.msg || `${editingUser.value ? '编辑' : '新增'}失败`)
        }
      } catch (error) {
        console.error(`${editingUser.value ? '编辑' : '新增'}用户出错:`, error)
        ElMessage.error(`${editingUser.value ? '编辑' : '新增'}用户失败`)
      }
    }
  })
}

// 重置用户表单
const resetUserForm = () => {
  Object.assign(userForm, {
    id: '',
    username: '',
    password: '',
    roleId: 2
  })
  editingUser.value = false
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 组件挂载时获取用户信息和用户列表
onMounted(() => {
  fetchUserInfo()
  fetchUserList()
})
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
}

.sidebar-menu {
  border: none;
  height: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff;
  color: #303133;
  border-bottom: 1px solid #dcdfe6;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 8px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

.content-panel {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-header h2 {
  margin: 0;
}
</style>