#!/bin/bash
# 家庭数字孪生系统 - MinIO初始化脚本

MINIO_URL="http://localhost:9000"
ACCESS_KEY="minioadmin"
SECRET_KEY="minioadmin"
BUCKET_NAME="hometwin"

echo "=========================================="
echo "  MinIO初始化脚本"
echo "=========================================="
echo ""

# 检查mc命令是否存在
if ! command -v mc &> /dev/null; then
    echo "正在下载MinIO客户端(mc)..."
    
    # 检测操作系统和架构
    OS=$(uname -s)
    ARCH=$(uname -m)
    
    if [ "$OS" = "Darwin" ]; then
        if [ "$ARCH" = "arm64" ]; then
            MC_URL="https://dl.min.io/client/mc/release/darwin-arm64/mc"
        else
            MC_URL="https://dl.min.io/client/mc/release/darwin-amd64/mc"
        fi
    else
        MC_URL="https://dl.min.io/client/mc/release/linux-amd64/mc"
    fi
    
    # 下载mc
    curl -sL "$MC_URL" -o /tmp/mc
    chmod +x /tmp/mc
    MC_CMD="/tmp/mc"
else
    MC_CMD="mc"
fi

echo "配置MinIO连接..."
$MC_CMD alias set hometwin-minio $MINIO_URL $ACCESS_KEY $SECRET_KEY

echo ""
echo "检查bucket是否存在..."
if $MC_CMD ls hometwin-minio/$BUCKET_NAME &> /dev/null; then
    echo "✓ Bucket '$BUCKET_NAME' 已存在"
else
    echo "创建bucket: $BUCKET_NAME"
    $MC_CMD mb hometwin-minio/$BUCKET_NAME
    
    if [ $? -eq 0 ]; then
        echo "✓ Bucket创建成功"
        
        # 设置bucket公开读取权限（用于图片访问）
        echo "设置bucket公开读取权限..."
        $MC_CMD anonymous set download hometwin-minio/$BUCKET_NAME
        echo "✓ 权限设置完成"
    else
        echo "✗ Bucket创建失败"
        exit 1
    fi
fi

echo ""
echo "MinIO初始化完成！"
echo "=========================================="
