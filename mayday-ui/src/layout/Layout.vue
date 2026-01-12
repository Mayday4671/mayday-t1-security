<template>
  <a-layout class="layout-container">
    <!-- 左侧菜单 -->
    <a-layout-sider 
      v-model:collapsed="collapsed" 
      collapsible 
      :trigger="null"
      class="layout-sider"
    >
      <div class="logo">
        <span v-if="!collapsed">🔐 MayDay</span>
        <span v-else>🔐</span>
      </div>
      <SideMenu :menus="menus" :collapsed="collapsed" />
    </a-layout-sider>

    <a-layout>
      <!-- 顶部导航 -->
      <a-layout-header class="layout-header">
        <div class="header-left">
          <menu-unfold-outlined v-if="collapsed" class="trigger" @click="collapsed = false" />
          <menu-fold-outlined v-else class="trigger" @click="collapsed = true" />
          <a-breadcrumb style="margin-left: 16px">
            <a-breadcrumb-item v-for="item in breadcrumbs" :key="item">{{ item }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-space :size="16">
            <!-- 部门切换 -->
            <a-select 
              v-if="deptList.length > 1" 
              v-model:value="currentDeptId" 
              @change="handleSwitchDept"
              style="width: 140px"
              :loading="switching"
            >
              <a-select-option v-for="dept in deptList" :key="dept.deptId" :value="dept.deptId">
                {{ dept.deptName }}
              </a-select-option>
            </a-select>
            <a-tag v-else-if="currentDeptName" color="blue">{{ currentDeptName }}</a-tag>
            
            <!-- 用户下拉菜单 -->
            <a-dropdown>
              <a-space class="user-dropdown">
                <a-avatar :size="28" style="background-color: #1890ff">
                  {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
                </a-avatar>
                <span>{{ userInfo?.username }}</span>
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="profile">
                    <UserOutlined /> 个人中心
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <LogoutOutlined /> 退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </div>
      </a-layout-header>

      <!-- 标签页导航 -->
      <div class="tabs-wrapper">
        <a-tabs
          v-model:activeKey="activeKey"
          type="editable-card"
          hide-add
          size="small"
          @edit="onTabEdit"
          @change="onTabChange"
        >
          <a-tab-pane
            v-for="tab in tabs"
            :key="tab.path"
            :closable="tab.closable"
          >
            <template #tab>
              <a-dropdown :trigger="['contextmenu']">
                <span>{{ tab.title }}</span>
                <template #overlay>
                  <a-menu @click="({ key }: { key: any }) => handleContextMenu(tab.path, key as string)">
                    <a-menu-item key="current" :disabled="!tab.closable">关闭当前</a-menu-item>
                    <a-menu-item 
                      key="left" 
                      :disabled="isFirstClosable(tab.path)"
                    >关闭左侧</a-menu-item>
                    <a-menu-item 
                      key="right" 
                      :disabled="isLastTab(tab.path)"
                    >关闭右侧</a-menu-item>
                    <a-menu-item key="others">关闭其他</a-menu-item>
                    <a-menu-item key="all">关闭全部</a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </template>
          </a-tab-pane>
        </a-tabs>
      </div>

      <!-- 主内容区 -->
      <a-layout-content class="layout-content">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" :key="route.fullPath" />
          </keep-alive>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  MenuUnfoldOutlined, 
  MenuFoldOutlined, 
  UserOutlined, 
  LogoutOutlined 
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import SideMenu from './SideMenu.vue'
import { useTabs } from '../store/useTabs'
import { getInfo, logout, switchDept, type LoginResult, type DeptOption } from '../api/auth'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()

const collapsed = ref(false)
const switching = ref(false)
const menus = ref<any[]>([])
const userInfo = ref<LoginResult>()
const deptList = ref<DeptOption[]>([])
const currentDeptId = ref<number>()

// 当前部门名称
const currentDeptName = computed(() => {
  if (!currentDeptId.value) return ''
  const dept = deptList.value.find(d => d.deptId === currentDeptId.value)
  return dept?.deptName || `部门 ${currentDeptId.value}`
})

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(r => r.meta?.title)
  return matched.map(r => r.meta.title as string)
})

onMounted(async () => {
  // 从 localStorage 读取部门列表
  const savedDeptList = localStorage.getItem('deptList')
  if (savedDeptList) {
    try {
      deptList.value = JSON.parse(savedDeptList)
    } catch (e) {}
  }
  
  await loadUserInfo()
  await loadMenus()
})

const loadUserInfo = async () => {
  try {
    const res = await getInfo()
    userInfo.value = res
    currentDeptId.value = res.currentDeptId
  } catch (e) {
    router.push('/login')
  }
}

const loadMenus = async () => {
  try {
    const res = await request.get('/menu/getRouters')
    menus.value = (res as any) || []
  } catch (e) {
    console.error('加载菜单失败:', e)
  }
}

// 标签页逻辑
const { 
  tabs, activeKey, addTab, removeTab, 
  closeOthers, closeLeft, closeRight, closeAll, resetTabs 
} = useTabs()

// 监听路由变化添加标签
watch(() => route.fullPath, () => {
  addTab(route)
}, { immediate: true })

const onTabChange = (key: string) => {
  router.push(key)
}

const onTabEdit = (targetKey: any, action: string) => {
  if (action === 'remove') {
    removeTab(targetKey as string)
  }
}

const handleContextMenu = (path: string, key: string) => {
  switch (key) {
    case 'current': removeTab(path); break
    case 'others': closeOthers(path); break
    case 'left': closeLeft(path); break
    case 'right': closeRight(path); break
    case 'all': closeAll(); break
  }
}

// 判断是否为第一个可关闭标签（或首页）
const isFirstClosable = (path: string) => {
  const index = tabs.value.findIndex(t => t.path === path)
  // 如果是首页，或者左侧没有除首页外的标签
  return index <= 1
}

// 判断是否为最后一个标签
const isLastTab = (path: string) => {
  const index = tabs.value.findIndex(t => t.path === path)
  return index === tabs.value.length - 1
}

// 切换部门
const handleSwitchDept = async (deptId: number) => {
  switching.value = true
  try {
    const res = await switchDept(deptId)
    localStorage.setItem('token', res.token!)
    await loadUserInfo()
    await loadMenus()
    message.success(`已切换到 ${currentDeptName.value}`)
    // 刷新当前页面
    router.go(0)
  } catch (e: any) {
    message.error(e.message || '切换部门失败')
    currentDeptId.value = userInfo.value?.currentDeptId
  } finally {
    switching.value = false
  }
}

const handleLogout = async () => {
  try {
    await logout()
  } catch (e) {}
  localStorage.removeItem('token')
  localStorage.removeItem('deptList')
  resetTabs() // 清理标签页
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.layout-sider {
  background: #001529;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.1);
}

.layout-header {
  background: white;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
}

.layout-content {
  margin: 0 16px 16px;
  padding: 24px;
  background: white;
  min-height: 280px;
  border-radius: 8px;
}

.tabs-wrapper {
  background: #f0f2f5;
  padding: 6px 16px 0;
}

:deep(.ant-tabs-nav) {
  margin: 0 !important;
}

:deep(.ant-tabs-card > .ant-tabs-nav .ant-tabs-tab) {
  border: none !important;
  background: white !important;
  transition: all 0.3s;
}

:deep(.ant-tabs-card > .ant-tabs-nav .ant-tabs-tab-active) {
  background: #e6f7ff !important;
  color: #1890ff !important;
}
</style>
