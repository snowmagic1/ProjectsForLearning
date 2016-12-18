import java.util.concurrent.ExecutionException;

public class Factorizer {
	
	private final Computable<Long, Long[]> c = new Computable<Long, Long[]>(){

		@Override
		public Long[] Compute(Long arg) throws InterruptedException, ExecutionException, Exception {
			return factor(arg);
		}
		
		private Long[] factor(Long f) {
			Long[] ff = new Long[3];
			for(int i=0;i<3;i++) {
				ff[i] = f - i;
			}
	        
			return ff;
		}
		
		
	};
	
	private final Memoizer<Long, Long[]> cache = new Memoizer<Long, Long[]>(c);
	
	public Long[] service(Long f) throws Exception {
		return cache.Compute(f);
	}
}
