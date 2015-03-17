package mesosphere.marathon.client.model.v2;

import java.util.Collection;

import mesosphere.marathon.client.utils.ModelUtils;

public class Docker {
	private String image;
	private String network;
	private boolean privileged;
	private Collection<Port> portMappings;
	private Collection<Parameter> parameters;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public Collection<Port> getPortMappings() {
		return portMappings;
	}

	public void setPortMappings(Collection<Port> portMappings) {
		this.portMappings = portMappings;
	}

	public boolean isPrivileged() {
		return privileged;
	}

	public void setPrivileged(boolean privileged) {
		this.privileged = privileged;
	}

	public Collection<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Collection<Parameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return ModelUtils.toString(this);
	}
}
