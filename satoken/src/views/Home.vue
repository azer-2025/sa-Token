<template>
  <div class="home-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="stat in stats" :key="stat.id">
        <el-card class="stat-card" :class="stat.cardClass">
          <div class="stat-content">
            <div class="stat-icon" :class="stat.iconClass">
              <component :is="stat.icon" />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stat.count }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表和活动区域 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">用户增长趋势</span>
              <el-select v-model="timeRange" size="small" @change="handleTimeRangeChange">
                <el-option label="近7天" value="7" />
                <el-option label="近30天" value="30" />
                <el-option label="近90天" value="90" />
              </el-select>
            </div>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">最近活动</span>
              <el-link type="primary" @click="refreshActivities">刷新</el-link>
            </div>
          </template>
          <div class="activity-list">
            <div class="activity-item" v-for="item in recentActivities" :key="item.id" @click="viewActivityDetail(item)">
              <div class="activity-icon">
                <el-avatar :icon="item.icon" :size="36" :style="{ backgroundColor: item.color }"
                  class="activity-avatar" />
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ item.title }}</div>
                <div class="activity-desc">{{ item.description }}</div>
                <div class="activity-time">{{ item.time }}</div>
              </div>
              <div class="activity-arrow">
                <el-icon>
                  <ArrowRight />
                </el-icon>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-actions-row">
      <el-col :span="24">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" v-for="action in quickActions" :key="action.id" @click="handleQuickAction(action)">
              <div class="action-icon" :style="{ backgroundColor: action.color }">
                <component :is="action.icon" />
              </div>
              <div class="action-label">{{ action.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { User, UserFilled, Tickets, Setting, ArrowRight, Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

// 时间范围
const timeRange = ref('7')

// 统计数据
const stats = ref([
  { id: 1, icon: 'User', iconClass: 'bg-blue', cardClass: 'card-blue', count: 128, label: '用户总数' },
  { id: 2, icon: 'UserFilled', iconClass: 'bg-green', cardClass: 'card-green', count: 5, label: '管理员数' },
  { id: 3, icon: 'Tickets', iconClass: 'bg-orange', cardClass: 'card-orange', count: 8, label: '角色数量' },
  { id: 4, icon: 'Setting', iconClass: 'bg-purple', cardClass: 'card-purple', count: 24, label: '权限数量' }
])

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    icon: 'User',
    color: '#409eff',
    title: '新增用户',
    description: 'admin',
    time: '2分钟前'
  },
  {
    id: 2,
    icon: 'Setting',
    color: '#67c23a',
    title: '权限更新',
    description: '用户权限配置已更新',
    time: '1小时前'
  },
  {
    id: 3,
    icon: 'UserFilled',
    color: '#e6a23c',
    title: '用户登录',
    description: '用户 admin 登录系统',
    time: '2小时前'
  },
  {
    id: 4,
    icon: 'Tickets',
    color: '#f56c6c',
    title: '角色创建',
    description: '创建新角色 Manager',
    time: '1天前'
  },
  {
    id: 5,
    icon: 'User',
    color: '#909399',
    title: '用户删除',
    description: '删除用户 test',
    time: '2天前'
  }
])

// 快捷操作
const quickActions = ref([
  { id: 1, icon: 'Plus', color: '#409eff', label: '新增用户', action: 'addUser' },
  { id: 2, icon: 'Edit', color: '#67c23a', label: '编辑用户', action: 'editUser' },
  { id: 3, icon: 'Delete', color: '#f56c6c', label: '删除用户', action: 'deleteUser' },
  { id: 4, icon: 'Search', color: '#909399', label: '查询用户', action: 'searchUser' }
])

// 图表引用
const chartRef = ref()
let chartInstance = null

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#eee',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: getTimeRangeData(timeRange.value)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '用户增长',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(64, 158, 255, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(64, 158, 255, 0.01)'
            }
          ])
        },
        lineStyle: {
          color: '#409eff',
          width: 2
        },
        itemStyle: {
          color: '#409eff',
          borderWidth: 2
        },
        data: generateRandomData(7)
      }
    ]
  }

  chartInstance.setOption(option)

  // 响应窗口大小变化
  window.addEventListener('resize', () => {
    chartInstance.resize()
  })
}

// 获取时间范围数据
const getTimeRangeData = (days) => {
  const data = []
  const today = new Date()

  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    data.push(`${date.getMonth() + 1}-${date.getDate()}`)
  }

  return data
}

// 生成随机数据
const generateRandomData = (count) => {
  const data = []
  let base = 100

  for (let i = 0; i < count; i++) {
    base += Math.floor(Math.random() * 20) - 5
    data.push(base)
  }

  return data
}

// 处理时间范围变化
const handleTimeRangeChange = (value) => {
  if (!chartInstance) return

  const option = {
    xAxis: {
      data: getTimeRangeData(value)
    },
    series: [
      {
        data: generateRandomData(parseInt(value))
      }
    ]
  }

  chartInstance.setOption(option)
}

// 刷新活动
const refreshActivities = () => {
  ElMessage.success('活动已刷新')
}

// 查看活动详情
const viewActivityDetail = (activity) => {
  ElMessage.info(`查看活动: ${activity.title}`)
}

// 处理快捷操作
const handleQuickAction = (action) => {
  switch (action.action) {
    case 'addUser':
      router.push('/dashboard/users')
      break
    case 'editUser':
      router.push('/dashboard/users')
      break
    case 'deleteUser':
      ElMessage.warning('请选择要删除的用户')
      router.push('/dashboard/users')
      break
    case 'searchUser':
      router.push('/dashboard/users')
      break
    default:
      ElMessage.info(`执行操作: ${action.label}`)
  }
}

onMounted(() => {
  initChart()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  height: 100%;
  overflow: auto;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: none;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.card-blue {
  background: linear-gradient(135deg, #409eff, #3486ff);
  color: white;
}

.card-green {
  background: linear-gradient(135deg, #67c23a, #52b026);
  color: white;
}

.card-orange {
  background: linear-gradient(135deg, #e6a23c, #d6902c);
  color: white;
}

.card-purple {
  background: linear-gradient(135deg, #909399, #7a7d84);
  color: white;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon.bg-blue {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.stat-icon.bg-green {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.stat-icon.bg-orange {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.stat-icon.bg-purple {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.content-row {
  margin-bottom: 20px;
}

.chart-card,
.recent-card,
.quick-actions-card {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 350px;
  padding: 10px;
}

.activity-list {
  height: 350px;
  overflow-y: auto;
  padding: 0 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.activity-item:hover {
  background-color: #f9f9f9;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  margin-right: 15px;
}

.activity-avatar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

.activity-arrow {
  color: #c0c4cc;
  font-size: 14px;
}

.quick-actions-row {
  margin-bottom: 20px;
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  padding: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 15px;
  border-radius: 12px;
}

.action-item:hover {
  background-color: #f5f7fa;
  transform: translateY(-3px);
}

.action-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}
</style>