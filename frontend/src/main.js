import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import UserList from './components/UserList.vue'
import UserDetail from './components/UserDetail.vue'
import Exchange from './components/Exchange.vue'
import RiskControl from './components/RiskControl.vue'

const routes = [
  { path: '/', component: UserList },
  { path: '/user/:id', component: UserDetail },
  { path: '/exchange', component: Exchange },
  { path: '/risk-control', component: RiskControl }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
