<template>
  <view class="container">
    <view class="photo-section card">
      <view class="section-header">
        <text class="section-title">物品照片</text>
      </view>
      <view class="photo-upload" @click="chooseImage">
        <view v-if="!photo" class="upload-placeholder">
          <text class="upload-icon">📷</text>
          <text class="upload-text">点击拍照或上传照片</text>
        </view>
        <image v-else :src="photo" class="preview-photo" mode="aspectFill" />
      </view>
      <view class="upload-tips">
        <text class="tips-text">支持拍照或从相册选择，系统将自动识别物品</text>
      </view>
    </view>

    <view class="form-section card">
      <view class="form-item">
        <text class="form-label">物品名称 *</text>
        <input 
          v-model="form.name" 
          class="form-input" 
          placeholder="请输入物品名称"
          maxlength="100"
        />
      </view>

      <view class="form-item">
        <text class="form-label">所属房间 *</text>
        <view class="form-picker" @click="showRoomPicker = true">
          <text class="picker-text">{{ selectedRoomName || '请选择房间' }}</text>
          <text class="picker-arrow">›</text>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">所属人物</text>
        <view class="form-picker" @click="showPersonPicker = true">
          <text class="picker-text">{{ selectedPersonName || '请选择（可选）' }}</text>
          <text class="picker-arrow">›</text>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">具体位置</text>
        <input 
          v-model="form.location" 
          class="form-input" 
          placeholder="如：书架第二层"
          maxlength="200"
        />
      </view>

      <view class="form-item">
        <text class="form-label">物品描述</text>
        <textarea 
          v-model="form.description" 
          class="form-textarea" 
          placeholder="请输入物品描述（可选）"
          maxlength="500"
        />
      </view>
    </view>

    <view class="submit-btn" @click="submitForm">
      <text>保存物品</text>
    </view>
  </view>

  <view v-if="showRoomPicker" class="picker-mask" @click="showRoomPicker = false">
    <view class="picker-content" @click.stop>
      <view class="picker-header">
        <text class="picker-title">选择房间</text>
        <text class="picker-close" @click="showRoomPicker = false">×</text>
      </view>
      <scroll-view scroll-y class="picker-list">
        <view 
          v-for="room in rooms" 
          :key="room.id" 
          class="picker-item"
          :class="{ active: form.roomId === room.id }"
          @click="selectRoom(room)"
        >
          <text class="room-icon">{{ room.icon }}</text>
          <text class="room-name">{{ room.name }}</text>
        </view>
      </scroll-view>
    </view>
  </view>

  <view v-if="showPersonPicker" class="picker-mask" @click="showPersonPicker = false">
    <view class="picker-content" @click.stop>
      <view class="picker-header">
        <text class="picker-title">选择人物</text>
        <text class="picker-close" @click="showPersonPicker = false">×</text>
      </view>
      <scroll-view scroll-y class="picker-list">
        <view 
          class="picker-item"
          :class="{ active: !form.personId }"
          @click="selectPerson(null)"
        >
          <text class="room-icon">✕</text>
          <text class="room-name">不选择</text>
        </view>
        <view 
          v-for="person in persons" 
          :key="person.id" 
          class="picker-item"
          :class="{ active: form.personId === person.id }"
          @click="selectPerson(person)"
        >
          <text class="room-icon">{{ person.avatar }}</text>
          <text class="room-name">{{ person.name }}</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getRooms, getPersons, createItem, type Room, type Person } from '@/api'

const rooms = ref<Room[]>([])
const persons = ref<Person[]>([])
const photo = ref('')
const showRoomPicker = ref(false)
const showPersonPicker = ref(false)

const form = ref({
  name: '',
  roomId: '',
  personId: '',
  location: '',
  description: ''
})

const selectedRoomName = computed(() => {
  if (!form.value.roomId) return ''
  return rooms.value.find(r => r.id === form.value.roomId)?.name || ''
})

const selectedPersonName = computed(() => {
  if (!form.value.personId) return ''
  return persons.value.find(p => p.id === form.value.personId)?.name || ''
})

onLoad((options) => {
  if (options?.roomId) {
    form.value.roomId = options.roomId as string
  }
})

onMounted(async () => {
  await loadRooms()
  await loadPersons()
})

async function loadRooms() {
  try {
    rooms.value = await getRooms()
  } catch (error) {
    console.error('加载房间失败:', error)
  }
}

async function loadPersons() {
  try {
    persons.value = await getPersons()
  } catch (error) {
    console.error('加载人物失败:', error)
  }
}

function chooseImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      photo.value = res.tempFilePaths[0]
    }
  })
}

function selectRoom(room: Room) {
  form.value.roomId = room.id
  showRoomPicker.value = false
}

function selectPerson(person: Person | null) {
  form.value.personId = person?.id || ''
  showPersonPicker.value = false
}

async function submitForm() {
  if (!form.value.name.trim()) {
    uni.showToast({ title: '请输入物品名称', icon: 'none' })
    return
  }
  if (!form.value.roomId) {
    uni.showToast({ title: '请选择所属房间', icon: 'none' })
    return
  }

  try {
    uni.showLoading({ title: '保存中...' })
    
    await createItem({
      name: form.value.name.trim(),
      description: form.value.description.trim() || undefined,
      roomId: form.value.roomId,
      personId: form.value.personId || undefined,
      location: form.value.location.trim() || undefined,
      photo: photo.value || undefined
    })

    uni.hideLoading()
    uni.showToast({ title: '保存成功', icon: 'success' })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    uni.hideLoading()
    console.error('保存失败:', error)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 20rpx 32rpx;
  padding-bottom: 160rpx;
  min-height: 100vh;
  background: #f5f5f5;
}

.card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.section-header {
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.photo-section {
  margin-top: 20rpx;
}

.photo-upload {
  width: 100%;
  height: 320rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #f8f9fa;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.upload-text {
  font-size: 28rpx;
  color: #999999;
}

.preview-photo {
  width: 100%;
  height: 100%;
}

.upload-tips {
  margin-top: 16rpx;
}

.tips-text {
  font-size: 24rpx;
  color: #999999;
}

.form-section {
  margin-top: 20rpx;
}

.form-item {
  margin-bottom: 28rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.form-label {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 12rpx;
  display: block;
}

.form-input {
  width: 100%;
  height: 88rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  box-sizing: border-box;
}

.form-textarea {
  width: 100%;
  height: 200rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 30rpx;
  box-sizing: border-box;
}

.form-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
}

.picker-text {
  font-size: 30rpx;
  color: #333333;
}

.picker-arrow {
  font-size: 36rpx;
  color: #cccccc;
}

.submit-btn {
  position: fixed;
  bottom: 32rpx;
  left: 32rpx;
  right: 32rpx;
  background: linear-gradient(135deg, #4A90D9 0%, #6BA3E0 100%);
  color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba(74, 144, 217, 0.3);
}

.picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 100;
}

.picker-content {
  width: 100%;
  background: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 70vh;
}

.picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.picker-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333333;
}

.picker-close {
  font-size: 48rpx;
  color: #999999;
  line-height: 1;
}

.picker-list {
  max-height: 60vh;
}

.picker-item {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f5;

  &.active {
    background: #e8f0fe;
  }
}

.room-icon {
  font-size: 44rpx;
  margin-right: 20rpx;
}

.room-name {
  font-size: 32rpx;
  color: #333333;
}
</style>
