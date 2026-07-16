package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class Filter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Client IP------  " + request.getRemoteAddr());
        System.out.println("Remote User------  " + request.getRemoteUser());
        System.out.println("Remote Host------  " + request.getRemoteHost());
        System.out.println("Remote Port------  " + request.getRemotePort());
        filterChain.doFilter(request,response);
        System.out.println("Client IP------  " + request.getRemoteAddr());
        System.out.println("Remote User------  " + request.getRemoteUser());
        System.out.println("Remote Host------  " + request.getRemoteHost());
        System.out.println("Remote Port------  " + request.getRemotePort());
    }
}
