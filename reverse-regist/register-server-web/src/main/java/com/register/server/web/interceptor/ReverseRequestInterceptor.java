package com.register.server.web.interceptor;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.server.web.handler.RequestHandler;
import com.register.server.web.handler.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ReverseRequestInterceptor implements HandlerInterceptor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            InnerRequest innerRequest = RequestHandler.invokeRequest(request, this.getAppMappingInfo(request));
            InnerResponse innerResponse = ResponseHandler.getResponse(innerRequest);
            String result = innerResponse == null ? "{\"code\":\"-1\",\"msg\":\"异常结果\"}" : innerResponse.getContent();
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(result);
        }catch (Exception e){
            log.error("invoke error", e);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("{\"code\":\"-1\",\"msg\":\"异常结果\"}");
        }
        return false;
    }

    /**
     * 先只做第一级路径精确匹配，不做正则表达式匹配
     * @param request
     * @return
     */
    private Map<String, Object> getAppMappingInfo(HttpServletRequest request){
        String contextPath = getContextPathFromUrl(request.getRequestURI());
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from t_appname_path where mapping_path = ? ", contextPath);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    private String getContextPathFromUrl(String url){
        if(StringUtils.isBlank(url) || url.length() < 1 || !url.startsWith("/")){
            return "";
        }
        String url2 = url.substring(1);
        if(url2.contains("/")){
            return "/" + StringUtils.substringBefore(url2, "/");
        }
        return url;
    }

}
