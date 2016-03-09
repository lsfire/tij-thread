package com.liushao.tij_thread.model;

class SharedPool {
	private Integer[] dataPool;// data storage
	private int size;
	private int getIndex;// point to the location where to get object
	private int putIndex;// point to the location where to put Object
	private boolean avalible = false;

	public SharedPool(int size) {
		dataPool = new Integer[size];
		this.size = size;
		this.getIndex = 0;
		this.putIndex = 0;
	}

	public synchronized void put(Integer data) throws InterruptedException {
		while (avalible == true) {
			wait();
		}
		dataPool[putIndex] = data;
		putIndex = (putIndex + 1) % size;
		avalible = true;
		notifyAll();
	}

	public synchronized Integer get() throws InterruptedException {
		while (avalible == false) {// can't get but can put
			wait();
		}
		avalible = false;
		Integer data = dataPool[getIndex];
		getIndex = (getIndex + 1) % size;

		notifyAll();

		return data;
	}
}

class producer implements Runnable {
	private SharedPool pool;
	private static Integer putData = 0;

	public producer(SharedPool sharedPool) {
		this.pool = sharedPool;
	}

	@Override
	public void run() {
		while (true) {
			try {
				pool.put(putData);
				System.out.println("put " + putData);
				putData++;
			} catch (InterruptedException e) {
				System.out.println("producer was interrupt");
			}
		}
	}
}

class consumer implements Runnable {
	private SharedPool pool;

	public consumer(SharedPool sharedPool) {
		this.pool = sharedPool;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Integer data = pool.get();
				System.out.println("consumer get data " + data);
			} catch (InterruptedException e) {
				System.out.println("consumer was interrupts");
			}
		}
	}
}

public class Pro_Con {
	public static void main(String[] args) throws InterruptedException {
		SharedPool pool = new SharedPool(10);
		Thread pro_thread = new Thread(new producer(pool));
		Thread con_thread = new Thread(new consumer(pool));

		pro_thread.start();
		con_thread.start();
		

	}
}



