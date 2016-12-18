import java.util.concurrent.ExecutionException;

public interface Computable<K, V> {

	public V Compute(K arg) throws InterruptedException, ExecutionException, Exception;
}
