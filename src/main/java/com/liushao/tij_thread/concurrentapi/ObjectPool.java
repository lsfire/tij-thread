package com.liushao.tij_thread.concurrentapi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.omg.CORBA.portable.IndirectionException;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;
import org.w3c.dom.css.Counter;

/**
 * 
 * @author liushao
 *object pool store objects that can be checkout and release
 * @param <T>
 */
class Pool<T> {
	private int size;
	private List<T> items = new ArrayList<>();
	private volatile boolean[] checkedOut;//flag to decide wheather it is checkout or not
	private Semaphore available;
	
	public Pool(Class<T> clas,int size) {
		this.size = size;
		checkedOut = new boolean[size];
		available = new Semaphore(size, true);
		
		for(int i = 0;i < size;i++){
			try {
				items.add(clas.newInstance());
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
		}
	}
	
	public T checkout() throws InterruptedException{
		available.acquire();
		return getItem();
		
	}
	
	public void checkIn(T item){
		if (releaseItem(item)) available.release();
	}
	
	private synchronized T getItem(){
		for(int i = 0;i < size;i++){
			if(!checkedOut[i]){
				checkedOut[i] = true;
				return items.get(i);
			}
		}
		return null;
	}
	
	
	private synchronized boolean releaseItem(T item){
		int index = items.indexOf(item);
		if(index == -1) return false;
		if(checkedOut[index]){
			checkedOut[index] = false;
			return true;
		}
		return false;
	}
}

class Fat{
	
	private volatile double d;
	private static int counter = 0;
	private final int id = counter++;
	
	public Fat() {
		for(int i = 1;i < 10000;i++){
			d += (Math.E + Math.PI) /(double)i;
		}
	}
	
	public void operation(){
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		return "Fat id:" + id;
	}
}

class CheckoutTask<T> implements Runnable{
	private static int count = 0;
	private final  int id = count++;

	private Pool<T> pool;
	
	public CheckoutTask(Pool<T> pool) {
		this.pool = pool;
	}
	
	@Override
	public void run() {
		try {
			T item = pool.checkout();
			System.out.println(this + "check out " + item);
			TimeUnit.SECONDS.sleep(1);
			System.out.println(this + " check in " + item);
			pool.checkIn(item);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "checkout task " + id;
	}
	
}
public class ObjectPool {
	final static int SIZE = 25;
	public static void main(String[] args) throws InterruptedException {
		final Pool<Fat> pool = new Pool<>(Fat.class, SIZE);
		ExecutorService exec = Executors.newCachedThreadPool();
		
		for (int i = 0; i < SIZE; i++) {
			exec.execute(new CheckoutTask<>(pool));
		}
		System.out.println("all checkout task create");
		List<Fat> list = new ArrayList<>();
		
		for (int i = 0; i < SIZE; i++) {
			Fat f = pool.checkout();
			System.out.println(i + "main() thread check out");
			f.operation();
			list.add(f);
		}
		
		Future<?> blocked = exec.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					pool.checkout();//this call will blocked
				} catch (InterruptedException e) {
					System.out.println("");
				}
			}
		});
		
		TimeUnit.SECONDS.sleep(2);
		blocked.cancel(true);
		System.out.println("check in objects in" + list);
		for(Fat fat : list)
			pool.checkIn(fat);
		for(Fat fat : list)//second checkin ignored
			pool.checkIn(fat);
		exec.shutdown();
	}
}
