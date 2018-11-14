# JavaHTTPServer
A simple Http Server written in Java using Socket.


## Instruction

This is a simple HTTP server written in Java. The server keeps accepting HTTP request on port 8889 and respond according resources. Server will not close unless it is terminated from the server side, otherwise, it keeps waiting for further requests.

## To Run

-   locate `/src`.
    
-   For MAC and Windows, in your cmd or terminal, type `“javac HttpServer.java”` to compile.
- Then `“java HttpServer”` to run. In cmd, Server will display according status indication messages.
    
-   Open Chrome or Safari or whatever browser you please, type `“localhost:8889”` and append according resource you please such as `“localhost:8889/index.html”` to see the responded web content.
    
## Release Notes
Currently the server supports four resource requests:
    
  1.  request of index.html
        
   2.  request of logo.jpg
        
   3.  request of audio.mp3
        
   4.  request of movie.mp4
        
    
   Clear resource HTTP requests must be specified by clients. For example, if clients request “localhost:8889/logo.png” OR “localhost:8889/homepage.html”, server will respond 404 resource not found message indicating the resource cannot be located.
    
   The default respond when requesting “localhost:8889” is 404.
