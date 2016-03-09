package com.liushao.tij_thread.basic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasicThreads {
	public static void main(String[] args) {
		
//		for (int i = 0; i < 5; i++) {
//			new Thread(new LiftOff()).start();
//		}
//		System.out.println("waiting for liftOff");
		
		ExecutorService exec = Executors.newSingleThreadExecutor();
		
		for(int i = 0;i < 5;i++){
			exec.execute(new LiftOff());
		}
		exec.execute(new LiftOff());
		exec.shutdown();
	}

}
