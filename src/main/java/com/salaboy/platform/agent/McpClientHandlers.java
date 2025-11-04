package com.salaboy.platform.agent;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springaicommunity.mcp.annotation.McpSampling;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class McpClientHandlers {

  private static final Logger logger = LoggerFactory.getLogger(McpClientHandlers.class);

  private final ChatClient chatClient;

  public McpClientHandlers(@Lazy ChatClient chatClient) { // Lazy is needed to avoid circular dependency
    this.chatClient = chatClient;
  }

  @McpProgress(clients = "kagent-tools") // (1)
  public void progressHandler(McpSchema.ProgressNotification progressNotification) {
    logger.info("MCP PROGRESS: [{}] progress: {} total: {} message: {}",
        progressNotification.progressToken(), progressNotification.progress(),
        progressNotification.total(), progressNotification.message());
  }

  @McpLogging(clients = "kagent-tools")
  public void loggingHandler(McpSchema.LoggingMessageNotification loggingMessage) {
    logger.info("MCP LOGGING: [{}] {}", loggingMessage.level(), loggingMessage.data());
  }

  @McpSampling(clients = "kagent-tools")
  public McpSchema.CreateMessageResult samplingHandler(McpSchema.CreateMessageRequest llmRequest) {

    logger.info("MCP SAMPLING: {}", llmRequest);

    String llmResponse = chatClient
        .prompt()
        .system(llmRequest.systemPrompt())
        .user(((McpSchema.TextContent) llmRequest.messages().get(0).content()).text())
        .call()
        .content();

    return McpSchema.CreateMessageResult.builder().content(new McpSchema.TextContent(llmResponse)).build();
  }
}