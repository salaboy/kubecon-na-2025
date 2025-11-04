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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class CheckPlatformCapabilitiesActivity implements WorkflowActivity {


  private String agentPrompt = """
      This agent is responsible for checking the platform capabilities and requesting list of resources.
      To do this, the agent can use the kagent-tools, more specifically the ones related to Kubernetes (k8s_).
      
      Ignore all tools that are not related to Kubernetes.
      
      Return items found as a JSON array of strings. No markdown. For example: 
      [
        "pod",
        "deployment",
        "service"
      ]
      
      {input}
      """;

  private final ChatClient chatClient;
  private final ToolCallbackProvider mcpToolProvider;
  private final OpenAiChatModel openAiChatModel;

  public CheckPlatformCapabilitiesActivity(ChatClient.Builder chatClientBuilder,
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
    System.out.println(">>> Running CheckPlatformCapabilitiesActivity");
    String input = workflowActivityContext.getInput(String.class);

    String taskExecutionId = workflowActivityContext.getTaskExecutionId();

    System.out.println("TaskExecutionId: " + taskExecutionId);

    ToolCallback[] toolCallbacks = mcpToolProvider.getToolCallbacks();
    List<ToolCallback> filteredTools =  new ArrayList<>();
    for (ToolCallback toolCallback : toolCallbacks) {
      ToolDefinition definition = toolCallback.getToolDefinition();
      if(definition.name().equals("k8s_get_resources")){
        filteredTools.add(toolCallback);
      }
    }

    ChatClient.ChatClientRequestSpec clientRequestSpec = chatClient.prompt()
        .user(u -> u.text(agentPrompt)
            .param("input", input))
        .options(ChatOptions.builder()
          .temperature(0.1)  // Very deterministic output
          .build())
        //.toolContext(Map.of("progressToken", "token-" + new Random().nextInt()))
        .toolCallbacks(filteredTools);


    try {

      ChatClient.CallResponseSpec responseSpec = clientRequestSpec.call();


      String content = responseSpec.content();
      System.out.println(">> Response:" + content);

      System.out.println("Total tokens used:" + responseSpec.chatResponse().getMetadata().getUsage().getTotalTokens());
      System.out.println("Completion tokens used:" + responseSpec.chatResponse().getMetadata().getUsage().getCompletionTokens());

      return true;
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("CheckPlatformCapabilitiesActivity Error: " + e.getMessage());
      return true;
    }
  }
}
