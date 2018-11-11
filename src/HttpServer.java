import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/***
 * 
 * @author junhanliu
 * 
 * Simple HTTP server supports four kinds of web content response on port 8889.
 * 1, request of HTML web document
 * 2, request of image document
 * 3, request of Video
 * 4, request of audio
 * Otherwise, server respond 404 message to client.
 * 
 * Run the program, then open any browser as you please, type 'localhost:8889/xxx.xxx' to request resources.
 * 
 * For video and audio, they are loaded from remote server, and embedded in HTML document rather 
 * than read stream from local files.
 */

public class HttpServer implements Runnable {

    private ServerSocket serverSocket;
    public static int PORT=8889;

    public HttpServer() {
        try {
            serverSocket=new ServerSocket(PORT);
        } catch(Exception e) {
            System.out.println("Unable to initializa HTTP server:"+e.getLocalizedMessage());
        }
        if(serverSocket==null) {
        		System.exit(1);
        }
        new Thread(this).start();
        System.out.println("HTTP server is running on port:"+PORT);
    }
    
    /**
     * Start listening on port 8889, and respond HTTP resource;
     */
    public void run() {
        while(true) {
            try {
                Socket client=null;
                client=serverSocket.accept();
                if(client!=null) {
                    System.out.println("\nClient connected to server:"+client);
              
                    BufferedReader in=new BufferedReader(new InputStreamReader(
                            client.getInputStream()));
       
                    String line=in.readLine();
                    String resource = this.get_resource(line);
                    String method = this.get_method(line);

                    while( (line = in.readLine()) != null) {
                        if(line.equals("")) break;  
                    }
                    
                    System.out.println("Resource client has requested:"+ resource);
                    System.out.println("Type of request: " + method);
                    this.response(resource, method, client);  
                }
            } catch(Exception e) {
                System.out.println("HTTP server error:"+e.getLocalizedMessage());
            }
        }
    }
    
    /**
     * Fetch requested resource URL from URL and return;
     * @param line :(String) HTTP request string contains HTTP method, resource requested, and HTTP version;
     * @return resource :(String) HTTP resource 
     * @throws UnsupportedEncodingException
     */
    public String get_resource(String line) throws UnsupportedEncodingException {
    		String resource=line.substring(line.indexOf('/')+1,line.lastIndexOf('/')-5);
    		resource=URLDecoder.decode(resource, "UTF-8");
    		return resource;
    }
    
    /**
     * Fetch HTTP method from input string;
     * @param line :(String) HTTP request string contains HTTP method, resource requested, and HTTP version;
     * @return method :(String) HTTP method
     */
    public String get_method(String line) {
    		String method = new StringTokenizer(line).nextElement().toString();
    		return method;
    }
    
    /**
     * Checking requested resource, if resource exists, then respond to client. else respond 404;
     * @param resource :(String) HTTP requested resource
     * @param method :(String) HTTP method
     * @param client : Socket
     * @throws IOException
     */
    public void response(String resource, String method, Socket client) throws IOException {
    		String ROOT = this.getClass().getResource("WebContent/").getFile();
    		switch( resource ) {
	    		case "logo.jpg":	 
	    			String path = URLDecoder.decode(ROOT+"image.jpg", "UTF-8");
	    			fileService(path, client, "JPG");
	    			break;
	    		case "index.html":
	    			String path2 = URLDecoder.decode(ROOT+"index.html", "UTF-8");
	    			fileService(path2, client, "Content-Type: text/html;charset=UTF-8");
	            	break;
	    		case "movie.mp4":
	    			play_video(client);
	            break;
	    		case "audio.mp3":
	    			play_audio(client);
	        		break;
	        	default:
	        		PrintStream out = new PrintStream(client.getOutputStream(), true);
				String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type: text/html\r\n"
                        + "Content-Length: 23\r\n" + "\r\n" + "<h1>OOPS. 404 Not Found</h1>";
                out.write(errorMessage.getBytes());
                out.close();
    		}
    		closeSocket(client);
    }
    
     /**
      * Close socket;
      * @param socket :Socket
      */
    void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(socket);        
    }
    
    /**
     * Render HTML document. Remote video embedded in the document.
     * @param socket :Socket
     * @throws IOException
     */
    public void play_video(Socket socket) throws IOException{  
    		PrintStream out = new PrintStream(socket.getOutputStream(), true);
        StringBuilder sb = new StringBuilder("<html><head><title>Video</title>");  
          
        sb.append("<meta http-equiv='Content-Type' content='text/html;charset=utf-8'></head>");  
        sb.append("<body>");     
        sb.append("<center><video width=\"960\" height=\"720\" controls>\n" + 
        		"  <source src=\"http://junhanliu.com/video.mp4\" type=\"video/mp4\">\n" + 
        		"Your browser does not support the video tag.\n" + 
        		"</video></center>");  
        sb.append("</body></html>");  
        out.println("HTTP/1.1 200");  
        out.println();  
        out.print(sb.toString());  
        out.flush();  	
        out.close();
    }  
    
    /**
     * Render HTML document. Remote audio embedded in the document.
     * @param socket :Socket
     * @throws IOException
     */
    public void play_audio(Socket socket) throws IOException{  
		PrintStream out = new PrintStream(socket.getOutputStream(), true);
    StringBuilder sb = new StringBuilder("<html><head><title>Audio</title>");  
      
    sb.append("<meta http-equiv='Content-Type' content='text/html;charset=utf-8'></head>");  
    sb.append("<body>");     
    sb.append("<center><audio controls=\"controls\">\n" + 
    		"  <source src=\"http://junhanliu.com/music.mp3\" type=\"audio/mpeg\">\n" + 
    		"Your browser does not support the audio tag.\n" + 
    		"</audio></center>");  
    sb.append("</body></html>");  
    out.println("HTTP/1.1 200");  
    out.println();  
    out.print(sb.toString());  
    out.flush();  	
    out.close();
}  

    /**
     * respond resource to client;
     * @param fileName :(String) requested resource location
     * @param socket :Socket
     * @param ConTentType :requested resource content type
     * @throws IOException 
     */
    void fileService(String fileName, Socket socket,String ConTentType) throws IOException
    {
        
        PrintStream out = new PrintStream(socket.getOutputStream(), true);
        File fileToSend = new File(fileName);
        System.out.println(fileToSend);
        System.out.println(fileToSend.exists());
        if(fileToSend.exists() && !fileToSend.isDirectory())
        {
            out.println("HTTP/1.0 200 OK");
            out.println(ConTentType);   
            out.println("Content-Length: " + fileToSend.length());
            out.println();

            FileInputStream fis = new FileInputStream(fileToSend);
            byte data[] = new byte[fis.available()];
            fis.read(data);
            out.write(data);
            out.close();
            fis.close();
        }else {
        		String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type: text/html\r\n"
                    + "Content-Length: 23\r\n" + "\r\n" + "<h1>OOPS. 404 Not Found</h1>";
            out.write(errorMessage.getBytes());
            out.close();
        }
     
    }


    public static void main(String[] args) {
        try {
            if(args.length == 1) {
                PORT = Integer.parseInt(args[0]);
            }
        } catch (Exception ex) {
            System.err.println("Invalid port arguments. It must be a integer that greater than 0");
        }
        
        new HttpServer();   
    }
}