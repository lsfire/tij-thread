package com.liushao.tij_thread.model;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Chopstick{
	private boolean taken = false;
	
	public synchronized void take() throws InterruptedException{
		while(taken){//the chopstick has already been taken
			wait();
		}
		taken = true;//taken this chopstick
	}
	
	public synchronized void drop(){
		taken = false;
		notifyAll();
	}	
}

class Philosopher implements Runnable{
	private Chopstick left;
	private Chopstick right;
	private  int  id;
	private  int ponderFactor;
	Random rand = new Random(47);
	
	public Philosopher(Chopstick left,Chopstick right,int idn,int ponderFactor) {
		this.left = left;
		this.right = right;
		this.id = idn;
		this.ponderFactor = ponderFactor;
	}

	@Override
	public void run() {
		while(!Thread.interrupted()){
			try {
				System.out.println(this + " " + "thinking");
				pause();
				System.out.println(this + " grabing right");
				right.take();
				System.out.println(this +  " grabbing left");
				left.take();
				System.out.println(this + " eating");
				pause();
				right.drop();
				right.drop();
				
				
			} catch (InterruptedException e) {
				System.out.println(this +  " quit via InterruptedException");
			}
		}
	}
	
	private void pause() throws InterruptedException{
		if(ponderFactor == 0) return;
		TimeUnit.MILLISECONDS.sleep(rand.nextInt(ponderFactor * 250));
	}
	
	@Override
	public String toString() {
		return "Philosopher " + id;
	}
}

public class PhilosopherModel {
	public static void main(String[] args) throws InterruptedException {
		int ponder = 5,size = 5;
		Chopstick[] chopsticks = new Chopstick[size];
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0 ;i < size;i++){
			chopsticks[i] = new Chopstick();
		}
		
		for(int i = 0;i < size;i++ ){
			exec.execute(new Philosopher(chopsticks[i % size], chopsticks[(i + 1) % size], i, ponder));
		}
		
		TimeUnit.SECONDS.sleep(5);
		exec.shutdownNow();
		
	}
}
