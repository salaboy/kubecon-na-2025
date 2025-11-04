package com.salaboy.platform.agent;

import io.dapr.workflows.WorkflowActivity;
import io.dapr.workflows.WorkflowActivityContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IncreaseAppResiliencyActivity implements WorkflowActivity {


  private String agentPrompt = """
      This agent is responsible for increasing the resiliency of the application.
      It does that by using resiliency capabilities that are available in the cluster, 
      for example resiliency policies. 
      The agent will create these policies for the specificied applications.
      
      {input}
      """;

  private final ChatClient chatClient;
  private final ToolCallbackProvider mcpToolProvider;
  private final OpenAiChatModel openAiChatModel;

  public IncreaseAppResiliencyActivity(ChatClient.Builder chatClientBuilder,
                                       OpenAiChatModel openAiChatModel,
                                       ToolCallbackProvider mcpToolProvider) {

    this.openAiChatModel = openAiChatModel;
    var advisor = ChatModelCallAdvisor.builder()
        .chatModel(openAiChatModel)
        .build();
    this.chatClient = chatClientBuilder
        .defaultAdvisors(advisor).build();
    this.mcpToolProvider = mcpToolProvider;
  }

  @Override
  public Object run(WorkflowActivityContext workflowActivityContext) {
    System.out.println(">>> Running IncreaseAppResiliencyActivity");
    String input = workflowActivityContext.getInput(String.class);

    ToolCallback[] toolCallbacks = mcpToolProvider.getToolCallbacks();
    List<ToolCallback> filteredTools = new ArrayList<>();
    for (ToolCallback toolCallback : toolCallbacks) {
      ToolDefinition definition = toolCallback.getToolDefinition();
      if(definition.name().equals("k8s_create_resource") ||
          definition.name().equals("k8s_get_resources")){
        filteredTools.add(toolCallback);

      }
    }

    ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
        .user(u -> u.text(agentPrompt)
            .param("input", input))
        .options(ChatOptions.builder()
            .temperature(0.1)  // Very deterministic output
            .build())
        //.toolContext(Map.of("progressToken", "token-" + new Random().nextInt()))
        .toolCallbacks(filteredTools)
        .call();

    System.out.println("Total tokens used:" + responseSpec.chatResponse().getMetadata().getUsage().getTotalTokens());
    System.out.println("Completion tokens used:" + responseSpec.chatResponse().getMetadata().getUsage().getCompletionTokens());

    String content = responseSpec.content();

    System.out.println("Response:" + content);
    return content;
  }
}
