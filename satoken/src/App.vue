```
<script setup>
import { RouterView } from 'vue-router'
import { useUserStore } from './stores/user'
import { onMounted } from 'vue'

const userStore = useUserStore()

// 页面加载时检查用户登录状态
onMounted(() => {
  // 如果有token，尝试获取用户信息
  const token = localStorage.getItem('satoken')
  if (token) {
    userStore.fetchUserInfo()
  }
})
</script>

<template>
  <div id="app">
    <RouterView /> 
  </div>
</template>

<style>
#app {
  height: 100vh;
  margin: 0;
  padding: 0;
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

* {
  box-sizing: border-box;
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}
</style>
```