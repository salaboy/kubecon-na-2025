package com.salaboy.platform.agent;


import io.dapr.workflows.Workflow;
import io.dapr.workflows.WorkflowStub;
import org.springframework.stereotype.Component;

@Component
public class PlatformRequestWorkflow implements Workflow {

  @Override
  public WorkflowStub create() {
    return ctx -> {
      String input = ctx.getInput(String.class);
      ctx.getLogger().info("Calling Activity: {}", CheckPlatformCapabilitiesActivity.class.getName());
      Boolean capabilitiesObtained = ctx.callActivity(CheckPlatformCapabilitiesActivity.class.getName(),
          input, Boolean.class).await();


      if(capabilitiesObtained) {
        ctx.getLogger().info("Calling Activity: {}", IncreaseAppResiliencyActivity.class.getName());
        ctx.callActivity(IncreaseAppResiliencyActivity.class.getName(),
            "I want to increase resiliency for my app app1", String.class).await();
      }
      ctx.complete("Workflow completed");
    };
  }
}
