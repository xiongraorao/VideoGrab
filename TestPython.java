import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;  

public class TestPython{

	static class PrintThread extends Thread
	{
		private BufferedReader in;
		
		public PrintThread(BufferedReader in)
		{
			this.in = in; 
		}
		
		public void run()
		{
			 String line;  
			 try
			 {
             while ((line = in.readLine()) != null) {  
                 System.out.println(line);  
             }  
             in.close();
			 }catch(IOException e)
			 {
				 e.printStackTrace();
			 }
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			 try{  
	             System.out.println("start");  
	             String argStr = "python ";
	             for(int i = 0;i < args.length; i++)
	             {
	            	 argStr += args[i]+" ";
	             }
	             Process pr = Runtime.getRuntime().exec(argStr);  
	             
	             BufferedReader in = new BufferedReader(new  
	                     InputStreamReader(pr.getErrorStream()));  
	             new PrintThread(in).start();
	             in = new BufferedReader(new  
	                     InputStreamReader(pr.getInputStream())); 
	             new PrintThread(in).start();
	             pr.waitFor();  
	             System.out.println("end");  
	     } catch (Exception e){  
	                 e.printStackTrace();  
	             }  
	     }  
}
