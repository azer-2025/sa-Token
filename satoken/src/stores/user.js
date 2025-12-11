import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('satoken') || '',
    userInfo: {},
    isLoggedIn: false
  }),

  getters: {
    getToken: (state) => state.token,
    getUserInfo: (state) => state.userInfo,
    getIsLoggedIn: (state) => state.isLoggedIn
  },

  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('satoken', token)
      axios.defaults.headers.common['satoken'] = token
    },

    clearToken() {
      this.token = ''
      this.userInfo = {}
      this.isLoggedIn = false
      localStorage.removeItem('satoken')
      delete axios.defaults.headers.common['satoken']
    },

    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.isLoggedIn = true
    },

    async login(loginData) {
      try {
        const response = await axios.post('/auth/login', loginData)
        console.log(response);
        const { code, data, msg } = response.data
        if (code === 200) {
          this.setToken(data.satoken)
          this.setUserInfo({
            userId: data.userId,
            username: data.username,
            roleId: data.roleId
          })
          return { success: true, data }
        } else {
          return { success: false, message: msg }
        }
      } catch (error) {
        return { success: false, message: error.message }
      }
    },

    async logout() {
      try {
        await axios.post('/auth/logout')
        this.clearToken()
        return { success: true }
      } catch (error) {
        this.clearToken()
        return { success: false, message: error.message }
      }
    },

    async fetchUserInfo() {
      try {
        const response = await axios.get('/auth/info')
        const { code, data } = response.data

        if (code === 200) {
          this.setUserInfo(data)
          return { success: true, data }
        } else {
          this.clearToken()
          return { success: false, message: '获取用户信息失败' }
        }
      } catch (error) {
        this.clearToken()
        return { success: false, message: error.message }
      }
    }
  }
})