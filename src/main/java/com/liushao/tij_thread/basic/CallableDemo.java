package com.liushao.tij_thread.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CallableDemo {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService exec = Executors.newCachedThreadPool();
		
		List<Future<String>> futures = new ArrayList<>();
		
		for(int i = 0;i < 10;i++){
			futures.add(exec.submit(new TaskWithResult(i)));
		}
		
		for(Future<String> future : futures){
			System.out.println(future.get());
		}
	}
}


class TaskWithResult implements Callable<String> {
	
	private int taskId;
	
	public TaskWithResult(int taskId) {
		this.taskId = taskId;
	}

	@Override
	public String call() throws Exception {
		return "task with result id" + taskId;
	}
	
}

