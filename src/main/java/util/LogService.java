package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogService {
  public static void errorSaveJson(Object object, Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(clazz);
    try {
      log.error(new ObjectMapper().writeValueAsString(object),throwable);
    } catch (JsonProcessingException e) {
      log.error(e);
    }
  }

  public static void error(Throwable throwable, Class<?> clazz){
    Log log = LogFactory.getLog(clazz);
    log.error(throwable);
  }

}
