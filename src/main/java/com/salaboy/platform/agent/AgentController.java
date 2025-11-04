package com.salaboy.platform.agent;

import io.dapr.workflows.client.DaprWorkflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {


  @Autowired
  private DaprWorkflowClient daprWorkflowClient;




  @PostMapping("/start")
  public String request(){
    String instanceId = daprWorkflowClient.scheduleNewWorkflow(PlatformRequestWorkflow.class, "get all available kubernetes CRDs resources cluster wide");
    return instanceId;
  }

}
