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
        <text class="section-title">户型图</text>
        <text class="section-subtitle">点击房间查看物品</text>
      </view>

      <!-- 户型图精确布局 -->
      <view class="floor-plan-wrapper">
        <view class="floor-plan">
          <!-- 厨房 L型: 左上区域 -->
          <view class="room kitchen-top" @click="goToRoom('room-005')">
            <view class="room-content">
              <text class="room-icon-text">🍳</text>
              <text class="room-name-text">厨房</text>
              <text class="room-area">8.4m²</text>
              <text class="room-items">{{ getRoomItemCount('room-005') }}件</text>
            </view>
          </view>
          <view class="room kitchen-bottom" @click="goToRoom('room-005')">
            <view class="room-content">
              <text class="room-icon-text">🍳</text>
            </view>
          </view>

          <!-- 卫生间: 左中 -->
          <view class="room bathroom" @click="goToRoom('room-006')">
            <view class="room-content">
              <text class="room-icon-text">🚽</text>
              <text class="room-name-text">卫生间</text>
              <text class="room-area">4.4m²</text>
              <text class="room-items">{{ getRoomItemCount('room-006') }}件</text>
            </view>
          </view>

          <!-- 主卧(卧室B): 左下 -->
          <view class="room bedroom-b" @click="goToRoom('room-001')">
            <view class="room-content">
              <text class="room-icon-text">🏠</text>
              <text class="room-name-text">主卧</text>
              <text class="room-area">13.8m²</text>
              <text class="room-items">{{ getRoomItemCount('room-001') }}件</text>
            </view>
          </view>

          <!-- 阳台: 最下方 -->
          <view class="room balcony" @click="goToRoom('room-007')">
            <view class="room-content">
              <text class="room-icon-text">🌳</text>
              <text class="room-name-text">阳台</text>
              <text class="room-area">8.5m²</text>
              <text class="room-items">{{ getRoomItemCount('room-007') }}件</text>
            </view>
          </view>

          <!-- 入口/过道 -->
          <view class="hallway">
            <text class="hallway-text">入口</text>
          </view>

          <!-- 餐厅: 中上 -->
          <view class="room dining" @click="goToRoom('room-004')">
            <view class="room-content">
              <text class="room-icon-text">🥘</text>
              <text class="room-name-text">餐厅</text>
              <text class="room-area">9.2m²</text>
              <text class="room-items">{{ getRoomItemCount('room-004') }}件</text>
            </view>
          </view>

          <!-- 客厅: 中下 -->
          <view class="room living" @click="goToRoom('room-003')">
            <view class="room-content">
              <text class="room-icon-text">🛋️</text>
              <text class="room-name-text">客厅</text>
              <text class="room-area">16.6m²</text>
              <text class="room-items">{{ getRoomItemCount('room-003') }}件</text>
            </view>
          </view>

          <!-- 次卧(卧室A): 右侧 -->
          <view class="room bedroom-a" @click="goToRoom('room-002')">
            <view class="room-content">
              <text class="room-icon-text">🛏️</text>
              <text class="room-name-text">次卧</text>
              <text class="room-area">10.4m²</text>
              <text class="room-items">{{ getRoomItemCount('room-002') }}件</text>
            </view>
          </view>
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
import { ref, onMounted, computed } from 'vue'
import { getRooms, getPersons, type Room, type Person } from '@/api'

const rooms = ref<Room[]>([])
const persons = ref<Person[]>([])
const selectedPerson = ref<string>('')

const roomMap = computed(() => {
  const map: Record<string, Room> = {}
  rooms.value.forEach(room => {
    map[room.id] = room
  })
  return map
})

function getRoomItemCount(roomId: string): number {
  return roomMap.value[roomId]?.itemCount || 0
}

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
  padding: 80rpx 0 60rpx;
}

.header-title {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 8rpx;
}

.card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.person-section {
  margin-top: -30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}

.section-subtitle {
  font-size: 22rpx;
  color: #999999;
}

.person-scroll {
  white-space: nowrap;
}

.person-list {
  display: inline-flex;
  gap: 16rpx;
}

