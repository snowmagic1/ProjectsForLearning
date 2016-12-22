import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Hash {

	private ConcurrentMap<Long, String> _circle = new ConcurrentHashMap<Long, String>();
	private List<Long> _sortedHashes;
	private int replica = 20; 
	
	public void add(String Server) {
		for(int i=0;i<replica;i++){
			this._circle.putIfAbsent(this.computeHash(i + Server + i), Server);
		}
		
		this.updateSortedHashes();
	}
	
	public void remove(String Server) {
		if(this._circle.remove(Server) != null) {
			this.updateSortedHashes();
		}
	}
	
	public String get(String Key) {
		
		if(this._circle.isEmpty()) {
			return "";
		}
		
		Long keyHash = this.computeHash(Key);
		int size = this._sortedHashes.size();
		for(int i=0;i<size;i++) {
			if(this._sortedHashes.get(i) > keyHash) {
				return this._circle.get(this._sortedHashes.get(i));
			}
		}
		
		return this._circle.values().iterator().next();
	}
	
	private void updateSortedHashes() {
		List<Long> hashes = new ArrayList<Long>();
		hashes.addAll(_circle.keySet());
		Collections.sort(hashes);
		
		_sortedHashes = hashes;
	}
	
	private Long computeHash(String val) {
		long h = 1125899906842597L; // prime
	    int len = val.length();

	    for (int i = 0; i < len; i++) {
	      h = 31*h + val.charAt(i);
	    }
	    
	    return h;
	}
	
	public static void main(String[] args) {
		Hash hash = new Hash();
		hash.add("server1");
		hash.add("server2");
		hash.add("server3");
		
		for(int i=0;i<10;i++) {
			String key = i + "_key" + i;
			System.out.println(String.format("[%s] -> [%s]", key, hash.get(key)));
		}
		
		hash.add("server4");
		hash.add("server5");
		
		System.out.println();
		for(int i=0;i<10;i++) {
			String key = i + "_key" + i;
			System.out.println(String.format("[%s] -> [%s]", key, hash.get(key)));
		}
		
		hash.remove("server4");
		
		System.out.println();
		for(int i=0;i<10;i++) {
			String key = i + "_key" + i;
			System.out.println(String.format("[%s] -> [%s]", key, hash.get(key)));
		}
	}

}
