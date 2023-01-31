package com.clm.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.clm.reggie.common.BaseContext;
import com.clm.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String reqURI = req.getRequestURI();

        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        boolean check = check(urls, reqURI);

        if(check){
            filterChain.doFilter(req,resp);
            return;
        }

        if(req.getSession().getAttribute("employee") != null){
            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.SetCurrentId(empId);
            filterChain.doFilter(req,resp);
            return;
        }

        if(req.getSession().getAttribute("user") != null){
            Long userId = (Long) req.getSession().getAttribute("user");
            BaseContext.SetCurrentId(userId);
            filterChain.doFilter(req,resp);
            return;
        }

        resp.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    public boolean check(String[] urls,String reqURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, reqURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
