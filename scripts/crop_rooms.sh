#!/bin/bash

# 户型图尺寸: 1080 x 829
# 网格布局: 
#   grid-template-columns: 1fr 0.8fr 0.4fr 1.2fr 0.8fr;  (总: 4.2fr)
#   grid-template-rows: 1fr 0.7fr 1.3fr 0.6fr;           (总: 3.6fr)

SOURCE_IMAGE="/Users/lvwei/code/home-twin/photo/户型图.jpg"
OUTPUT_DIR="/Users/lvwei/code/home-twin/photo/rooms"

mkdir -p "$OUTPUT_DIR"

# 厨房上 (kitchen-top): 列1(1fr), 行1(1fr)
# 位置: (0, 0), 尺寸: 257 x 230
sips --cropToHeightWidth 230 257 --cropOffset 0 0 -o "$OUTPUT_DIR/kitchen-top.jpg" "$SOURCE_IMAGE"

# 厨房下 (kitchen-bottom): 列1(1fr), 行2(0.7fr)
# 位置: (230, 0), 尺寸: 161 x 257
sips --cropToHeightWidth 161 257 --cropOffset 230 0 -o "$OUTPUT_DIR/kitchen-bottom.jpg" "$SOURCE_IMAGE"

# 卫生间 (bathroom): 列2(0.8fr), 行2(0.7fr)
# 位置: (230, 257), 尺寸: 161 x 206
sips --cropToHeightWidth 161 206 --cropOffset 230 257 -o "$OUTPUT_DIR/bathroom.jpg" "$SOURCE_IMAGE"

# 主卧 (bedroom-b): 列1-2(1.8fr), 行3(1.3fr)
# 位置: (391, 0), 尺寸: 299 x 463
sips --cropToHeightWidth 299 463 --cropOffset 391 0 -o "$OUTPUT_DIR/bedroom-b.jpg" "$SOURCE_IMAGE"

# 阳台 (balcony): 所有列(4.2fr), 行4(0.6fr)
# 位置: (690, 0), 尺寸: 138 x 1080
sips --cropToHeightWidth 138 1080 --cropOffset 690 0 -o "$OUTPUT_DIR/balcony.jpg" "$SOURCE_IMAGE"

# 入口 (hallway): 列3(0.4fr), 行2(0.7fr)
# 位置: (230, 463), 尺寸: 161 x 103
sips --cropToHeightWidth 161 103 --cropOffset 230 463 -o "$OUTPUT_DIR/hallway.jpg" "$SOURCE_IMAGE"

# 餐厅 (dining): 列4(1.2fr), 行1(1fr)
# 位置: (0, 566), 尺寸: 230 x 309
sips --cropToHeightWidth 230 309 --cropOffset 0 566 -o "$OUTPUT_DIR/dining.jpg" "$SOURCE_IMAGE"

# 客厅 (living): 列3-4(1.6fr), 行2-3(2.0fr)
# 位置: (230, 566), 尺寸: 400 x 460
sips --cropToHeightWidth 400 460 --cropOffset 230 566 -o "$OUTPUT_DIR/living.jpg" "$SOURCE_IMAGE"

# 次卧 (bedroom-a): 列5(0.8fr), 行1-2(1.7fr)
# 位置: (0, 872), 尺寸: 206 x 391
sips --cropToHeightWidth 206 391 --cropOffset 0 872 -o "$OUTPUT_DIR/bedroom-a.jpg" "$SOURCE_IMAGE"

echo "切割完成！生成的房间图片："
ls -la "$OUTPUT_DIR/"