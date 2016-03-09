package com.liushao.tij_thread.shared;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class SerialGenerator{
	private static volatile int serialNumber = 0;
	
	public static int nextNum(){
		return serialNumber++;
	}
}

class serrialSet{
	private int[] array;
	private int len;
	private int index ;
	
	public serrialSet(int size) {
		array = new int[size];
		len = size;
		index = 0;
		for(int i = 0;i < len;i++){
			array[i] = -1;
		}
	}
	
	public synchronized void add(int element){
		array[index] = element;
		index = (++index) % len;
	} 
	
	public synchronized boolean contains(int element){
		for(int i = 0;i < len;i++)
			if (array[i] == element) return true; 
		return false;
	}
	
}
public class SerialNumberChecker {
	private static final int SIZE = 10;
	
	private static serrialSet serrial = new serrialSet(1000);
	
	private static ExecutorService exec = Executors.newCachedThreadPool();
	
	
	static class SerialChecker implements Runnable{
		@Override
		public void run() {
			while (true) {
				int serial = SerialGenerator.nextNum();
				if(serrial.contains(serial)){
					System.out.println("Duplicate : " + serial);
					System.exit(0);
				}
				serrial.add(serial);
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		for(int i = 0;i < SIZE;i++){
			exec.execute(new SerialChecker());
		}
		exec.shutdown();
		TimeUnit.SECONDS.sleep(10);
		System.out.println("no duplicates detected");
		System.exit(0);
	}

}
