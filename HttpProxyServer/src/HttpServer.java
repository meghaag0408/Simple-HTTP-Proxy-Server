import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.ServerSocket;


public class HttpServer
{
	public static ConcurrentHashMap<String, ArrayList<Byte>> hashmap = new ConcurrentHashMap<String, ArrayList<Byte>>();
	public static boolean listeni = false;
	public static int choice =1;
	public static void main(String[] args) throws IOException 
	{
		
		//Intialising variables
		int port = 0;
		ServerSocket serverSocket = null;
        boolean listening;
        listening = true;
        hashmap.clear();
 
        if(args.length!=2)
	        {
        	System.out.println("Bind Error!! Try Some other Port");   	
	        }
        else
        	port=Integer.parseInt(args[0]);
    	
        try 
        {
        	boolean listenig = true;
            serverSocket = new ServerSocket(port);
        } 
        catch (IOException e) 
        {
            System.out.println("Invalid Port, Try Some Other Port");
            return;
        }
        choice = Integer.parseInt(args[1]);
        
        while (listening==true) 
        {
            new MultiThreads(serverSocket.accept()).start();            
        }
        serverSocket.close();
	}

}

