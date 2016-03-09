package com.liushao.tij_thread.shared;

import java.nio.channels.NetworkChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EvenChecker implements Runnable{
	private IntGenerator generator;
	
	private int id;
	
	public EvenChecker(IntGenerator generator,int id) {
		this.generator = generator;
		this.id = id;
	}

	@Override
	public void run() {
		while(!generator.isCanceled()){
			int val = generator.next();
			if(val % 2 != 0){
				System.out.println(val + " is not even");
				generator.cancel();
			}
		}
	}
	
	public static void test(IntGenerator generator,int count){
		ExecutorService exec  = Executors.newCachedThreadPool();
		for(int i = 0 ;i < count;i++){
			exec.execute(new EvenChecker(generator, i));
		}
		exec.shutdown();
	}
	
	public static void test(IntGenerator generator){
		test(generator, 10);
	}
	
	public static void main(String[] args) {
		test(new EvenGenerator(), 10);
	}

}

class EvenGenerator extends IntGenerator{
	private   int value = 0;
	private Lock lock = new ReentrantLock();
	
	@Override
	public  int next() {
		lock.lock();
		try {
			++value;
			++value;
			return value;
		} finally {
			lock.unlock();
		}
				
		
	}
	
}


