package com.liushao.tij_thread.basic;

public class Joining {
	public static void main(String[] args) {
		Sleeper sleepy = new Sleeper("sleepy", 1500),
		grumpy = new Sleeper("grumpy", 1500);
		
		joinner dopey = new joinner("dopey", sleepy),
				doc = new joinner("doc", grumpy);
		
		grumpy.interrupt();
	}

}


class Sleeper extends Thread{
	private int duration;
	
	public Sleeper(String name,int sleepTime) {
		super(name);
		this.duration = sleepTime;
		start();
	}
	
	@Override
	public void run() {
			try {
				sleep(duration);
			} catch (InterruptedException e) {
				System.out.println(getName() + " was interrupt." + " isInterrupt:" + isInterrupted());
				return;
			}
			
			System.out.println(getName() + " has awakened");
	}
}

class joinner extends Thread{
	
	private Sleeper sleeper;
	
	public joinner(String name,Sleeper sleeper){
		super(name);
		this.sleeper = sleeper;
		start();
	}
	
	@Override
	public void run() {
		try {
			//blocked until sleeper finish task
			sleeper.join();
		} catch (InterruptedException e) {
			System.out.println("joiner " + getName() + " interrupt");
		}
		
		System.out.println("joinner " + getName() + " completed");
	}
	
}