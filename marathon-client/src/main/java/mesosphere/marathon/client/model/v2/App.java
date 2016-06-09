package mesosphere.marathon.client.model.v2;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class App {

  private String id;
  private String cmd;
  private Integer instances;
  private Double cpus;
  private Double mem;
  private Double backoffFactor;
  private Integer backoffSeconds;
  private Integer maxLaunchDelaySeconds;
  @Deprecated
  private Collection<String> uris;
  private List<List<String>> constraints;
  private Container container;
  private Map<String, String> env;
  private Map<String, String> labels;
  private String executor;
  private List<Integer> ports;
  private boolean requirePorts;
  private UpgradeStrategy upgradeStrategy;
  private Collection<Task> tasks;
  private Integer tasksStaged;
  private Integer tasksRunning;
  private Integer tasksHealthy;
  private Integer tasksUnhealthy;
  private Collection<HealthCheck> healthChecks;

  private List<Resource> fetch;
}
