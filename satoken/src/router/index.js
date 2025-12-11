import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Home from '../views/Home.vue'
import UserProfile from '../views/UserProfile.vue'
import UserManagement from '../views/UserManagement.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  {
    path: '/dashboard',
    component: Dashboard,
    children: [
      { path: '', component: Home },
      { path: 'home', component: Home },
      { path: 'users', component: UserManagement },
      { path: 'profile', component: UserProfile }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('satoken')
  console.log(token);
  // 如果访问的是登录页，直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 如果没有token且不是去登录页，则跳转到登录页
  if (!token) {
    next('/login')
    return
  }
  
  // 其他情况正常放行
  next()
})

export default router