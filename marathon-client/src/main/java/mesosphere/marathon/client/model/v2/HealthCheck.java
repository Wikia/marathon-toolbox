package mesosphere.marathon.client.model.v2;

public class HealthCheck {
    private String path;
    private String protocol;
    private Integer portIndex;
    private Integer gracePeriodSeconds;
    private Integer intervalSeconds;
    private Integer timeoutSeconds;
    private Integer maxConsecutiveFailures;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPortIndex() {
        return portIndex;
    }

    public void setPortIndex(Integer portIndex) {
        this.portIndex = portIndex;
    }

    public Integer getGracePeriodSeconds() {
        return gracePeriodSeconds;
    }

    public void setGracePeriodSeconds(Integer gracePeriodSeconds) {
        this.gracePeriodSeconds = gracePeriodSeconds;
    }

    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Integer getMaxConsecutiveFailures() {
        return maxConsecutiveFailures;
    }

    public void setMaxConsecutiveFailures(Integer maxConsecutiveFailures) {
        this.maxConsecutiveFailures = maxConsecutiveFailures;
    }
}
