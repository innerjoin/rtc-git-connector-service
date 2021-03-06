package org.jazzcommunity.GitConnectorService.net;

import com.google.common.net.MediaType;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.jazzcommunity.GitConnectorService.properties.PropertyReader;

public class Request {
  public static boolean isLinkRequest(HttpServletRequest request) throws IOException {
    PropertyReader properties = new PropertyReader();
    return request.getHeader("Accept").contains(properties.get("content.type.link.compact"))
        || request.getHeader("Accept").contains(properties.get("content.type.link.oslc"));
  }

  public static boolean isOslcRequest(HttpServletRequest request) {
    return MediaType.JSON_UTF_8.toString().contains(request.getHeader("Accept"));
  }

  // for debugging
  public static void printHeader(HttpServletRequest request) {
    for (String key : Collections.list(request.getHeaderNames())) {
      System.out.println(String.format("key: %s, value: %s", key, request.getHeader(key)));
    }
  }
}
