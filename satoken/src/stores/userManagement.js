import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserManagementStore = defineStore('userManagement', {
  state: () => ({
    userList: [],
    pagination: {
      currentPage: 1,
      pageSize: 10,
      total: 0
    },
    loading: false
  }),

  getters: {
    getUserList: (state) => state.userList,
    getPagination: (state) => state.pagination
  },

  actions: {
    async fetchUserList(params = {}) {
      this.loading = true
      try {
        const queryParams = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
          ...params
        }
        
        const response = await axios.get('/user/page', { params: queryParams })
        const { code, data } = response.data

        if (code === 200) {
          this.userList = data.records
          this.pagination.total = data.total
          this.pagination.currentPage = data.current
          return { success: true, data }
        } else {
          return { success: false, message: '获取用户列表失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      } finally {
        this.loading = false
      }
    },

    async addUser(userData) {
      try {
        const response = await axios.post('/user/add', userData)
        const { code, data, msg } = response.data

        if (code === 200) {
          return { success: true, data, message: '添加用户成功' }
        } else {
          return { success: false, message: msg || '添加用户失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      }
    },

    async updateUser(userData) {
      try {
        const response = await axios.put('/user/edit', userData)
        const { code, data, msg } = response.data

        if (code === 200) {
          return { success: true, data, message: '更新用户成功' }
        } else {
          return { success: false, message: msg || '更新用户失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      }
    },

    async deleteUser(userId) {
      try {
        const response = await axios.delete(`/user/delete/${userId}`)
        const { code, data, msg } = response.data

        if (code === 200) {
          return { success: true, data, message: '删除用户成功' }
        } else {
          return { success: false, message: msg || '删除用户失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      }
    },

    // 设置分页参数
    setPagination(pagination) {
      this.pagination = { ...this.pagination, ...pagination }
    },

    // 重置分页参数
    resetPagination() {
      this.pagination = {
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    }
  }
})