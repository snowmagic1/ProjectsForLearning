import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Push {
	
	class Message {
		LocalDateTime TimeStamp;
		String Name;
		String Message;
	}
	
	private BlockingQueue<Message> Messages;
	private ConcurrentMap<String, ArrayList<Message>> Feeds;
	private ConcurrentMap<String, ArrayList<String>> Followings;
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    
	public Push() {
		Messages = new ArrayBlockingQueue<Message>(20);
		Followings = new ConcurrentHashMap<String, ArrayList<String>>();
		Feeds = new ConcurrentHashMap<String, ArrayList<Message>>();
	}
	
	public void Start() {
		executor.submit(new Runnable(){

			@Override
			public void run() {
				while(true) {
					try {
						Message msg = Messages.take();
						System.out.println("new message");
						ArrayList<String> followings = Followings.get(msg.Name);
						
						followings.forEach(f -> {
							ArrayList<Message> feeds = Feeds.get(f);
							feeds.add(msg);
						});
						
					} catch (InterruptedException e) {
						return;
					}
				}
			}
			
		});
	}
	
	public void Stop() {
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.shutdownNow();
	}
	
	public void NewUser(String Name) {
		Followings.putIfAbsent(Name, new ArrayList<String>());
		Feeds.putIfAbsent(Name, new ArrayList<Message>());
	}
	
	public void NewMessage(String Name, String Message) throws InterruptedException {
		Message msg = new Message();
		msg.TimeStamp = LocalDateTime.now();
		msg.Message = Message;
		msg.Name = Name;
		
		Messages.put(msg);
	}
	
	public void Follow(String Follower, String Followed) {
		if(Follower == Followed) {
			return;
		}
		
		if(!Followings.containsKey(Follower)){
			return;
		}
		
		ArrayList<String> followings = Followings.get(Follower);
		if(followings.contains(Followed)) {
			return;
		}
		followings.add(Followed);
	}
	
	public ArrayList<Message> Latest(String Name) {
		return Feeds.get(Name);
	}
	
	public static void main(String[] args) {
		Push pushModel = new Push();
		pushModel.Start();
		
		for(int i=0;i<5;i++) {
			pushModel.NewUser("user" + i);
		}
		
		for(int i=0;i<20;i++) {
			int follower = (int)(Math.random()*5);
			int followed = (int)(Math.random()*5);
			
			pushModel.Follow("user" + follower, "user" + followed);
		}
		
		for(int i=0;i<100;i++){
			int index = (int)(Math.random()*5);
			
			try {
				pushModel.NewMessage("user" + index, "Message" + i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pushModel.Latest("user1").forEach(m -> {
			System.out.println(String.format("[%s] [%s] - %s", 
					m.TimeStamp.toString(),
					m.Name,
					m.Message));
		});
		
		pushModel.Stop();
	}

}