.person-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 28rpx;
  background: #f8f9fa;
  border-radius: 14rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s;

  &.active {
    background: #e8f0fe;
    border-color: #4A90D9;
  }
}

.person-avatar {
  font-size: 48rpx;
  margin-bottom: 6rpx;
}

.person-name {
  font-size: 24rpx;
  color: #333333;
}

.person-count {
  font-size: 20rpx;
  color: #999999;
  margin-top: 4rpx;
}

.room-section {
  margin-top: 20rpx;
}

/* 户型图精确布局 */
.floor-plan-wrapper {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 16rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.floor-plan {
  display: grid;
  /* 5列: 厨房上/厨房下 | 卫生间/主卧 | 入口 | 餐厅/客厅 | 次卧 */
  grid-template-columns: 1fr 0.8fr 0.4fr 1.2fr 0.8fr;
  /* 4行 */
  grid-template-rows: 1fr 0.7fr 1.3fr 0.6fr;
  gap: 3rpx;
  background: #f0f0f0;
  border-radius: 16rpx;
  overflow: hidden;
  aspect-ratio: 1 / 1.1;
  max-height: 70vh;
}

.room {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  border-radius: 10rpx;

  &:active {
    filter: brightness(0.95);
    transform: scale(0.98);
  }
}

.room-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6rpx;
  text-align: center;
}

.room-icon-text {
  font-size: 32rpx;
  margin-bottom: 2rpx;
}

.room-name-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #ffffff;
  line-height: 1.2;
}

.room-area {
  font-size: 18rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 2rpx;
}

.room-items {
  font-size: 16rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 2rpx;
  background: rgba(0, 0, 0, 0.15);
  padding: 2rpx 10rpx;
  border-radius: 10rpx;
}

/* 厨房 L型 - 上下翻转 */
/* 厨房上部分 */
.kitchen-top {
  grid-column: 1;
  grid-row: 1;
  background: linear-gradient(135deg, #E8D5B7 0%, #D4C4A8 100%);
  border-radius: 10rpx 10rpx 4rpx 10rpx;
}

/* 厨房下部分 (L型的短边) */
.kitchen-bottom {
  grid-column: 1;
  grid-row: 2;
  background: linear-gradient(135deg, #E8D5B7 0%, #D4C4A8 100%);
  border-radius: 4rpx 10rpx 10rpx 10rpx;
}

/* 卫生间 */
.bathroom {
  grid-column: 2;
  grid-row: 2;
  background: linear-gradient(135deg, #B8D4E3 0%, #A8C8D8 100%);
}

/* 主卧(卧室B) */
.bedroom-b {
  grid-column: 1 / 3;
  grid-row: 3;
  background: linear-gradient(135deg, #C9B8D4 0%, #B8A8C8 100%);
}

/* 阳台 */
.balcony {
  grid-column: 1 / -1;
  grid-row: 4;
  background: linear-gradient(135deg, #B8DCC8 0%, #A8D0B8 100%);
}

/* 入口/过道 */
.hallway {
  grid-column: 3;
  grid-row: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f8f8;
  border-radius: 8rpx;
}

.hallway-text {
  font-size: 18rpx;
  color: #bbbbbb;
}

/* 餐厅 */
.dining {
  grid-column: 4;
  grid-row: 1;
  background: linear-gradient(135deg, #F0D5A8 0%, #E8C898 100%);
}

/* 客厅 */
.living {
  grid-column: 3 / 5;
  grid-row: 2 / 4;
  background: linear-gradient(135deg, #D4E5F7 0%, #C8D8F0 100%);
}

/* 次卧(卧室A) */
.bedroom-a {
  grid-column: 5;
  grid-row: 1 / 3;
  background: linear-gradient(135deg, #E8D8C8 0%, #D8C8B8 100%);
}

.quick-actions {
  margin-top: 20rpx;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
  background: #f8f9fa;
  border-radius: 14rpx;

  &:active {
    background: #e8f0fe;
  }
}

.action-icon {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.action-text {
  font-size: 22rpx;
  color: #666666;
}
</style>
