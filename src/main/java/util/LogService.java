package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Logger;

public class LogService {

  private static final String PLATFORM =
      System.getenv().getOrDefault("PLATFORM", Logger.GLOBAL_LOGGER_NAME);


  public static void errorSaveJson(Object object, Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(Logger.GLOBAL_LOGGER_NAME);
    log.error(PLATFORM + ":");
    try {
      log.error(new ObjectMapper().writeValueAsString(object),throwable);
    } catch (JsonProcessingException e) {
      log.error(e);
    }
  }

  public static void error(Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(Logger.GLOBAL_LOGGER_NAME);
    log.error(PLATFORM + ":");
    log.error(throwable);
  }

  public static void info(String msg){
    Log log = LogFactory.getLog(Logger.GLOBAL_LOGGER_NAME);
    log.info(PLATFORM + ":" + msg);
  }

}
