package DiningPhilosopher2;

import java.util.concurrent.Semaphore;

	
class Philosopher{
	
	public static void main(String[] args) {
	    String[] names = {"Plato", "Aristotle", "Cicero", "Confucius", "Eratosthenes"};
	    Fork[] fork = new Fork[5];
	    Philosophers[] philosopher = new Philosophers[5];

	    for (int i = 0; i < fork.length; i++) {
	        fork[i] = new Fork(i);
	    }

	    for (int i = 0; i < philosopher.length; i++) {

	        if (i != philosopher.length - 1) {
	            philosopher[i] = new Philosophers(fork[i], fork[i+1], names[i]);
	            philosopher[i].start();
	        } else {
	            philosopher[i] = new Philosophers(fork[0], fork[i], names[i]);
	            philosopher[i].start();
	        }
	    }
	}
	
	
	static class Fork {
		public static Semaphore fork = new Semaphore(1);
		public int id;
		
		Fork(int id) {
		    this.id = id;
		}
		
		public int getId() {
		    return id;
		}
		
		public boolean take() {
		    return fork.tryAcquire();
		}
		
		public void putDown() {
		    fork.release();
		}
	}
		
	static class Philosophers extends Thread{

	private Fork fork_low;
	private Fork fork_high;
	private String name;

	Philosophers(Fork fork_low, Fork fork_high, String name) {
	    this.fork_low = fork_low;
	    this.fork_high = fork_high;
	    this.name = name;
	}

	public void run() {
	
	    try {
	        sleep(1000);
	    } catch (InterruptedException ex) {
	    }
	
	    while (true) {
	        eat();
	    }
	}

	private void eat(){
	    if(fork_low.take()){
	        if(fork_high.take()){
	            try {
	                sleep(2000); // eating;
	            } catch (InterruptedException ex) { }
	
	            fork_high.putDown();
	            fork_low.putDown();  
	
	        }
	        else{
	            fork_low.putDown();
	        }
	    }
	}
	}
}
