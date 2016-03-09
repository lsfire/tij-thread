package com.liushao.tij_thread.interrupting;


import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


class SleepBlocked implements Runnable{

	@Override
	public void run() {
		try {
			System.out.println("sleep blocked ready to run");
			TimeUnit.SECONDS.sleep(100);
		} catch (InterruptedException e) {
			System.out.println("sleepBlocked Interrupt exception");
		}
		
		System.out.println("exiting sleepBlocked.run");
	}
}

class IOBlocked implements Runnable{
	private InputStream in;
	
	public IOBlocked(InputStream is) {
		this.in = is;
	}

	@Override
	public void run() {
		try {
			System.out.println("wait for read");
			in.read();
		} catch (IOException e) {
			if(Thread.currentThread().isInterrupted()){
				System.out.println("interrupt from blocked I/O");
			}else throw new RuntimeException();
		}
		
		System.out.println("exiting IOBlocked.run");
	}
}

class SynchronizedBlocked implements Runnable{
	
	public synchronized void f(){//the function will never release lock
		while (true) {
			Thread.yield();
		}
	}
	
	public SynchronizedBlocked() {
		new Thread(){//this thread will acquire the lock of function f()
			public void run() {
				f();
			};
		}.start();
	}

	@Override
	public void run() {
		System.out.println("trying to call f");
		f();//it will never get the lock of f because 
		//another thread call f and f will never release the lock
		System.out.println("exiting SynchronizedBlocked.run");
	}
}
public class Interrupting {
	
	private static ExecutorService exec = Executors.newCachedThreadPool();
	
	public static void test(Runnable r) throws InterruptedException{
		Future<?> future = exec.submit(r);
		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println("interrupting " + r.getClass().getName());
		future.cancel(true);
		System.out.println("interrupt send to " + r.getClass().getName());
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		//test(new SleepBlocked());
		test(new SynchronizedBlocked());
		test(new IOBlocked(System.in));
		TimeUnit.SECONDS.sleep(3);
		//System.out.println("aborting with system exiting");
		//System.exit(0);
	}
	
}
