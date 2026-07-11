package org.duckdns.massemiso.todo_list_api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class ResponseUtil {

  private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public static void writeErrorResponse(
      HttpServletResponse response,
      HttpStatus status,
      Exception ex,
      String logMsg,
      String msg
  ) throws IOException {
    log.warn(logMsg, ex);
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        status.value(),
        msg,
        List.of(ex.getMessage())
    );
    response.setContentType("application/json");
    response.setStatus(status.value());
    response.getWriter().write(mapper.writeValueAsString(errorResponse));
  }
}
