import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Pull {

	class Message {
		LocalDateTime TimeStamp;
		String Name;
		String Message;
	}
	
	private ConcurrentMap<String, ArrayList<Message>> Messages; 
	private ConcurrentMap<String, ArrayList<String>> Followings;
	
	public Pull() {
		Messages = new ConcurrentHashMap<String, ArrayList<Message>>();
		Followings = new ConcurrentHashMap<String, ArrayList<String>>();
	}
	
	public void NewUser(String Name) {
		Followings.putIfAbsent(Name, new ArrayList<String>());
		Messages.putIfAbsent(Name, new ArrayList<Message>());
	}
	
	public void NewMessage(String Name, String Message) {
		Message msg = new Message();
		msg.TimeStamp = LocalDateTime.now();
		msg.Message = Message;
		msg.Name = Name;
		
		Messages.get(Name).add(msg);
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
	
	public ArrayList<Message> Latest(String Name, int count) {
		ArrayList<String> followings = Followings.get(Name);
		int followingSize = followings.size();
		ArrayList<ArrayList<Message>> messagesAll = new ArrayList<ArrayList<Message>>();
		
		followings.forEach(f -> messagesAll.add(Messages.get(f)));
		
		ArrayList<Message> result = new ArrayList<Message>();
		int currentCount = 0;
		int[] messageIndexes = new int[followingSize];
		for(int i = 0;i<followingSize;i++){
			messageIndexes[i] = 0;
		}
		
		while(currentCount < count) {
			Message latest = null;
			int latestIndex = -1;
			
			for(int i=0;i<followingSize;i++){
				int msgIndex = messageIndexes[i];
				ArrayList<Message> msgs = messagesAll.get(i);
				if(msgs.size() <= msgIndex) continue;
				
				Message msg = msgs.get(msgIndex);
				
				if(latestIndex == -1 ||
				   latest.TimeStamp.isAfter(msg.TimeStamp)) {
					latestIndex = i;
					latest = msg;
				}
			}
			
			if(latestIndex == -1) {
				System.out.println("out of entries");
				return result;
			}
			
			messageIndexes[latestIndex] ++;
			result.add(latest);
		}
		
		return result;
	}
	
	public static void main(String args[]) {
		Pull pullModel = new Pull();
		for(int i=0;i<5;i++) {
			pullModel.NewUser("user" + i);
		}
		
		for(int i=0;i<100;i++){
			int index = (int)(Math.random()*5);
			pullModel.NewMessage("user" + index, "Message" + i);
		}
		
		for(int i=0;i<20;i++) {
			int follower = (int)(Math.random()*5);
			int followed = (int)(Math.random()*5);
			
			pullModel.Follow("user" + follower, "user" + followed);
		}
		
		pullModel.Latest("user1", 10).forEach(m -> {
			System.out.println(String.format("[%s] [%s] - %s", 
					m.TimeStamp.toString(),
					m.Name,
					m.Message));
		});
	}
}
