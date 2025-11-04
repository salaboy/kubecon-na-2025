package com.salaboy.platform.agent;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.McpConnectionInfo;
import org.springframework.ai.mcp.McpToolFilter;
import org.springframework.stereotype.Component;

@Component
public class KAgentToolsFilter implements McpToolFilter {

  @Override
  public boolean test(McpConnectionInfo connectionInfo, McpSchema.Tool tool) {
    // Filter logic based on connection information and tool properties
    // Return true to include the tool, false to exclude it

//    // Example: Exclude tools from a specific client
//    if (connectionInfo.clientInfo().name().equals("restricted-client")) {
//      return false;
//    }

    // Example: Only include tools with specific names
    if (tool.name().startsWith("k8s_")) {
      if(tool.name().equals("k8s_get_available_api_resources")
          || tool.name().equals("k8s_get_cluster_configuration")
      || tool.name().equals("k8s_check_service_connectivity")
      || tool.name().equals("k8s_delete_resource")
      || tool.name().equals("k8s_get_events")
      || tool.name().equals("k8s_rollout")
    || tool.name().equals("k8s_get_pod_log")
      || tool.name().equals("k8s_create_resource_from_url")
      || tool.name().equals("k8s_describe_resource")
      || tool.name().equals("k8s_generate_resource")){
        return false;
      }
      System.out.println("Accepted tool: " + tool.name());
      return true;
    }

//    // Example: Filter based on tool description or other properties
//    if (tool.description() != null &&
//        tool.description().contains("experimental")) {
//      return false;
//    }

    return false; // Include all other tools by default
  }
}