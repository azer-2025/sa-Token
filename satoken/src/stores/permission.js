import { defineStore } from 'pinia'
import axios from 'axios'
import { useUserStore } from './user'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    permissions: [],
    roles: [],
    loading: false
  }),

  getters: {
    getPermissions: (state) => state.permissions,
    getRoles: (state) => state.roles,
    getUserPermissions: (state) => (userId) => {
      // 根据用户ID获取权限列表
      return state.permissions.filter(permission => 
        permission.users.includes(userId)
      )
    }
  },

  actions: {
    async fetchPermissions() {
      this.loading = true
      try {
        // 模拟获取权限列表
        const response = await axios.get('/permission/list')
        const { code, data } = response.data

        if (code === 200) {
          this.permissions = data
          return { success: true, data }
        } else {
          return { success: false, message: '获取权限列表失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      } finally {
        this.loading = false
      }
    },

    async fetchRoles() {
      this.loading = true
      try {
        // 模拟获取角色列表
        const response = await axios.get('/role/list')
        const { code, data } = response.data

        if (code === 200) {
          this.roles = data
          return { success: true, data }
        } else {
          return { success: false, message: '获取角色列表失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      } finally {
        this.loading = false
      }
    },

    async addRole(roleData) {
      try {
        const response = await axios.post('/role/add', roleData)
        const { code, data, msg } = response.data

        if (code === 200) {
          // 更新角色列表
          await this.fetchRoles()
          return { success: true, data, message: '添加角色成功' }
        } else {
          return { success: false, message: msg || '添加角色失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      }
    },

    async updateRole(roleData) {
      try {
        const response = await axios.put('/role/edit', roleData)
        const { code, data, msg } = response.data

        if (code === 200) {
          // 更新角色列表
          await this.fetchRoles()
          return { success: true, data, message: '更新角色成功' }
        } else {
          return { success: false, message: msg || '更新角色失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      }
    },

    async deleteRole(roleId) {
      try {
        const response = await axios.delete(`/role/delete/${roleId}`)
        const { code, data, msg } = response.data

        if (code === 200) {
          // 更新角色列表
          await this.fetchRoles()
          return { success: true, data, message: '删除角色成功' }
        } else {
          return { success: false, message: msg || '删除角色失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      }
    },

    // 检查用户是否有特定权限
    hasPermission(permissionName) {
      // 在实际应用中，这里应该根据用户的角色和权限进行判断
      // 现在我们简化处理，假设管理员有所有权限
      const userStore = useUserStore()
      if (userStore.getUserInfo.roleId === 1) {
        return true // 管理员拥有所有权限
      }
      
      // 其他情况根据具体权限判断
      return this.permissions.some(p => 
        p.name === permissionName && 
        p.roles.includes(userStore.getUserInfo.roleId)
      )
    }
  }
})