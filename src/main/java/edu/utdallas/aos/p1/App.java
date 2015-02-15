package edu.utdallas.aos.p1;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * AOS Project 1 - Distributed Node Discovery
 * 
 * @author sudhanshu iyer - sxi120530
 *
 */
public class App {
	private static final Logger logger = LogManager.getLogger(App.class);

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Hello World!");
		logger.info("HELLO WORLD");

		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i = 0; i < 140; i++){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
						e.printStackTrace();
					}
					logger.info("Appending From thread t1");

				}
			}
		});

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
						e.printStackTrace();
					}
					logger.info("Thread T2 Here");
				}
			}
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		logger.info("Finished Logging");

	}
}
