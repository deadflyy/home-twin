<template>
  <view class="container">
    <view class="header">
      <view class="header-title">
        <text class="title">🏠 家庭数字孪生</text>
        <text class="subtitle">管理家中的每一件物品</text>
      </view>
    </view>

    <view class="person-section card">
      <view class="section-header">
        <text class="section-title">人物分类</text>
      </view>
      <scroll-view scroll-x class="person-scroll">
        <view class="person-list">
          <view 
            v-for="person in persons" 
            :key="person.id" 
            class="person-card"
            :class="{ active: selectedPerson === person.id }"
            @click="selectPerson(person.id)"
          >
            <text class="person-avatar">{{ person.avatar }}</text>
            <text class="person-name">{{ person.name }}</text>
            <text class="person-count">{{ person.itemCount }}件</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="room-section">
      <view class="section-header">
        <text class="section-title">房间列表</text>
        <text class="section-subtitle">共{{ rooms.length }}个房间</text>
      </view>
      <view class="room-grid">
        <view 
          v-for="room in rooms" 
          :key="room.id" 
          class="room-card"
          @click="goToRoom(room.id)"
        >
          <view class="room-icon" :style="{ backgroundColor: room.color + '20' }">
            <text class="icon-text">{{ room.icon }}</text>
          </view>
          <view class="room-info">
            <text class="room-name">{{ room.name }}</text>
            <text class="room-count">{{ room.itemCount }}件物品</text>
          </view>
          <view class="room-arrow">›</view>
        </view>
      </view>
    </view>

    <view class="quick-actions card">
      <view class="section-header">
        <text class="section-title">快捷操作</text>
      </view>
      <view class="action-grid">
        <view class="action-item" @click="goToAddItem">
          <view class="action-icon">📷</view>
          <text class="action-text">拍照添加</text>
        </view>
        <view class="action-item" @click="goToAddItem">
          <view class="action-icon">✏️</view>
          <text class="action-text">手动添加</text>
        </view>
        <view class="action-item" @click="goToSearch">
          <view class="action-icon">🔍</view>
          <text class="action-text">查找物品</text>
        </view>
        <view class="action-item" @click="goToStatistics">
          <view class="action-icon">📊</view>
          <text class="action-text">统计分析</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRooms, getPersons, type Room, type Person } from '@/api'

const rooms = ref<Room[]>([])
const persons = ref<Person[]>([])
const selectedPerson = ref<string>('')

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    rooms.value = await getRooms()
    persons.value = await getPersons()
  } catch (error) {
    console.error('加载数据失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function selectPerson(personId: string) {
  selectedPerson.value = selectedPerson.value === personId ? '' : personId
}

function goToRoom(roomId: string) {
  uni.navigateTo({
    url: `/pages/room/room?id=${roomId}`
  })
}

function goToAddItem() {
  uni.navigateTo({
    url: '/pages/item/add'
  })
}

function goToSearch() {
  uni.switchTab({
    url: '/pages/item/search'
  })
}

function goToStatistics() {
  uni.switchTab({
    url: '/pages/statistics/statistics'
  })
}
</script>

<style lang="scss" scoped>
.container {
  padding: 0 32rpx;
  padding-bottom: 180rpx;
  background: linear-gradient(180deg, #4A90D9 0%, #6BA3E0 30%, #f5f5f5 30%);
  min-height: 100vh;
}

.header {
  padding: 120rpx 0 80rpx;
}

.header-title {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 56rpx;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 12rpx;
}

.person-section {
  margin-top: -40rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333333;
}

.section-subtitle {
  font-size: 24rpx;
  color: #999999;
}

.person-scroll {
  white-space: nowrap;
}

.person-list {
  display: inline-flex;
  gap: 20rpx;
}

.person-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 32rpx;
  background: #f8f9fa;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s;

  &.active {
    background: #e8f0fe;
    border-color: #4A90D9;
  }
}

.person-avatar {
  font-size: 56rpx;
  margin-bottom: 8rpx;
}

.person-name {
  font-size: 26rpx;
  color: #333333;
}

.person-count {
  font-size: 22rpx;
  color: #999999;
  margin-top: 4rpx;
}

.room-section {
  margin-top: 24rpx;
}

.room-grid {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.room-card {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.room-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.icon-text {
  font-size: 48rpx;
}

.room-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.room-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333333;
}

.room-count {
  font-size: 24rpx;
  color: #999999;
  margin-top: 4rpx;
}

.room-arrow {
  font-size: 40rpx;
  color: #cccccc;
}

.quick-actions {
  margin-top: 24rpx;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 0;
  background: #f8f9fa;
  border-radius: 16rpx;
}

.action-icon {
  font-size: 48rpx;
  margin-bottom: 12rpx;
}

.action-text {
  font-size: 24rpx;
  color: #666666;
}
</style>
