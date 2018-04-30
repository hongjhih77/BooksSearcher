package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class LogService {

  public static String LOGGER_NAME;

  @Value("${PLATFORM}")
  public void setDatabase(String logname) {
    LOGGER_NAME = logname;
  }

  public static void errorSaveJson(Object object, Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(Logger.GLOBAL_LOGGER_NAME);
    try {
      log.error(new ObjectMapper().writeValueAsString(object),throwable);
    } catch (JsonProcessingException e) {
      log.error(e);
    }
  }

  public static void error(Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(Logger.GLOBAL_LOGGER_NAME);
    log.error(throwable);
  }

  public static void info(String msg){
    Log log = LogFactory.getLog(LOGGER_NAME);
    log.info(msg);
  }

}
