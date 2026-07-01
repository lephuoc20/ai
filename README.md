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
sudo rm -rf /opt/data/ai/postgres-data/*
sudo rm -rf /opt/data/ai/shared-app-data/*
sudo rm -rf /opt/data/ai/redis-data/*

```