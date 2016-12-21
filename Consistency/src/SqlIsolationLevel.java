import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqlIsolationLevel {

    private ExecutorService executor = Executors.newFixedThreadPool(20);
    
	class DirtyWrite {
		
		private int x;
		private int y;
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				double r = Math.random();
				x = (int)(r * 100);

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				y = (int)(r * 100);
			}
		};
		
		public DirtyWrite(){};
		
		public void Run() throws InterruptedException {
			while(true) {
				executor.submit(r);
				// System.out.println(String.format("[%d] - [%d]", x, y));
				
				if(x != y) {
					System.out.println(String.format("dirty write [%d] != [%d]", x, y));
					return;
				}
				
				Thread.sleep(10);
			}
		}
	}
	
	class DirtyRead {
		
		private int x = 100;
		private int y;
		
		private Runnable createRunnable(final int amount){
			
			Runnable transfer = new Runnable() {
	
				@Override
				public void run() {
	
					x -= amount;
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					y += amount;
				}
			};
			
			return transfer;
		}
		public DirtyRead(){};
		
		public void Run() throws InterruptedException {
			while(true) {
				int amount = (int)(Math.random() * 10);
				executor.submit(createRunnable(amount));
				
				if(x + y != 100) {
					System.out.println(String.format("dirty read [%d] [%d]", x, y));
					return;
				}
				
				Thread.sleep(10);
			}
		}
	}

	class NonRepeatableRead {
		
		private int x = 100;
		
		private Runnable createRunnable(final int amount){
			
			Runnable transfer = new Runnable() {
	
				@Override
				public void run() {
	
					int tempX = x;
					
					x -= amount;
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					if(x != tempX) {
						System.out.println(String.format("Read stale data"));
					}
				}
			};
			
			return transfer;
		}
		public NonRepeatableRead(){};
		
		public void Run() throws InterruptedException {
			while(true) {
				int amount = (int)(Math.random() * 10);
				executor.submit(createRunnable(amount));
				
				if(x < 0) {
					System.out.println("return");
					return;
				}
				
				Thread.sleep(10);
			}
		}
	}

	class Phantom {
		
		private List<Integer> list = new ArrayList<Integer>();
			
		Runnable check = new Runnable() {

			@Override
			public void run() {

				int s = list.size();
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				list.add(1);
				
				if(s + 1 != list.size()) {
					System.out.println(String.format("Phantom [%d] != [%d]", s, list.size()));
				}
			}
		};
		
		public void Run() throws InterruptedException {
			while(true) {
				executor.submit(check);
				
				if(list.size() > 2000) {
					System.out.println("return");
					return;
				}
				
				// Thread.sleep(10);
			}
		}
	}
	
	private void Execute() {
		try {
			// new DirtyWrite().Run();
			// new DirtyRead().Run();
			// new NonRepeatableRead().Run();
			new Phantom().Run();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new SqlIsolationLevel().Execute();
	}
}
