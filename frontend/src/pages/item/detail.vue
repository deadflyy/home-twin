<template>
  <view class="container">
    <view v-if="item" class="detail-content">
      <view class="photo-section">
        <image v-if="item.photo" :src="item.photo" class="item-photo" mode="aspectFill" />
        <view v-else class="photo-placeholder">
          <text class="placeholder-icon">📦</text>
        </view>
      </view>

      <view class="info-section card">
        <view class="info-header">
          <text class="item-name">{{ item.name }}</text>
          <view class="item-status" :class="item.status">
            {{ item.status === 'active' ? '使用中' : '已清理' }}
          </view>
        </view>

        <view class="info-row">
          <text class="info-label">📍 所属房间</text>
          <text class="info-value">{{ item.roomName }}</text>
        </view>

        <view v-if="item.personName" class="info-row">
          <text class="info-label">👤 所属人物</text>
          <text class="info-value">{{ item.personName }}</text>
        </view>

        <view v-if="item.location" class="info-row">
          <text class="info-label">🏠 具体位置</text>
          <text class="info-value">{{ item.location }}</text>
        </view>

        <view v-if="item.description" class="info-row description">
          <text class="info-label">📝 物品描述</text>
          <text class="info-value">{{ item.description }}</text>
        </view>

        <view class="info-row">
          <text class="info-label">⏰ 添加时间</text>
          <text class="info-value">{{ formatDate(item.addedAt) }}</text>
        </view>

        <view v-if="item.cleanedAt" class="info-row">
          <text class="info-label">✅ 清理时间</text>
          <text class="info-value">{{ formatDate(item.cleanedAt) }}</text>
        </view>
      </view>
    </view>

    <view class="action-section">
      <view 
        v-if="item?.status === 'active'" 
        class="action-btn clean-btn"
        @click="handleClean"
      >
        <text>🗑️ 标记为已清理</text>
      </view>
      <view class="action-btn edit-btn" @click="handleEdit">
        <text>✏️ 编辑物品</text>
      </view>
      <view class="action-btn delete-btn" @click="handleDelete">
        <text>🗑️ 删除物品</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getItemById, cleanItem, deleteItem, type Item } from '@/api'

const itemId = ref('')
const item = ref<Item | null>(null)

onLoad((options) => {
  if (options?.id) {
    itemId.value = options.id as string
  }
})

onMounted(async () => {
  await loadItem()
})

async function loadItem() {
  try {
    item.value = await getItemById(itemId.value)
  } catch (error) {
    console.error('加载物品失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

async function handleClean() {
  try {
    uni.showLoading({ title: '处理中...' })
    await cleanItem(itemId.value)
    uni.hideLoading()
    uni.showToast({ title: '标记成功', icon: 'success' })
    await loadItem()
  } catch (error) {
    uni.hideLoading()
    console.error('清理失败:', error)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

function handleEdit() {
  uni.navigateTo({
    url: `/pages/item/add?id=${itemId.value}`
  })
}

async function handleDelete() {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个物品吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '删除中...' })
          await deleteItem(itemId.value)
          uni.hideLoading()
          uni.showToast({ title: '删除成功', icon: 'success' })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } catch (error) {
          uni.hideLoading()
          console.error('删除失败:', error)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
}

.detail-content {
  padding-bottom: 200rpx;
}

.photo-section {
  width: 100%;
  height: 500rpx;
  background: #ffffff;
}

.item-photo {
  width: 100%;
  height: 100%;
}

.photo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

.placeholder-icon {
  font-size: 120rpx;
}

.card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 20rpx;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.item-name {
  font-size: 40rpx;
  font-weight: bold;
  color: #333333;
}

.item-status {
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
  font-size: 24rpx;

  &.active {
    background: #e8f0fe;
    color: #4A90D9;
  }

  &.cleaned {
    background: #f0f9eb;
    color: #67C23A;
  }
}

.info-row {
  display: flex;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }

  &.description {
    flex-direction: column;
  }
}

.info-label {
  font-size: 28rpx;
  color: #999999;
  flex-shrink: 0;
}

.info-value {
  font-size: 28rpx;
  color: #333333;
  margin-left: 16rpx;
  flex: 1;

  .description & {
    margin-left: 0;
    margin-top: 8rpx;
  }
}

.action-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
  background: #ffffff;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  padding: 28rpx;
  border-radius: 12rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.clean-btn {
  background: linear-gradient(135deg, #4A90D9 0%, #6BA3E0 100%);
  color: #ffffff;
}

.edit-btn {
  background: #f5f5f5;
  color: #666666;
}

.delete-btn {
  background: #fff2f0;
  color: #f56c6c;
}
</style>
