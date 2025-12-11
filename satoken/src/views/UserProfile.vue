<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>个人资料</el-breadcrumb-item>
        </el-breadcrumb>
        <h1 class="page-title">个人资料</h1>
      </div>
    </div>

    <!-- 个人信息卡片 -->
    <el-row :gutter="20">
      <!-- 左侧头像区域 -->
      <el-col :span="8">
        <el-card class="avatar-card">
          <div class="avatar-content">
            <el-avatar 
              :size="100" 
              :icon="getUserAvatarIcon" 
              class="user-avatar"
            />
            <div class="user-name">{{ userStore.getUserInfo.username }}</div>
            <el-tag :type="getUserRoleTagType" effect="dark" class="user-role">
              {{ getUserRole }}
            </el-tag>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">12</div>
                <div class="stat-label">登录次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">5</div>
                <div class="stat-label">操作记录</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">30</div>
                <div class="stat-label">在线时长(小时)</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧信息区域 -->
      <el-col :span="16">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-card class="info-card">
              <template #header>
                <div class="card-header">
                  <span>基本信息</span>
                </div>
              </template>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="用户ID">
                  <span class="info-value">{{ userStore.getUserInfo.userId }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="用户名">
                  <span class="info-value">{{ userStore.getUserInfo.username }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="角色">
                  <el-tag :type="getUserRoleTagType" size="small">
                    {{ getUserRole }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">
                  <span class="info-value">2025-01-01 12:00:00</span>
                </el-descriptions-item>
                <el-descriptions-item label="最后登录">
                  <span class="info-value">2025-12-08 09:00:00</span>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-tab-pane>

          <!-- 安全设置 -->
          <el-tab-pane label="安全设置" name="security">
            <el-card class="security-card">
              <template #header>
                <div class="card-header">
                  <span>安全设置</span>
                </div>
              </template>
              <div class="security-settings">
                <div class="setting-item">
                  <div class="setting-info">
                    <div class="setting-title">修改密码</div>
                    <div class="setting-desc">建议定期修改密码以保护账户安全</div>
                  </div>
                  <el-button type="primary" @click="handleEditPassword">修改</el-button>
                </div>
                <div class="setting-item">
                  <div class="setting-info">
                    <div class="setting-title">绑定手机</div>
                    <div class="setting-desc">未绑定</div>
                  </div>
                  <el-button>绑定</el-button>
                </div>
                <div class="setting-item">
                  <div class="setting-info">
                    <div class="setting-title">绑定邮箱</div>
                    <div class="setting-desc">未绑定</div>
                  </div>
                  <el-button>绑定</el-button>
                </div>
                <div class="setting-item">
                  <div class="setting-info">
                    <div class="setting-title">二次验证</div>
                    <div class="setting-desc">未开启</div>
                  </div>
                  <el-button>开启</el-button>
                </div>
              </div>
            </el-card>
          </el-tab-pane>

          <!-- 操作记录 -->
          <el-tab-pane label="操作记录" name="logs">
            <el-card class="logs-card">
              <template #header>
                <div class="card-header">
                  <span>最近操作记录</span>
                </div>
              </template>
              <el-table :data="operationLogs" style="width: 100%" max-height="400">
                <el-table-column prop="time" label="操作时间" width="180" />
                <el-table-column prop="action" label="操作类型" width="150" />
                <el-table-column prop="ip" label="IP地址" width="150" />
                <el-table-column prop="location" label="地点" />
              </el-table>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog 
      v-model="passwordDialogVisible" 
      title="修改密码" 
      width="500px"
      :destroy-on-close="true"
    >
      <el-form 
        :model="passwordForm" 
        :rules="passwordFormRules" 
        ref="passwordFormRef" 
        label-width="100px"
        class="password-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            show-password 
            placeholder="请输入原密码"
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password 
            placeholder="请输入新密码"
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password 
            placeholder="请再次输入新密码"
            prefix-icon="Lock"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitPasswordChange"
            :loading="passwordSubmitLoading"
          >
            {{ passwordSubmitLoading ? '提交中...' : '确认' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const passwordDialogVisible = ref(false)
const passwordFormRef = ref()
const activeTab = ref('basic')
const passwordSubmitLoading = ref(false)

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 操作记录
const operationLogs = ref([
  { time: '2025-12-08 09:00:00', action: '登录系统', ip: '192.168.1.100', location: '北京' },
  { time: '2025-12-07 15:30:00', action: '修改用户信息', ip: '192.168.1.100', location: '北京' },
  { time: '2025-12-07 10:15:00', action: '登录系统', ip: '192.168.1.100', location: '北京' },
  { time: '2025-12-06 14:20:00', action: '查看用户列表', ip: '192.168.1.100', location: '北京' },
  { time: '2025-12-06 09:05:00', action: '登录系统', ip: '192.168.1.100', location: '北京' }
])

// 修改密码表单验证规则
const passwordFormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 计算用户头像图标
const getUserAvatarIcon = computed(() => {
  const roleId = userStore.getUserInfo.roleId
  if (roleId === 1) return 'UserFilled'
  if (roleId === 2) return 'User'
  return 'User'
})

// 计算用户角色
const getUserRole = computed(() => {
  const roleId = userStore.getUserInfo.roleId
  if (roleId === 1) return '管理员'
  if (roleId === 2) return '普通用户'
  return '未知角色'
})

// 计算用户角色标签类型
const getUserRoleTagType = computed(() => {
  const roleId = userStore.getUserInfo.roleId
  if (roleId === 1) return 'danger'
  if (roleId === 2) return 'primary'
  return 'info'
})

// 处理修改密码
const handleEditPassword = () => {
  passwordDialogVisible.value = true
  // 重置表单
  Object.keys(passwordForm).forEach(key => {
    passwordForm[key] = ''
  })
}

// 提交密码修改
const submitPasswordChange = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordSubmitLoading.value = true
      // 这里应该调用API修改密码
      // 模拟API调用
      try {
        // await axios.post('/api/user/change-password', passwordForm)
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟网络延迟
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
      } catch (error) {
        ElMessage.error('密码修改失败: ' + error.message)
      } finally {
        passwordSubmitLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  height: 100%;
  overflow: auto;
}

.page-header {
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

.avatar-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.avatar-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
}

.user-avatar {
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.user-role {
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  width: 100%;
  padding: 0 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.profile-tabs {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.info-card,
.security-card,
.logs-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.info-value {
  color: #606266;
}

.security-settings {
  padding: 20px 0;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
}

.setting-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
}

.setting-desc {
  font-size: 12px;
  color: #909399;
}

.password-form {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>