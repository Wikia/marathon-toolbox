package mesosphere.marathon.client.model.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import mesosphere.marathon.client.utils.ModelUtils;

public class App {
	private String id;
	private String cmd;
	private Integer instances;
	private Double cpus;
	private Double mem;
	private Double backoffFactor;
	private Integer backoffSeconds;
	private Integer maxLaunchDelaySeconds;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Integer getInstances() {
		return instances;
	}

	public void setInstances(Integer instances) {
		this.instances = instances;
	}

	public Double getCpus() {
		return cpus;
	}

	public void setCpus(Double cpus) {
		this.cpus = cpus;
	}

	public Double getMem() {
		return mem;
	}

	public void setMem(Double mem) {
		this.mem = mem;
	}

	public Double getBackoffFactor() {
		return backoffFactor;
	}

	public void setBackoffFactor(Double backoffFactor) {
		this.backoffFactor = backoffFactor;
	}

	public Integer getBackoffSeconds() {
		return backoffSeconds;
	}

	public void setBackoffSeconds(Integer backoffSeconds) {
		this.backoffSeconds = backoffSeconds;
	}

	public Integer getMaxLaunchDelaySeconds() {
		return maxLaunchDelaySeconds;
	}

	public void setMaxLaunchDelaySeconds(Integer maxLaunchDelaySeconds) {
		this.maxLaunchDelaySeconds = maxLaunchDelaySeconds;
	}

	public Collection<String> getUris() {
		return uris;
	}

	public void setUris(Collection<String> uris) {
		this.uris = uris;
	}

	public List<List<String>> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<List<String>> constraints) {
		this.constraints = constraints;
	}

	public void addConstraint(String attribute, String operator, String value) {
		if (this.constraints == null) {
			this.constraints = new ArrayList<List<String>>();
		}
		List<String> constraint = new ArrayList<String>(3);
		constraint.add(attribute == null ? "" : attribute);
		constraint.add(operator == null ? "" : operator);
		constraint.add(value == null ? "" : value);
		this.constraints.add(constraint);
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public Map<String, String> getEnv() {
		return env;
	}

	public void setEnv(Map<String, String> env) {
		this.env = env;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}

	public void addUri(String uri) {
		if (this.uris == null) {
			this.uris = new ArrayList<String>();
		}
		this.uris.add(uri);
	}

	public void addPort(int port) {
		if (this.ports == null) {
			this.ports = new ArrayList<Integer>();
		}
		this.ports.add(port);
	}

	public boolean isRequirePorts() {
		return requirePorts;
	}

	public void setRequirePorts(boolean requirePorts) {
		this.requirePorts = requirePorts;
	}


	public UpgradeStrategy getUpgradeStrategy() {
		return upgradeStrategy;
	}

	public void setUpgradeStrategy(UpgradeStrategy upgradeStrategy) {
	this.upgradeStrategy = upgradeStrategy;
	}

	public Collection<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Collection<Task> tasks) {
		this.tasks = tasks;
	}

	public Integer getTasksStaged() {
		return tasksStaged;
	}

	public void setTasksStaged(Integer tasksStaged) {
		this.tasksStaged = tasksStaged;
	}

	public Integer getTasksRunning() {
		return tasksRunning;
	}

	public void setTasksRunning(Integer tasksRunning) {
		this.tasksRunning = tasksRunning;
	}

	public Integer getTasksHealthy() {
		return tasksHealthy;
	}

	public void setTasksHealthy(Integer tasksHealthy) {
		this.tasksHealthy = tasksHealthy;
	}

	public Integer getTasksUnhealthy() {
		return tasksUnhealthy;
	}

	public void setTasksUnhealthy(Integer tasksUnhealthy) {
		this.tasksUnhealthy = tasksUnhealthy;
	}

	public Collection<HealthCheck> getHealthChecks() {
		return healthChecks;
	}

	public void setHealthChecks(Collection<HealthCheck> healthChecks) {
		this.healthChecks = healthChecks;
	}

	@Override
	public String toString() {
		return ModelUtils.toString(this);
	}
}
