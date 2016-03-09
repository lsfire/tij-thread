package com.liushao.tij_thread;

public class ThreadTest {
	public static void main(String[] args) {
		
		for(int i = 0 ; i < 9;i++){
			new Thread(new Task("" + i )).start();
		}
	}

}

class Person{
	String name;
	int age;
	
	public Person(String name,int age) {
		this.name  =name;
		this.age = age;
	}
}

class Task implements Runnable{
	private Person person = new Person("aa", 100);
	
	private String newName;

	 public Task(String newName) {
		 this.newName = newName;
	}
	 
	@Override
	public void run() {
		person.name  =newName ;
		System.out.println("new name of person " + person.name);
	}
	
}
