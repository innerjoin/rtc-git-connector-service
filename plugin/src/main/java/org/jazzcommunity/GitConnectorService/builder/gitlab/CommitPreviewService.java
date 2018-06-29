package org.jazzcommunity.GitConnectorService.builder.gitlab;

import ch.sbi.minigit.gitlab.GitlabApi;
import ch.sbi.minigit.type.gitlab.commit.Commit;
import com.ibm.team.repository.service.TeamRawService;
import com.siemens.bt.jazz.services.base.rest.RestRequest;
import org.apache.commons.logging.Log;
import org.jazzcommunity.GitConnectorService.base.rest.AbstractRestService;
import org.jazzcommunity.GitConnectorService.base.rest.PathParameters;
import org.jazzcommunity.GitConnectorService.data.TokenHelper;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class CommitPreviewService extends AbstractRestService {

    public CommitPreviewService(Log log, HttpServletRequest request, HttpServletResponse response, RestRequest restRequest, TeamRawService parentService, PathParameters pathParameters) {
        super(log, request, response, restRequest, parentService, pathParameters);
    }

    public void execute() throws IOException {
        URL url = new URL("https://" + pathParameters.get("host"));

        GitlabApi api = new GitlabApi(url.toString(), TokenHelper.getToken(url, parentService));
        Commit commit = api.getCommit(
                pathParameters.getAsInteger("projectId"),
                pathParameters.get("commit"));

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/html/commit_preview.twig");
        JtwigModel model = JtwigModel.newModel()
                .with("title", commit.getTitle())
                .with("comment", commit.getMessage())
                .with("authorName", commit.getCommitterName())
                .with("creationDate", commit.getAuthoredDate())
                .with("commitAuthor", commit.getAuthorName())
                .with("commitDate", commit.getCommittedDate())
                .with("sha", commit.getId());

        response.setContentType("text/html");
        template.render(model, response.getOutputStream());
    }
}
