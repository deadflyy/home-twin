<template>
  <view class="container">
    <view class="room-header" :style="{ backgroundColor: room.color }">
      <view class="room-info">
        <text class="room-icon">{{ room.icon }}</text>
        <view class="room-title-wrap">
          <text class="room-name">{{ room.name }}</text>
          <text class="room-desc">共{{ items.length }}件物品</text>
        </view>
      </view>
    </view>

    <view class="filter-section">
      <scroll-view scroll-x class="filter-scroll">
        <view class="filter-list">
          <view 
            v-for="person in persons" 
            :key="person.id" 
            class="filter-item"
            :class="{ active: selectedPerson === person.id }"
            @click="selectPerson(person.id)"
          >
            <text>{{ person.avatar }} {{ person.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="add-btn" @click="goToAddItem">
      <text class="add-icon">+</text>
      <text class="add-text">添加物品</text>
    </view>

    <view v-if="items.length > 0" class="item-list">
      <view 
        v-for="item in items" 
        :key="item.id" 
        class="item-card"
        @click="goToDetail(item.id)"
      >
        <view class="item-photo-wrap">
          <image 
            v-if="item.photo" 
            :src="item.photo" 
            class="item-photo"
            mode="aspectFill"
          />
          <view v-else class="item-placeholder">
            <text class="placeholder-icon">📦</text>
          </view>
        </view>
        <view class="item-info">
          <text class="item-name">{{ item.name }}</text>
          <text v-if="item.personName" class="item-person">{{ item.personName }}</text>
          <text v-if="item.location" class="item-location">📍 {{ item.location }}</text>
        </view>
        <view class="item-status" :class="item.status">
          {{ item.status === 'active' ? '使用中' : '已清理' }}
        </view>
      </view>
    </view>

    <view v-else class="empty-state">
      <text class="empty-icon">📭</text>
      <text class="empty-text">该房间暂无物品</text>
      <view class="empty-btn" @click="goToAddItem">
        <text>添加第一件物品</text>
      </view>
    </view>

    <view v-if="hasMore" class="load-more">
      <text @click="loadMore">加载更多</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getRoomById, getPersons, getItems, type Room, type Person, type Item, type PageResponse } from '@/api'

const roomId = ref('')
const room = ref<Room>({ id: '', name: '', icon: '', color: '#4A90D9', itemCount: 0 })
const persons = ref<Person[]>([])
const items = ref<Item[]>([])
const selectedPerson = ref('')
const page = ref(0)
const size = 10
const hasMore = ref(true)

onLoad((options) => {
  if (options?.id) {
    roomId.value = options.id as string
  }
})

onMounted(async () => {
  await loadRoom()
  await loadPersons()
  await loadItems()
})

async function loadRoom() {
  try {
    room.value = await getRoomById(roomId.value)
    uni.setNavigationBarTitle({ title: room.value.name })
  } catch (error) {
    console.error('加载房间失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

async function loadPersons() {
  try {
    persons.value = await getPersons()
  } catch (error) {
    console.error('加载人物失败:', error)
  }
}

async function loadItems(isLoadMore = false) {
  try {
    const currentPage = isLoadMore ? page.value + 1 : 0
    const params: Record<string, unknown> = { page: currentPage, size }
    if (roomId.value) params.roomId = roomId.value
    if (selectedPerson.value) params.personId = selectedPerson.value

    const res: PageResponse<Item> = await getItems(roomId.value, selectedPerson.value, currentPage, size)
    
    if (isLoadMore) {
      items.value = [...items.value, ...res.content]
    } else {
      items.value = res.content
    }
    
    page.value = currentPage
    hasMore.value = currentPage < res.totalPages - 1
  } catch (error) {
    console.error('加载物品失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function selectPerson(personId: string) {
  selectedPerson.value = selectedPerson.value === personId ? '' : personId
  page.value = 0
  hasMore.value = true
  loadItems()
}

function loadMore() {
  loadItems(true)
}

function goToAddItem() {
  uni.navigateTo({
    url: `/pages/item/add?roomId=${roomId.value}`
  })
}

function goToDetail(itemId: string) {
  uni.navigateTo({
    url: `/pages/item/detail?id=${itemId}`
  })
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
}

.room-header {
  padding: 120rpx 32rpx 40rpx;
  display: flex;
  align-items: center;
}

.room-info {
  display: flex;
  align-items: center;
}

.room-icon {
  font-size: 72rpx;
  margin-right: 24rpx;
}

.room-title-wrap {
  display: flex;
  flex-direction: column;
}

.room-name {
  font-size: 44rpx;
  font-weight: bold;
  color: #ffffff;
}

.room-desc {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 8rpx;
}

.filter-section {
  background: #ffffff;
  padding: 20rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.filter-scroll {
  white-space: nowrap;
}

.filter-list {
  display: inline-flex;
  gap: 16rpx;
}

.filter-item {
  padding: 16rpx 28rpx;
  background: #f5f5f5;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #666666;
  border: 2rpx solid transparent;
  transition: all 0.3s;

  &.active {
    background: #e8f0fe;
    color: #4A90D9;
    border-color: #4A90D9;
  }
}

.add-btn {
  background: #ffffff;
  margin: 20rpx 32rpx;
  padding: 28rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.add-icon {
  font-size: 40rpx;
  color: #4A90D9;
  margin-right: 12rpx;
}

.add-text {
  font-size: 32rpx;
  color: #4A90D9;
  font-weight: 500;
}

.item-list {
  padding: 0 32rpx;
  padding-bottom: 40rpx;
}

.item-card {
  display: flex;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.item-photo-wrap {
  width: 140rpx;
  height: 140rpx;
  border-radius: 12rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.item-photo {
  width: 100%;
  height: 100%;
}

.item-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48rpx;
}

.item-info {
  flex: 1;
  padding: 0 24rpx;
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333333;
}

.item-person {
  font-size: 24rpx;
  color: #4A90D9;
  margin-top: 8rpx;
}

.item-location {
  font-size: 24rpx;
  color: #999999;
  margin-top: 4rpx;
}

.item-status {
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  align-self: flex-start;

  &.active {
    background: #e8f0fe;
    color: #4A90D9;
  }

  &.cleaned {
    background: #f0f9eb;
    color: #67C23A;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999999;
  margin-bottom: 32rpx;
}

.empty-btn {
  padding: 24rpx 64rpx;
  background: #4A90D9;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #ffffff;
}

.load-more {
  text-align: center;
  padding: 32rpx;
  font-size: 28rpx;
  color: #999999;
}
</style>
