package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class LogService {

  private static final String PLATFORM = System.getenv("PLATFORM");

  private static final String LOGGER_NAME = PLATFORM != null ? PLATFORM : Logger.GLOBAL_LOGGER_NAME;

  public static void errorSaveJson(Object object, Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(LOGGER_NAME);
    try {
      log.error(new ObjectMapper().writeValueAsString(object),throwable);
    } catch (JsonProcessingException e) {
      log.error(e);
    }
  }

  public static void error(Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(LOGGER_NAME);
    log.error(throwable);
  }

  public static void info(String msg){
    Log log = LogFactory.getLog(LOGGER_NAME);
    log.info(msg);
  }

}
