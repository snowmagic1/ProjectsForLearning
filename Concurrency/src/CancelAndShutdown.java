import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class CancelAndShutdown {

	public static void main(String args[]) throws InterruptedException {
	    ExecutorService executor = Executors.newFixedThreadPool(1);
	    Callable<Object> r = new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				
	            while (true) {
	                if (Thread.currentThread().isInterrupted()) {
	                	System.out.println("interrupted");
	                	break;
	                }
	            }
	            
				return null;
			}
	    };
	    
	    FutureTask<Object> ft = (FutureTask<Object>) executor.submit(r);

	    System.out.println("submit");
	    Thread.sleep(1000);
	    
	    ft.cancel(true);
	    System.out.println("cancel");
	    
	    // Thread.sleep(1000);
	    /*
	    executor.shutdown();
	    System.out.println("shutdown");
	    
	    executor.shutdownNow();
	    System.out.println("shutdownNow");
	    if (!executor.awaitTermination(100, TimeUnit.MICROSECONDS)) {
	        System.out.println("Still waiting...");
	        System.exit(0);
	    }
	    */
	    System.out.println("Exiting normally...");
	}
}
