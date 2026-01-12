<template>
  <div class="page-container">
    <a-card title="部门管理">
      <template #extra>
        <a-button type="primary">
          <template #icon><PlusOutlined /></template>
          新增
        </a-button>
      </template>

      <a-table 
        :columns="columns" 
        :dataSource="deptList" 
        rowKey="id"
        :defaultExpandAllRows="true"
        :loading="loading"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small">编辑</a-button>
              <a-button type="link" size="small">新增</a-button>
              <a-button type="link" size="small" danger>删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import request from '../../utils/request'

interface DeptItem {
  id: number
  deptName: string
  orderNum: number
  children?: DeptItem[]
}

const deptList = ref<DeptItem[]>([])
const loading = ref(false)

const columns = [
  { title: '部门名称', dataIndex: 'deptName', key: 'deptName' },
  { title: '排序', dataIndex: 'orderNum', key: 'orderNum', width: 80 },
  { title: '操作', key: 'action', width: 200 },
]

/**
 * 加载部门列表
 */
const loadDepts = async () => {
  loading.value = true
  try {
    const res = await request.get('/system/dept/list')
    deptList.value = (res as any) || []
  } catch (error) {
    console.error('加载部门列表失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDepts()
})
</script>

<style scoped>
.page-container { padding: 8px; }
</style>

