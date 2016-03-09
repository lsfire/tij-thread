package com.liushao.tij_thread.basic;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CaptureUncaughtException {
	public static void main(String[] args) {
		ExecutorService exec = Executors.newCachedThreadPool(new HandlerThreadFactory());
		exec.execute(new ExceptionThread());
		exec.shutdown();
	}
	
}

class ExceptionThread implements Runnable{

	@Override
	public void run() {
		Thread t = Thread.currentThread();
		System.out.println("run() by " + t);
		System.out.println("eh = " + t.getUncaughtExceptionHandler());
		throw new RuntimeException();
		
	}
	
}

class UncaughtHandler implements UncaughtExceptionHandler{

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println("caught " + t + "\'s " + e);
	}
}

class HandlerThreadFactory implements ThreadFactory{

	@Override
	public Thread newThread(Runnable r) {
		System.out.println(this + " creating new thread");
		
		Thread thread = new Thread(r);
		thread.setUncaughtExceptionHandler(new UncaughtHandler());
		return thread;
	}
	
}
