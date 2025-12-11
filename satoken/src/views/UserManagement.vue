<template>
  <div class="user-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>用户管理</el-breadcrumb-item>
        </el-breadcrumb>
        <h1 class="page-title">用户管理</h1>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAddUser" round>
          <el-icon>
            <Plus />
          </el-icon>
          新增用户
        </el-button>
        <el-button @click="handleBatchDelete" :disabled="selectedUsers.length === 0" round>
          <el-icon>
            <Delete />
          </el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="search-form" label-position="left" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="用户名">
              <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="角色">
              <el-select v-model="searchForm.roleId" placeholder="请选择角色" clearable style="width: 100%;">
                <el-option label="管理员" :value="1" />
                <el-option label="普通用户" :value="2" />
                <el-option label="访客" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="创建时间">
              <el-date-picker v-model="searchForm.createTime" type="daterange" range-separator="至"
                start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <div class="form-buttons">
              <el-button type="primary" @click="handleSearch">
                <el-icon>
                  <Search />
                </el-icon>
                搜索
              </el-button>
              <el-button @click="resetSearch">
                <el-icon>
                  <Refresh />
                </el-icon>
                重置
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 用户表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <div class="table-header-left">
            <span class="table-title">用户列表</span>
            <el-tag type="info" effect="dark" size="small">共 {{ pagination.total }} 条记录</el-tag>
          </div>
          <div class="table-header-right">
            <el-button-group>
              <el-button :type="viewMode === 'table' ? 'primary' : 'default'" @click="viewMode = 'table'" size="small">
                <el-icon>
                  <List />
                </el-icon>
                表格视图
              </el-button>
              <el-button :type="viewMode === 'card' ? 'primary' : 'default'" @click="viewMode = 'card'" size="small">
                <el-icon>
                  <Grid />
                </el-icon>
                卡片视图
              </el-button>
            </el-button-group>
          </div>
        </div>
      </template>

      <!-- 表格视图 -->
      <div v-show="viewMode === 'table'">
        <el-table :data="userList" stripe style="width: 100%" v-loading="loading"
          @selection-change="handleSelectionChange" row-key="id"
          :default-sort="{ prop: 'createTime', order: 'descending' }">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" sortable />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="roleId" label="角色" width="120" :formatter="formatRole" sortable />
          <el-table-column prop="createTime" label="创建时间" width="180" sortable />
          <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="handleView(scope.row)">
                <el-icon>
                  <View />
                </el-icon>
                查看
              </el-button>
              <el-button size="small" @click="handleEdit(scope.row)">
                <el-icon>
                  <Edit />
                </el-icon>
                编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">
                <el-icon>
                  <Delete />
                </el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-show="viewMode === 'card'" class="card-view">
        <el-row :gutter="20">
          <el-col :span="6" v-for="user in userList" :key="user.id" class="card-col">
            <el-card class="user-card" shadow="hover">
              <div class="card-header">
                <el-avatar :size="40" :icon="getUserAvatarIcon(user.roleId)" />
                <div class="card-title">
                  <div class="username">{{ user.username }}</div>
                  <el-tag :type="getUserRoleTagType(user.roleId)" size="small">
                    {{ formatRole(null, null, user.roleId) }}
                  </el-tag>
                </div>
              </div>
              <div class="card-content">
                <div class="info-item">
                  <span class="label">创建时间:</span>
                  <span class="value">{{ user.createTime }}</span>
                </div>
                <div class="info-item">
                  <span class="label">更新时间:</span>
                  <span class="value">{{ user.updateTime }}</span>
                </div>
              </div>
              <div class="card-footer">
                <el-button size="small" @click="handleView(user)">
                  <el-icon>
                    <View />
                  </el-icon>
                  查看
                </el-button>
                <el-button size="small" @click="handleEdit(user)">
                  <el-icon>
                    <Edit />
                  </el-icon>
                  编辑
                </el-button>
                <el-button size="small" type="danger" @click="handleDelete(user.id)">
                  <el-icon>
                    <Delete />
                  </el-icon>
                  删除
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" background />
      </div>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog :title="editingUser ? '编辑用户' : '新增用户'" v-model="dialogVisible" width="500px" @close="handleDialogClose"
      :destroy-on-close="true">
      <el-form :model="userForm" :rules="userFormRules" ref="userFormRef" label-width="80px" class="user-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!editingUser">
          <el-input v-model="userForm.password" type="password" show-password placeholder="请输入密码" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!editingUser">
          <el-input v-model="userForm.confirmPassword" type="password" show-password placeholder="请再次输入密码"
            prefix-icon="Lock" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" placeholder="请选择角色" style="width: 100%;" prefix-icon="UserFilled">
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="2" />
            <el-option label="访客" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser" :loading="submitLoading" :disabled="submitLoading">
            {{ submitLoading ? '提交中...' : '确认' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog title="用户详情" v-model="detailDialogVisible" width="500px" @close="detailDialogVisible = false">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ detailUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailUser.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ formatRole(null, null, detailUser.roleId) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailUser.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Delete,
  Search,
  Refresh,
  List,
  Grid,
  View,
  Edit
} from '@element-plus/icons-vue'
import axios from 'axios'

const userFormRef = ref()

// 控制对话框显示
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)

