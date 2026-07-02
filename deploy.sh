#!/usr/bin/env bash
set -e
if [ $# -eq 0 ]; then
    echo "No target arguments [jar, native] provided."
    exit 1
fi

if [ $# -eq 1 ]; then
    echo "No app arguments [order-service, order-worker] provided."
    exit 1
fi
KUBE="microk8s kubectl"
REGISTRY="localhost:32000"
TARGET="jar"
APP="ai-service"
echo "🐳 STEP 2: Building & Tagging Docker Images..."
docker build --network=host --no-cache --build-arg REGISTRY=$REGISTRY --build-arg APP="$APP" -t "$REGISTRY/$APP:latest" -f "Dockerfile-$TARGET" .

echo "📦 STEP 3: Pushing Images to MicroK8s Local Registry..."
docker push "$REGISTRY/$APP:latest"

echo "☸️ STEP 4: Applying Manifests..."
$KUBE apply -f "deployment/$APP.yaml"

echo "🔄 STEP 5: Restarting Deployments..."
$KUBE rollout restart deployment $APP

echo "✅ Success!"
$KUBE get pods
