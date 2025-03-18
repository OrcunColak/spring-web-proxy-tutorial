# Read Me First

The original idea is from  
https://medium.com/javarevisited/http-forward-proxy-using-spring-boot-2d90b01f4f16

```java
String url = UriComponentsBuilder
        .fromHttpUrl("https://api.example.com")
        .pathSegment("resource", "123")
        .queryParam("filter", "active")
        .toUriString();

System.out.println("Generated URL: "+url);
```

Generated URL: https://api.example.com/resource/123?filter=active