// 是否正在编辑用户
const editingUser = ref(false)

// 提交按钮加载状态
const submitLoading = ref(false)

// 加载状态
const loading = ref(false)

// 视图模式
const viewMode = ref('table')

// 用户列表
const userList = ref([])

// 选中的用户
const selectedUsers = ref([])

// 详情用户
const detailUser = ref({})

// 搜索表单
const searchForm = reactive({
  username: '',
  roleId: undefined,
  createTime: []
})

// 分页参数
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 用户表单
const userForm = reactive({
  id: '',
  username: '',
  password: '',
  confirmPassword: '',
  roleId: 2, // 默认普通用户
})

// 用户表单验证规则
const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名长度至少3位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (userForm.password && value !== userForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 格式化角色显示
const formatRole = (row, column, cellValue) => {
  switch (cellValue) {
    case 1:
      return '管理员'
    case 2:
      return '普通用户'
    case 3:
      return '访客'
    default:
      return '未知'
  }
}

// 获取用户头像图标
const getUserAvatarIcon = (roleId) => {
  switch (roleId) {
    case 1: return 'UserFilled'
    case 2: return 'User'
    case 3: return 'User'
    default: return 'User'
  }
}

// 获取角色标签类型
const getUserRoleTagType = (roleId) => {
  switch (roleId) {
    case 1: return 'danger'
    case 2: return 'primary'
    case 3: return 'info'
    default: return 'info'
  }
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
    }

    const response = await axios.get('/user/page', { params })
    console.log(response);
    const { code, data } = response.data
    if (code === 200) {
      userList.value = data.records
      pagination.total = data.total
    } else {
      ElMessage.error('获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 处理新增用户
const handleAddUser = () => {
  editingUser.value = false
  resetUserForm()
  dialogVisible.value = true
}

// 处理编辑用户
const handleEdit = (user) => {
  editingUser.value = true
  Object.assign(userForm, {
    id: user.id,
    username: user.username,
    password: '',
    confirmPassword: '',
    roleId: user.roleId
  })
  dialogVisible.value = true
}

// 处理查看用户
const handleView = (user) => {
  detailUser.value = user
  detailDialogVisible.value = true
}

// 处理删除用户
const handleDelete = (userId) => {
  ElMessageBox.confirm('确定要删除该用户吗？删除后无法恢复！', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.delete(`/user/delete/${userId}`)
      const { code, msg } = response.data

      if (code === 200) {
        ElMessage.success('删除成功')
        await fetchUserList()
      } else {
        ElMessage.error(msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 批量删除用户
const handleBatchDelete = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }

  ElMessageBox.confirm(`确定要批量删除选中的 ${selectedUsers.value.length} 个用户吗？删除后无法恢复！`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const userIds = selectedUsers.value.map(user => user.id)
      // 这里应该调用批量删除接口
      ElMessage.success(`成功删除 ${selectedUsers.value.length} 个用户`)
      selectedUsers.value = []
      await fetchUserList()
    } catch (error) {
      ElMessage.error('批量删除失败: ' + error.message)
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
      submitLoading.value = true
      try {
        let response
        if (editingUser.value) {
          // 编辑用户
          const { id, username, roleId } = userForm
          response = await axios.put('/user/edit', { id, username, roleId, updateTime: new Date() })
        } else {
          // 新增用户
          const { username, password, roleId } = userForm
          response = await axios.post('/user/add', { username, password, roleId, createTime: new Date(), updateTime: new Date() })
        }

        const { code, msg } = response.data

        if (code === 200) {
          ElMessage.success(`${editingUser.value ? '编辑' : '新增'}成功`)
          dialogVisible.value = false
          await fetchUserList()
        } else {
          ElMessage.error(msg || `${editingUser.value ? '编辑' : '新增'}失败`)
        }
      } catch (error) {
        ElMessage.error(`${editingUser.value ? '编辑' : '新增'}失败: ` + error.message)
      } finally {
        submitLoading.value = false
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
    confirmPassword: '',
    roleId: 2
  })
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  resetUserForm()
}

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchUserList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.username = ''
  searchForm.roleId = undefined
  searchForm.createTime = []
  pagination.currentPage = 1
  fetchUserList()
}

// 处理分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  fetchUserList()
}

// 处理当前页改变
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchUserList()
}

// 处理多选
const handleSelectionChange = (val) => {
  selectedUsers.value = val
}

// 组件挂载时获取用户列表
onMounted(async () => {
  await fetchUserList()
})
</script>

<style scoped>
.user-management-container {
  padding: 20px;
  height: 100%;
  overflow: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #ffffff, #f8f9fa);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-title {
  margin: 10px 0 0 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  gap: 10px;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.search-form {
  padding: 20px 0;
}

.form-buttons {
  display: flex;
  gap: 10px;
}

.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.table-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.card-view {
  padding: 20px 0;
}

.card-col {
  margin-bottom: 20px;
}

.user-card {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.card-title {
  margin-left: 15px;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.card-content {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: #909399;
}

.value {
  color: #606266;
}

.card-footer {
  display: flex;
  justify-content: space-between;
}

.user-form {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>