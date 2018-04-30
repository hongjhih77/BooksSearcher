package io.swagger.api.gen.api;

import org.springframework.stereotype.Component;
import util.LogService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@javax.annotation.Generated(
  value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen",
  date = "2018-04-17T14:21:51.738+08:00[Asia/Taipei]"
)
@Component
public class ApiOriginFilter implements Filter {

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse res = (HttpServletResponse) response;
    res.addHeader("Access-Control-Allow-Origin", "*");
    res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    res.addHeader("Access-Control-Allow-Headers", "Content-Type");
    long time = System.currentTimeMillis();
    chain.doFilter(request, response);
    time = System.currentTimeMillis() - time;
    LogService.info(((HttpServletRequest) request).getRequestURI() + " : " + time + " ms ");
  }

  public void destroy() {}

  public void init(FilterConfig filterConfig) throws ServletException {}
}
