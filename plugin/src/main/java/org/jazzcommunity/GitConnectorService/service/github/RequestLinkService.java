package org.jazzcommunity.GitConnectorService.service.github;

import com.ibm.team.repository.service.TeamRawService;
import com.siemens.bt.jazz.services.base.rest.parameters.PathParameters;
import com.siemens.bt.jazz.services.base.rest.parameters.RestRequest;
import com.siemens.bt.jazz.services.base.rest.service.AbstractRestService;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;

public class RequestLinkService extends AbstractRestService {

  private final ResourceBundle messages;

  public RequestLinkService(
      Log log,
      HttpServletRequest request,
      HttpServletResponse response,
      RestRequest restRequest,
      TeamRawService parentService,
      PathParameters pathParameters) {
    super(log, request, response, restRequest, parentService, pathParameters);
    this.messages = ResourceBundle.getBundle("messages");
  }

  @Override
  public void execute() throws Exception {
    throw new RuntimeException(messages.getString("exception.not-implemented"));
  }
}
