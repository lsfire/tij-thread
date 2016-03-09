package com.liushao.tij_thread.simulation;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Customer{
	private int serviceTime;
	
	public Customer(int serviceTime) {
		this.serviceTime = serviceTime;
	}
	
	public int getServiceTime(){return serviceTime;}
	
	@Override
	public String toString() {
		return "[" + serviceTime + "]";
	}
}


class CustomerLine extends ArrayBlockingQueue<Customer>{

	public CustomerLine(int capacity) {
		super(capacity);
	}
	
	@Override
	public String toString() {
		if(this.size() == 0){
			return "[empty]";
		}
		
		StringBuilder builder = new StringBuilder();
		for(Customer customer:this){
			builder.append(customer);
		}
		return builder.toString();
	}
}

class CustomerGenerator implements Runnable{
	private CustomerLine customers;
	private Random random = new Random(47);
	
	public CustomerGenerator(CustomerLine customers) {
		this.customers = customers;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				TimeUnit.MILLISECONDS.sleep(random.nextInt(30));
				customers.add(new Customer(random.nextInt(1000)));
			}
		} catch (Exception e) {
			System.out.println("customer generator was interrupted");
		}
		System.out.println("CustomerGenerator terminating");
	}
	
}

class Teller implements Runnable,Comparable<Teller> {
	private static int COUNT = 0;
	private int id = COUNT++;
	private CustomerLine customers;
	private int customerServed = 0;
	private boolean isOnServing = true;
	
	public Teller(CustomerLine line) {
		this.customers = line;
	}
	

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				Customer customer = customers.take();
				TimeUnit.MILLISECONDS.sleep(customer.getServiceTime());
				synchronized(this){
					customerServed ++;
					while(!isOnServing){
						wait();
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	public synchronized void serveCustomerLine (){
		assert !isOnServing : "already serving: " + this;
		isOnServing = true;
		notifyAll();
	}
	
	public synchronized void doSometingElse(){
		customerServed = 0;
		isOnServing = false;
	}
	
	@Override
	public String toString() {
		return "Teller " + id + " ";
	}
	
	@Override
	public synchronized int compareTo(Teller o) {
		return customerServed < o.customerServed? -1:
			(customerServed == o.customerServed?0 : 1);
	}
}

class TellerManager implements Runnable{
	private ExecutorService exec;
	
	private CustomerLine customers;
	
	private PriorityQueue<Teller> workingTellers = new PriorityQueue<>();
	
	private Queue<Teller> notWorkingTellers = new LinkedList<>();
	
	private int adjustmentPeriod;
	
	private static Random random = new Random(47);
	
	public TellerManager(ExecutorService service,CustomerLine customerLine,int adjustmentPeriod) {
		this.exec = service;
		this.customers = customerLine;
		this.adjustmentPeriod = adjustmentPeriod;
		
		Teller teller = new Teller(customerLine);
		exec.execute(teller);
		workingTellers.add(teller);//first add a teller to work
	}
	
	public void adjustTellerNumber(){
		
		if(customers.size() / workingTellers.size() > 2){
			if(notWorkingTellers.size() > 0){
				Teller t = notWorkingTellers.remove();
				t.serveCustomerLine();
				workingTellers.offer(t);
				return;
			}
			Teller teller = new Teller(customers);
			exec.execute(teller);
			notWorkingTellers.add(teller);
			return;
		}
		
		if(workingTellers.size() > 1 && 
				customers.size() / workingTellers.size() < 2){
			reassignOneTeller();
		}
		if(customers.size() == 0){
			while(workingTellers.size() > 1){
				reassignOneTeller();
			}
		}
	}
	
	private void reassignOneTeller(){
		Teller teller = workingTellers.poll();
		teller.doSometingElse();
		notWorkingTellers.offer(teller);
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				TimeUnit.MILLISECONDS.sleep(adjustmentPeriod);
				adjustTellerNumber();
				System.out.print(customers + "{");
				for(Teller teller : workingTellers){
					System.out.print(teller + " ");
				}
				System.out.print("}");
			}
		} catch (Exception e) {
			System.out.println(this + " interrupted");
		}
		System.out.println(this + "terminating");
	}
	
	@Override
	public String toString() {
		return "Teller Manager";
	}
}

public class BankTellerSimulation {
	static final int MAX_LINE_NUNBER = 50;
	static final int ADJUSTMENT_PERIOD = 1000;
	public static void main(String[] args) throws InterruptedException {
		ExecutorService executor = Executors.newCachedThreadPool();
		CustomerLine line = new CustomerLine(MAX_LINE_NUNBER);
		
		executor.execute(new CustomerGenerator(line));
		
		executor.execute(new TellerManager(executor, line, ADJUSTMENT_PERIOD));
		
		TimeUnit.SECONDS.sleep(30);
		
		executor.shutdownNow();
	}
	

}
