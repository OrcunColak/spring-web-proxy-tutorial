package com.colak.springtutorial.service;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface ProxyServices {

    ResponseEntity<Object> doProxy(HttpServletRequest request);

}
