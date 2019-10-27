package com.montos.boot.montos.mq.core.helper;

public class AsynExecuter {
	
	public static int DEFAULT_CAPACITY = 5;
	
	Integer capacity = DEFAULT_CAPACITY;

	public AsynExecuter setCapacity(Integer capacity) {
		this.capacity = capacity;
		return this;
	}
	
	public void execute(final IExecuter executer){
			this.consume();
			new Thread(String.format("AsynExecuter-%s", capacity)){

				@Override
				public void run() {
					try{
						executer.execute();
					}finally{
						produce();
					}
				}
				
			}.start();
	}
	
	public synchronized void consume(){
		if(capacity<=0){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		capacity--;
	}
	
	public synchronized void produce(){
		capacity++;
		this.notifyAll();
	}
	
	

}
