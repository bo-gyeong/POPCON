package com.example.popconback.configurations;

import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.service.UserService;
import com.example.popconback.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization= request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization: {}", authorization);

        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.error("authorization 이 없습니다.");
            filterChain.doFilter(request,response);
            return;
        }
        // 토큰 꺼내기
        String token = authorization.split(" ")[1];
        //토큰이 만료되었는지 여부
        if(JwtUtil.isExpired(token, secretKey)){
            log.error("token이 만료되었습니다.");
            filterChain.doFilter(request,response);
            return;
        }



        // 토큰에서 유저 정보 꺼내기
        String userName = JwtUtil.getUserName(token, secretKey);// 일단 world로 가정
        log.info("userName : {}",userName);
        String social =JwtUtil.getSocial(token, secretKey);
        UserDto tempuser = new UserDto();
        tempuser.setEmail(userName);
        tempuser.setSocial(social);



        //권한 부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(tempuser, null, List.of(new SimpleGrantedAuthority("USER")));
        //Detail을 넣어줍니다.
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
