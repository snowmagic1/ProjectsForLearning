import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Hash {

	private ConcurrentMap<Long, String> _circle = new ConcurrentHashMap<Long, String>();
	private List<Long> _sortedHashes;
	private int replica = 10; 
	
	public void add(String Server) throws NoSuchAlgorithmException {
		for(int i=0;i<replica;i++){
			this._circle.putIfAbsent(this.computeHash(i + Server + i), Server);
		}
		
		this.updateSortedHashes();
	}
	
	public void remove(String Server) {
		this._circle.forEach( (key, val) -> {
			if(val == Server) {
				this._circle.remove(key, val);
			}
		});
		
		this.updateSortedHashes();
	}
	
	public String get(String Key) throws NoSuchAlgorithmException {
		
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
	
	private Long computeHash(String val) throws NoSuchAlgorithmException {		
		MessageDigest messageDigest = null;
		messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(val.getBytes());
		ByteBuffer bb = ByteBuffer.wrap(messageDigest.digest());
		return bb.getLong();
	}
	
	public static void main(String[] args) {
		try{
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
		} catch(Exception ex) {
			System.out.println(ex);
		}
	}

}
