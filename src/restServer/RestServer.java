/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restServer;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestServer {


    File keyValPairs;
    String type;
    boolean success;
    Map<String, String> parameters = new HashMap<>();
    //ArrayList<Map<String,String>> keyVals;
    
    /*public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/store", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }*/
    
    
    public RestServer() throws IOException{
        this.parameters=new HashMap<>();
        this.keyValPairs=new File("keyVals");
        if(!(this.keyValPairs.exists()))
        {
            try {
                this.keyValPairs.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(RestServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            BufferedReader br = new BufferedReader(new FileReader(keyValPairs));
            String line;
            while((line=br.readLine())!=null)
            {
                    String[] value=line.split("=");
                    if(value.length>1) {
                        parameters.put(value[0], value[1]);
                    }
                }
            }
        this.type=null;
        this.success=false;
        this.start();
    }
    
    private void start() throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/store", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    private String parseQuery(String query)
    {
        String result=new String();
        if(query!=null)
        {
            String[] pairs=query.split("&");
                //String[] param=pair.split("=");
                String key=null;
                String value=null;
                try {
                    if (pairs.length > 1) {
                        String[] param=pairs[0].split("=");
                         key = URLDecoder.decode(param[1],System.getProperty("file.encoding"));
                         param=pairs[1].split("=");
                         if(param.length>1) {
                            value = URLDecoder.decode(param[1],System.getProperty("file.encoding"));
                            put(key,value);
                            if(success) {
                                 return "ok";
                             }
                         }else {
                             return "  <head>\n" +
                                     "<meta http-equiv=\"refresh\" content=\"3;URL=http://localhost:8000/store\">\n" +
                                     "</head> You entered no value for the key";
                         }
                    }

                    else if (pairs.length > 0) {
                        String[] param=pairs[0].split("=");
                         key = URLDecoder.decode(param[1],System.getProperty("file.encoding"));
                         result=get(key);
                         if(success) {
                            return result;
                         }
                         
                     }
                    else {
                        this.success=false;
                        return "You entered something wrong";
                    }
                }catch(UnsupportedEncodingException e)
                {
                    this.success=false;
                    return "You used unsupported encoding";
                }
            }
            return result;
    }

    private String get(String key) {
        String result=new String();
        if(parameters.containsKey(key)) {
            this.success=true;
            this.type="get";
            return parameters.get(key);
        }
        else
            return "Key not found";
    }
    
    private void put(String key, String value) {
        String result=null;
        try {
            PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(keyValPairs, true)));
            out.print(key+"="+value+"\n");
            out.close();
            parameters.put(key, value);
            this.type="put";
            this.success=true;
        } catch (IOException ex) {
            Logger.getLogger(RestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //test
     class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String result="";
            String response="";
            String request=t.getRequestURI().getQuery();
            if(request!=null){
                result = parseQuery(request);
                if(success && type.equalsIgnoreCase("put")) {
                  response="  <head>\n" +
                          "<meta http-equiv=\"refresh\" content=\"3;URL=http://localhost:8000/store\">\n" +
                          "</head> "+"All went ok";
                }
                else if(success && type.equalsIgnoreCase("get")) {
                    response="  <head>\n" +
                            "<meta http-equiv=\"refresh\" content=\"3;URL=http://localhost:8000/store\">\n" +
                            "</head> "+result;
                }
                else if(!(success)) {
                    response="  <head>\n" +
                            "<meta http-equiv=\"refresh\" content=\"3;URL=http://localhost:8000/store\">\n" +
                            "</head> "+result;
                }
            }
            else {
                response="<!DOCTYPE html>\n" +"<html>\n" +"<body>\n" +"\n" +
                        "<form action=\"\" method=\"get\">\n" +"Key:<br>\n" +
                        "<input type=\"text\" name=\"key\" value=\"\">\n" +"<br>\n" +
                        "Value:<br>\n" +"<input type=\"text\" name=\"value\" value=\"\">\n" +
                        "<br><br>\n" +"<input type=\"submit\" value=\"Submit\">\n" +"</form> \n" +
                        "<form action=\"\" method=\"get\">\n" +"Key:&nbsp;\n" +
                        "<input type=\"text\" name=\"key\" value=\"\"><br>"+
                        "<input type=\"submit\" value=\"Get me my value\">\n" +"</form>"+
                        "\n" +"</body>\n" +"</html>";
            }
            t.sendResponseHeaders(400, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}