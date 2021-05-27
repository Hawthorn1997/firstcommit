package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests().antMatchers(
//                "/user/setting",
//                "/user/upload",
//                "/discuss/add",
//                "/comment/add/**",
//                "/letter/**",
//                "/notice/**",
//                "/like",
//                "/follow",
//                "/unfollow"
        ).hasAnyAuthority(
                AUTHORITY_ADMIN,
                AUTHORITY_MODERATOR,
                AUTHORITY_USER

        ).anyRequest().permitAll()
        .and().csrf().disable();
        //权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没有登录
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = httpServletResponse.getWriter();
                            printWriter.write(CommunityUtil.getJSONString(403, "你还没有登录"));
                        }else{
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    //权限不足
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = httpServletResponse.getWriter();
                            printWriter.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限"));
                        }else{
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/denied");
                        }
                    }
                });

        //security底层默认拦截/logout请求进行退出的处理
        //覆盖他默认的逻辑，才能执行自己的退出代码
        http.logout().logoutUrl("/securitylogout");





    }
}
