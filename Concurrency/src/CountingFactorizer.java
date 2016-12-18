import java.util.concurrent.atomic.AtomicLong;

public class CountingFactorizer {

	private final AtomicLong count = new AtomicLong(0);

	private long lastNumber = 0;
	
	private long[] factors;
			
	public long get() {
		return count.get();
	}

	public long[] service(long f) {

		if(lastNumber == f) {
			return factors;
		}
		
		long[] ff = factor(f);
		lastNumber = f;
		factors = ff;
		
		return factors;
	}
	
	private long[] factor(long f) {
		long[] ff = new long[3];
		for(int i=0;i<3;i++) {
			ff[i] = f - i;
		}
		
        //System.out.println(String.format("--- [%d] => [%d %d %d]", 
        //		f, ff[0], ff[1], ff[2]));
        
		return ff;
	}
}
