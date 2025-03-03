package com.colak.springtutorial.controller;

import com.colak.springtutorial.service.ProxyServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class ProxyController {

    private final ProxyServices proxyService;

    @RequestMapping(value = "/**")
    public ResponseEntity<Object> handleProxyRequest(HttpServletRequest request) {

        log.info("Receive {} for url {}", request.getMethod(), request.getRequestURL().toString());

        return proxyService.doProxy(request);
    }

}
