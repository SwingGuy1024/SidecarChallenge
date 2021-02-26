package com.neptunedreams.framework;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptunedreams.framework.exception.ResponseException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/14/21
 * <p>Time: 7:50 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class ResponseBody {
  private final String timestamp;
  private final int status;
  private final String error;
  private final String message;
  private final String path;

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
  private static final ZoneId utcZone = ZoneId.of("Etc/Universal");
  
  public ResponseBody(ResponseException responseException, WebRequest webRequest) {
    this.message = responseException.getMessage();
    this.status = responseException.getStatusCode();
    this.error = responseException.getErrorName();
    this.timestamp = formatter.format(OffsetDateTime.now(utcZone));
    this.path = getPath(webRequest);
  }

  private String getPath(final WebRequest webRequest) {
    NativeWebRequest nativeWebRequest = (NativeWebRequest) webRequest;
    final HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
    if (httpServletRequest != null) {
      return httpServletRequest.getRequestURI();
    }
    //noinspection HardCodedStringLiteral
    return "(unknown)"; // Shouldn't happen
  }

  public String getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
//      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      //noinspection ProhibitedExceptionThrown
      throw new RuntimeException(e); // Shouldn't happen
    }
  }
}
