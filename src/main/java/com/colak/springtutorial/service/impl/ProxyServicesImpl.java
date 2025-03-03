package com.colak.springtutorial.service.impl;

import com.colak.springtutorial.service.ProxyServices;
import jakarta.servlet.http.HttpServletRequest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Service
public class ProxyServicesImpl implements ProxyServices {

    final Logger log1=LoggerFactory.getLogger(this.getClass());

    @Override
    public ResponseEntity<Object> doProxy(HttpServletRequest request) {

        String method = request.getMethod();
        try	{

            List<String> listMethod=new LinkedList<>();
            listMethod.add("POST");
            listMethod.add("GET");
            listMethod.add("PATCH");
            listMethod.add("PUT");

            if(!listMethod.contains(method.toUpperCase()))	{
                //Method not supported
                return ResponseEntity.status(405).build();
            }


            //Collect all http headers
            Map<String, String> headers=new HashMap<>();

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();

                String headerValue = request.getHeader(headerName);
                headers.put(headerName, headerValue);
            }

            //Remove header and add new header
            headers.remove("host");
            headers.put("via", "1.1 my-proxy-server");


            //Collect all query params
            Enumeration<String> parameterNames = request.getParameterNames();
            Map<String, Object> queries=new HashMap<>();

            while (parameterNames.hasMoreElements())
            {
                String parameterName = parameterNames.nextElement();
                String parameterValue = request.getParameter(parameterName);
                queries.put(parameterName, parameterValue);
            }
            HttpResponse<byte[]> result=null;

            if(method.equalsIgnoreCase("GET"))	{
                result= Unirest.get(request.getRequestURL().toString())
                        .queryString(queries)
                        .headers(headers)
                        .asBytes();
            }
            else if(method.equalsIgnoreCase("POST"))	{
                result=Unirest.post(request.getRequestURL().toString())
                        .queryString(queries)
                        .headers(headers)
                        .body(getBody(request))
                        .asBytes();
            }
            else if(method.equalsIgnoreCase("PUT"))	{
                result=Unirest.put(request.getRequestURL().toString())
                        .queryString(queries)
                        .headers(headers)
                        .body(getBody(request))
                        .asBytes();
            }
            else if(method.equalsIgnoreCase("PATCH"))	{
                result=Unirest.patch(request.getRequestURL().toString())
                        .queryString(queries)
                        .headers(headers)
                        .body(getBody(request))
                        .asBytes();
            }

            if(result==null)	{
                return ResponseEntity.status(404).build();
            }

            HttpHeaders headersResult = new HttpHeaders();
            result.getHeaders().all().forEach(a-> headersResult.add(a.getName(), a.getValue()));


            String contentType=result.getHeaders().getFirst("content-type");

            log1.debug("Response content-type for url {}:{}",request.getRequestURI(),contentType);


            if(contentType.contains("image"))	{
                return ResponseEntity.status(result.getStatus())
                        .headers(headersResult)
                        .body(result.getBody());
            }
            else 	{
                String resultString=new String( result.getBody()).replaceAll("https://", "http://");
                return ResponseEntity.status(result.getStatus())
                        .headers(headersResult)
                        .body(resultString);
            }


        }
        catch(Exception e)	{
            log1.error("Error processing {} request {}", method,
                    request.getRequestURL().toString(),e);
            return ResponseEntity.internalServerError().build();
        }

    }

    private static String getBody(HttpServletRequest request) throws IOException	{
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);

        }
        reader.close();

        return stringBuilder.toString();
    }


}
