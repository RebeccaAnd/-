package com.heima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.heima.reggie.common.BaseContext;
import com.heima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j //通过日志方式输出
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher(); //+ 专门用来进行路径比较的,路径匹配器,支持匹配器

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest; //向下转型 ServletRequest强转成HttpServletRequest
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1.获取本次请求的URI
        String requestURI = request.getRequestURI();//获取本次请求的URI

        String[] urls = new String[] { //定义不需要处理的请求路径
                "/employee/login",
                "/employee/logout",
                "/backend/**", //一些静态资源也不需要拦截 backend页面可以,主要是数据不可以
                "/front/**" ,//那么通配符如何处理
                "/common/**",
                // 对用户登录操作放行(邮箱)
                "/user/login",
                "/user/sendMsg"

        };
        // 2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI); //通过返回值就可知

        // 3.如果不需要处理,就直接放行
        if (check) {
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        // 4.判断登录状态,如果已登录,直接放行
        //判断登录状态
            //从Session中获取登录用户 设置当前登录用户的id以供同线程使用
        if (request.getSession().getAttribute("employee") != null) {
            log.info("后端用户已登录,用户id为{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId); //就是设置值

            filterChain.doFilter(request,response);
            return;
        }

        //判断前端用户是否登录
        if (request.getSession().getAttribute("user") != null ) {
            log.info("前端用户已登录,用户id为{}",request.getSession().getAttribute("user"));
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response); //通行
            return;
        }

        log.info("后台用户未登录");
        // 5.如果未登录则返回未登录结果
            //通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN"))); //相当于把R对象转成了JSON然后通过输出流写回去
    }

    /*
     * 路径匹配,检查本次请求是否需要放行
     */
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        //不匹配
        return false;
    }
}
