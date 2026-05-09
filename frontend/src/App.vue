<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.container {
  padding: 20rpx;
  min-height: 100vh;
}

.card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.btn-primary {
  background: linear-gradient(135deg, #4A90D9 0%, #6BA3E0 100%);
  color: #ffffff;
  border: none;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 32rpx;
  font-weight: 500;
}

.btn-secondary {
  background: #f5f5f5;
  color: #666666;
  border: 2rpx solid #e0e0e0;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 32rpx;
}

.text-primary {
  color: #4A90D9;
}

.text-secondary {
  color: #999999;
}

.text-sm {
  font-size: 24rpx;
}

.text-md {
  font-size: 28rpx;
}

.text-lg {
  font-size: 32rpx;
}

.text-xl {
  font-size: 36rpx;
}

.flex {
  display: flex;
}

.flex-col {
  flex-direction: column;
}

.items-center {
  align-items: center;
}

.justify-between {
  justify-content: space-between;
}

.justify-center {
  justify-content: center;
}

.gap-sm {
  gap: 16rpx;
}

.gap-md {
  gap: 24rpx;
}

.mt-sm {
  margin-top: 16rpx;
}

.mt-md {
  margin-top: 24rpx;
}

.mb-sm {
  margin-bottom: 16rpx;
}

.mb-md {
  margin-bottom: 24rpx;
}
</style>
