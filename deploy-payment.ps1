# 1. GitHub'daki en son başarılı commit SHA'sını al (İlk 7 hane)
$SHORT_SHA = (git rev-parse --short HEAD)
$IMAGE_NAME = "enesskaracay/payment-service:v1.0-$SHORT_SHA"

Write-Host "🚀 Dağıtım Başlıyor: $IMAGE_NAME" -ForegroundColor Cyan

# 2. Kubernetes imajını güncelle
kubectl set image deployment/payment-service payment-service=$IMAGE_NAME

# 3. Güncelleme durumunu takip et
Write-Host "⏳ Yeni Pod'un ayağa kalkması bekleniyor..." -ForegroundColor Yellow
kubectl rollout status deployment/payment-service

Write-Host "✅ İşlem Başarılı! Payment Service v1.0-$SHORT_SHA sürümüne güncellendi." -ForegroundColor Green