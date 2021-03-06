package org.jazzcommunity.GitConnectorService.service.gitlab;

import ch.sbi.minigit.gitlab.GitlabApi;
import ch.sbi.minigit.type.gitlab.mergerequest.MergeRequest;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.team.repository.service.TeamRawService;
import com.siemens.bt.jazz.services.base.rest.parameters.PathParameters;
import com.siemens.bt.jazz.services.base.rest.parameters.RestRequest;
import com.siemens.bt.jazz.services.base.rest.service.AbstractRestService;
import java.io.IOException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.jazzcommunity.GitConnectorService.data.TokenHelper;
import org.jazzcommunity.GitConnectorService.net.GitServiceArtifact;
import org.jazzcommunity.GitConnectorService.net.Request;
import org.jazzcommunity.GitConnectorService.net.UrlBuilder;
import org.jazzcommunity.GitConnectorService.olsc.type.merge_request.OslcMergeRequest;
import org.jazzcommunity.GitConnectorService.oslc.mapping.MergeRequestMapper;
import org.jazzcommunity.GitConnectorService.properties.PropertyReader;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class RequestLinkService extends AbstractRestService {

  public RequestLinkService(
      Log log,
      HttpServletRequest request,
      HttpServletResponse response,
      RestRequest restRequest,
      TeamRawService parentService,
      PathParameters pathParameters) {
    super(log, request, response, restRequest, parentService, pathParameters);
  }

  @Override
  public void execute() throws Exception {
    GitServiceArtifact parameters =
        new GitServiceArtifact(
            pathParameters.get("host"),
            pathParameters.get("projectId"),
            pathParameters.get("mergeRequestId"));

    MergeRequest mergeRequest = getMergeRequest();

    if (Request.isLinkRequest(this.request)) {
      sendLinkResponse(mergeRequest, parameters);
    } else if (Request.isOslcRequest(request)) {
      sendOslcResponse(mergeRequest, parameters);
    } else {
      response.sendRedirect(mergeRequest.getWebUrl());
    }
  }

  private void sendOslcResponse(MergeRequest mergeRequest, GitServiceArtifact parameters)
      throws IOException {
    OslcMergeRequest oslcRequest =
        MergeRequestMapper.map(
            mergeRequest,
            UrlBuilder.getLinkUrl(parentService, parameters, "merge-request"),
            parentService.getRequestRepositoryURL());

    Gson gson = new GsonBuilder().serializeNulls().create();
    String json = gson.toJson(oslcRequest);
    response.setContentType(MediaType.JSON_UTF_8.toString());
    response.setHeader("OSLC-Core-Version", "2.0");
    response.getWriter().write(json);
  }

  private void sendLinkResponse(MergeRequest request, GitServiceArtifact parameters)
      throws IOException {
    URL preview = UrlBuilder.getPreviewUrl(parentService, parameters, "merge-request");
    PropertyReader properties = new PropertyReader();
    String icon =
        String.format(
            properties.get("url.image"),
            parentService.getRequestRepositoryURL(),
            properties.get("icon.gitlab.mergerequest.small"));

    JtwigTemplate template = JtwigTemplate.classpathTemplate(properties.get("template.xml.link"));
    JtwigModel model =
        JtwigModel.newModel()
            .with("about", request.getWebUrl())
            .with("title", StringEscapeUtils.escapeXml(request.getTitle()))
            .with("comment", StringEscapeUtils.escapeXml(request.getDescription()))
            .with("icon", icon)
            .with("resourceSmall", preview.toString())
            .with("resourceLarge", preview.toString());

    response.setContentType(properties.get("content.type.link.compact"));
    template.render(model, response.getOutputStream());
  }

  private MergeRequest getMergeRequest() throws IOException {
    URL url = new URL("https://" + pathParameters.get("host"));
    GitlabApi api = new GitlabApi(url.toString(), TokenHelper.getToken(url, parentService));

    return api.getMergeRequest(
        pathParameters.getAsInteger("projectId"), pathParameters.getAsInteger("mergeRequestId"));
  }
}
