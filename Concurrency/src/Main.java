import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {

	private static Random rand = new Random();
	private static Factorizer cf = new Factorizer();
	private static ExecutorService executor = Executors.newFixedThreadPool(20);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		IntStream.range(0, 10).forEach(i -> executor.submit(
			() -> {
				long f = 3; 
		    	
		    	if(0 == rand.nextInt(10)%2) {
		    		f = 5;
		    	}
		    	
		    	Long[] ff;
				try {
					ff = cf.service(Long.valueOf(f));
					
			        System.out.println(String.format("[%d] => [%d %d %d]", 
			        		f, ff[0], ff[1], ff[2]));
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}));
	}
}
