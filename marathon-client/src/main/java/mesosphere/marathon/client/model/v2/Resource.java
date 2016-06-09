package mesosphere.marathon.client.model.v2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Resource {

  private final String uri;
  private final boolean executable;
  private final boolean extract;
  private final boolean cache;
}
