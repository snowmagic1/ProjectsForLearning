import java.util.Arrays;

public class VolatileCachedFactorizer {

	OneValueCache cache = new OneValueCache(0, null);
	
	public long[] service(long f) {

		long[] ff = cache.Get(f);
		if(ff == null) {
			ff = factor(f);
			cache = new OneValueCache(f, ff);
		}
		
		return ff;
	}
	
	private long[] factor(long f) {
		long[] ff = new long[3];
		for(int i=0;i<3;i++) {
			ff[i] = f - i;
		}
        
		return ff;
	}
	
	class OneValueCache {

		private long lastNumber = 0;
		
		private long[] factors;

		public OneValueCache(long f, long[] ff) {
			lastNumber = f;
			if(ff != null) {
				factors = Arrays.copyOf(ff, ff.length);
			}
		}
		
		public long[] Get(long f) {
			if(lastNumber != f) {
				return null; 
			}
			
			return Arrays.copyOf(factors, factors.length);
		}
	}
}
