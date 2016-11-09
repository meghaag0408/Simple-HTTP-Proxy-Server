import java.io.InputStream;
import java.util.ArrayList;
import java.io.*;
import java.net.*;


class MultiThreads extends Thread 
{

    private final Socket clientSocket;
    public static int port=80;

    public MultiThreads(Socket socket) 
    {
        this.clientSocket = socket;
    }
    
  

    public void run() 
    {
        try 
        {
            // Read request
        	byte[] b = new byte[8196];
        	byte[] bnew = new byte[8196];
        	InputStream incommingIS = clientSocket.getInputStream();
            int len = 0, currentlen=0, prev;
         
            String req_try = "";
            boolean endreq = false;
            while(!endreq)
            {
                prev= currentlen;
            	currentlen = incommingIS.read(bnew);
            	int j=0;
                for(int i=prev; i<prev+currentlen; i++)
                {
                	b[i] = bnew[j];
                	j++;
                }  
                req_try+=new String(bnew, 0, currentlen);
                len = len + currentlen;
                if (req_try.contains("\r\n\r\n"))
                {
                    endreq = true;
                }
            }

            if (len > 0) 
            { 
                String req=req_try;
                String local[] = new String[] {"localhost", "127.0.0.1", "iiit.ac.in", ".iiit.ac.in","iiit.net", ".iiit.net" };
                
                String path = null;
                String[] patharray = req.split(" ");
                path = patharray[1];
                URL url_finding = new URL(path);
            	String host = url_finding.getHost();
            	String url = host+url_finding.getFile();
            	int port_new = url_finding.getPort();
            	if(port_new!=-1)
            		port = port_new;
	             if(url.indexOf('/')!=url.length()-1)
	            	{
	            		url = url+"/";
	            	}
            	
                Socket socket=null;
                OutputStream incommingOS = clientSocket.getOutputStream();
                
               if(HttpServer.hashmap.containsKey(url))
                {
            	   ArrayList<Byte> valuelist = (ArrayList<Byte>) HttpServer.hashmap.get(url);
               		System.out.println("## "+url+" ## Found in cache ##");
                	for(int i=0; i<valuelist.size(); ++i)
                	{
                		int j=0;
                		for (; j < 8196; ++j) 
                			{
                				int size = valuelist.size();
                				if(i==size)
                					break;
                	         	b[j] = valuelist.get(i++);
                			}
                		incommingOS.write(b, 0, b.length);
                	}
              
                }
                else
                {
                	System.out.println("!! Not Found in Cache: "+url+" @Requesting Server");
                    if(HttpServer.choice==1)
                    {
                    	if(host.contains(local[0]) || host.contains(local[1]) || host.contains(local[2]) || host.contains(local[3])|| host.contains(local[4])  || host.contains(local[5]))
                        	socket= new Socket(host, port);
                        else
                        	socket= new Socket("proxy.iiit.ac.in", 8080);
                    	
                    }
                    else
                    {
                    	if(host.contains(local[0]) || host.contains(local[1]) || host.contains(local[2]) || host.contains(local[3])|| host.contains(local[4])  || host.contains(local[5]))
                        	socket= new Socket(host, port);
                    }
                	
                    InputStream outgoingIS = socket.getInputStream();
                    ArrayList<Byte> incoming_data = new ArrayList<Byte>();
                    OutputStream outgoingOS = socket.getOutputStream();
                    outgoingOS.write(b, 0, len);
                    int firstreplypacketflag=0, successflag = 0, nocacheflag=0;
                    String reply_status=null;
                    int length;
                    for (;(length = outgoingIS.read(b)) != -1;) 
                    {
                    	
                    	reply_status=new String(b, 0, len);
                    	String cache_status = null;
                    	if(firstreplypacketflag==0)
                        {	
                    		int in = reply_status.indexOf("\n");
                    		if(in!=-1)
                    			reply_status = reply_status.substring(0, in);
                    		int space = reply_status.indexOf(" ");
                    		if(space!=-1)
                    			reply_status = reply_status.substring(space+1);
                        	if(reply_status.contains("200"))
                        		successflag=1;
                        	else if ((reply_status.contains("30")))
                        		successflag=1;
                        	firstreplypacketflag=1;
                        }
                    	if(reply_status.contains("Cache-Control"))
            			{
                    		cache_status = reply_status;
                    		int cindex;
                    		if((cindex = cache_status.indexOf("Cache-Control"))!=-1)
                    		{
                    			cache_status = cache_status.substring(cindex);
                    			int end;
                    			if((end = cache_status.indexOf("\n"))!=-1)
                    				cache_status = cache_status.substring(0, end);
                    			else
                    				cache_status = cache_status.substring(0, cache_status.length());
                    			
                        		System.out.println(cache_status);
                        		if(cache_status.contains("private") || cache_status.contains("no-cache"))
                        				{
	                        			nocacheflag=1;
	                        			System.out.println("Url Not Cacheable");
	                        				
                        				}
                    		}
                    		
            			}
                    	else if(reply_status.contains("Pragma"))
                    			{
	                    		cache_status = reply_status;
	                    		int cindex;
	                    		if((cindex = cache_status.indexOf("Pragma"))!=-1)
	                    		{
	                    			cache_status = cache_status.substring(cindex);
	                    			int end;
	                    			if((end = cache_status.indexOf("\n"))!=-1)
	                    				cache_status = cache_status.substring(0, end);
	                    			else
	                    				cache_status = cache_status.substring(0, cache_status.length());
	                    			
	                        		System.out.println(cache_status);
	                        		if(cache_status.contains("private") || cache_status.contains("no-cache"))
	                        				{
		                        			nocacheflag=1;
		                        			System.out.println("Url Not Cacheable");
		                        				
	                        				}
	                    		}	
		                    		
                    			}
                        
                        if(successflag==1)
                        	{
                        	for(int i=0; i<length; i++) 
	    	                    {
	    	                    	  incoming_data.add(new Byte(b[i]));
	    	                    }
                        	incommingOS.write(b, 0, length);
                        	}
                        else
	                        {
                        	byte[] error_code = null;
                        	StringBuilder htmlBuilder = new StringBuilder();
                        	htmlBuilder.append("<html><head><title>Bad Response</title></head><body><h1>");
                        	htmlBuilder.append(reply_status);
                        	htmlBuilder.append("</h1></body></html>");
                        	String html = htmlBuilder.toString();
                        	error_code = html.getBytes();
                        	successflag=0;
                        	incommingOS.write(error_code, 0, error_code.length);	
	                        break;	
	                        
	                        }
                      }

                   if(nocacheflag!=1 && successflag==1) 
                		   HttpServer.hashmap.put(url, incoming_data);
            
                
                   socket.close();
                   outgoingIS.close();
                   outgoingOS.close();
                }
               	
               incommingOS.close(); 
               incommingIS.close(); 
                
            } 
            else 
            {
                incommingIS.close();
            }
        } 
        catch (IOException e) 
        {
           // System.out.println("Not able to run thread");
        } 
        finally 
        {
            try 
            {clientSocket.close();
            } catch (IOException e) 
            {e.printStackTrace();
            }
        }
    }
}