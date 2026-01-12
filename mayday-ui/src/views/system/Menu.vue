<template>
  <div class="page-container">
    <a-card title="菜单管理">
      <template #extra>
        <a-button type="primary" @click="handleAdd()">
          <template #icon><PlusOutlined /></template>
          新增
        </a-button>
      </template>

      <a-table 
        :columns="columns" 
        :dataSource="menuList" 
        :loading="loading" 
        rowKey="id"
        :defaultExpandAllRows="true"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'icon'">
            <component :is="getIcon(record.icon)" v-if="record.icon && record.icon !== '#'" />
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'menuType'">
            <a-tag :color="getTypeColor(record.menuType)">
              {{ getTypeText(record.menuType) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">
              {{ record.status === '0' ? '正常' : '停用' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleAdd(record.id)">新增</a-button>
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-popconfirm title="确定删除？" @confirm="handleDelete(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal v-model:open="formVisible" :title="formTitle" @ok="handleSubmit" :confirmLoading="submitting" width="600px">
      <a-form :model="formData" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="菜单类型">
              <a-radio-group v-model:value="formData.menuType">
                <a-radio value="M">目录</a-radio>
                <a-radio value="C">菜单</a-radio>
                <a-radio value="F">按钮</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="菜单名称">
              <a-input v-model:value="formData.menuName" placeholder="如：用户管理" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="路由地址" v-if="formData.menuType !== 'F'">
              <a-input v-model:value="formData.path" placeholder="如：user" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="组件路径" v-if="formData.menuType === 'C'">
              <a-input v-model:value="formData.component" placeholder="如：system/User" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="权限标识">
              <a-input v-model:value="formData.perms" placeholder="如：system:user:list" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="图标" v-if="formData.menuType !== 'F'">
              <a-input v-model:value="formData.icon" placeholder="如：UserOutlined" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="排序">
              <a-input-number v-model:value="formData.orderNum" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态">
              <a-radio-group v-model:value="formData.status">
                <a-radio value="0">正常</a-radio>
                <a-radio value="1">停用</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { PlusOutlined, SettingOutlined, UserOutlined, TeamOutlined, ApartmentOutlined, MenuOutlined, DashboardOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import request from '../../utils/request'

const loading = ref(false)
const submitting = ref(false)
const menuList = ref<any[]>([])

// 新增/编辑表单
const formVisible = ref(false)
const formData = ref<any>({})
const formTitle = computed(() => formData.value.id ? '编辑菜单' : '新增菜单')

const columns = [
  { title: '菜单名称', dataIndex: 'menuName', key: 'menuName', width: 200 },
  { title: '图标', dataIndex: 'icon', key: 'icon', width: 60 },
  { title: '类型', dataIndex: 'menuType', key: 'menuType', width: 80 },
  { title: '路由', dataIndex: 'path', key: 'path', width: 120 },
  { title: '权限标识', dataIndex: 'perms', key: 'perms', width: 150 },
  { title: '排序', dataIndex: 'orderNum', key: 'orderNum', width: 60 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '操作', key: 'action', width: 180 },
]

const iconMap: Record<string, any> = {
  PlusOutlined, SettingOutlined, UserOutlined, TeamOutlined, 
  ApartmentOutlined, MenuOutlined, DashboardOutlined, AppstoreOutlined
}

const getIcon = (iconName: string) => iconMap[iconName] || AppstoreOutlined

const getTypeText = (type: string) => {
  const map: Record<string, string> = { 'M': '目录', 'C': '菜单', 'F': '按钮' }
  return map[type] || '未知'
}

const getTypeColor = (type: string) => {
  const map: Record<string, string> = { 'M': 'blue', 'C': 'green', 'F': 'orange' }
  return map[type] || 'default'
}

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/menu/list')
    menuList.value = res || []
  } catch (e) {
    console.error('加载失败:', e)
  } finally {
    loading.value = false
  }
}

const handleAdd = (parentId?: number) => {
  formData.value = { 
    parentId: parentId || 0, 
    menuType: 'C', 
    orderNum: 0, 
    status: '0' 
  }
  formVisible.value = true
}

const handleEdit = (record: any) => {
  formData.value = { ...record }
  formVisible.value = true
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (formData.value.id) {
      await request.put(`/menu/${formData.value.id}`, formData.value)
      message.success('更新成功')
    } else {
      await request.post('/menu', formData.value)
      message.success('新增成功')
    }
    formVisible.value = false
    await loadData()
  } catch (e: any) {
    message.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await request.delete(`/menu/${id}`)
    message.success('删除成功')
    await loadData()
  } catch (e: any) {
    message.error(e.message || '删除失败')
  }
}
</script>

<style scoped>
.page-container { padding: 8px; }
</style>
