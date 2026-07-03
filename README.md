# demo spring ai application

```shell
# Step A: Recreate secrets and restore the namespace shell configuration
microk8s kubectl apply -f secrets.yaml

# Step B: Spin up your infrastructure nodes (Volumes, PostgreSQL with pgvector, and Redis Stack)
microk8s kubectl apply -f infra.yaml

```

```shell
# clean up everything in ai namespace
microk8s kubectl delete all,pvc,secrets --all -n ai
# 1. Delete the stuck PVCs and old PV definitions
kubectl delete pvc postgres-ai-pvc redis-pvc -n ai
kubectl delete pv postgres-ai-pv redis-pv

# 2. Make absolutely sure the local host directories exist and have open permissions
sudo mkdir -p /opt/data/ai/postgres-data /opt/data/ai/redis-data
sudo chmod -R 777 /opt/data/ai/

```