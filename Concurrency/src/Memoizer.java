import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer<K, V> implements Computable<K, V> {

	private final ConcurrentMap<K, Future<V>> cache = new ConcurrentHashMap<K, Future<V>>();
	private final Computable<K, V> computableImpl;
	
	public Memoizer(Computable<K, V> c) {
		
		this.computableImpl = c;
	}
	
	@Override
	public V Compute(final K arg) throws Exception {
		
		Future<V> computeFuture = cache.get(arg);
		if(computeFuture == null) {
			Callable<V> eval = new Callable<V>(){

				@Override
				public V call() throws Exception {
					// TODO Auto-generated method stub
					return computableImpl.Compute(arg);
				}
				
			};
			
			FutureTask<V> f = new FutureTask<V>(eval);
			computeFuture = cache.putIfAbsent(arg, f);
			
			if(computeFuture == null) {
				computeFuture = f;
				f.run();
			}
		}
		
		try{
			return computeFuture.get();
		} catch(CancellationException e){
			cache.remove(arg, computeFuture);
		} catch (ExecutionException e) {
			throw new Exception(e.getCause());
		}
		
		return null;
	}

}
