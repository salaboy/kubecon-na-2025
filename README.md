# kubecon-na-2025

Agents with Kagent, Dapr Workflows and Spring AI

This simple example shows how we can use Kagent in conjuction with Dapr Workflows and Spring AI to create
Workflows as tools.





## Install

Install Kagent:

```
helm install kagent oci://ghcr.io/kagent-dev/kagent/helm/kagent \
    --namespace kagent \
    --set providers.default=openAI \
    --set providers.openAI.apiKey=$OPENAI_API_KEY \
    --set agents.argo-rollouts-agent.enabled=false \
    --set agents.cilium-debug-agent.enabled=false \
    --set agents.cilium-manager-agent.enabled=false \
    --set agents.cilium-policy-agent.enabled=false \
    --set agents.istio-agent.enabled=false 
```

Access the dashboard: 
```
k port-forward -n kagent svc/kagent-ui 8082:8080
```

Expose the MCP Tools Server: 

```
k port-forward -n kagent svc/kagent-tools 8084:8084
```

Install the MCP Inspector: 
```aiignore
npm install -g @modelcontextprotocol/inspector
```
Run
```
npx @modelcontextprotocol/inspector
```




