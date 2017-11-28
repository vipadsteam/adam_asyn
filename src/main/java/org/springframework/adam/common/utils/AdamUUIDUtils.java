package org.springframework.adam.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AdamUUIDUtils {

	public static String getUUID() {
		return new UUID(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong()).toString() + Thread.currentThread().getId();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(getUUID());
		}
		AdamUUIDUtils a = new AdamUUIDUtils();
		a.test();
	}

	private void test() {
		int threads = 1000;
		int times = 1000000;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<Long>> results = new ArrayList<Future<Long>>();
		for (int index = 1; index < times; index++) {
			results.add(executor.submit(new Worker(index)));
		}
		executor.shutdown();
		try {
			while (!executor.awaitTermination(1, TimeUnit.SECONDS))
				;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long sum = 0;
		for (Future<Long> result : results) {
			try {
				sum += result.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("---------------------------------");
		System.out.println("number of threads :" + threads + " times:" + times);
		System.out.println("running time: " + sum + "ms");
		System.out.println("running time per request: " + ((double) sum / (double) times) + "ms");
		System.out.println("QPS: " + ((double) times / ((double) ((double) sum / (double) threads) / (double) 1000)));
		System.out.println("---------------------------------");
	}

	class Worker implements Callable<Long> {

		private int i;

		public Worker(int i) {
			super();
		}

		@Override
		public Long call() throws Exception {
			long begin = System.currentTimeMillis();
			String result = "";
			try {
				result = getUUID();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			Long end = System.currentTimeMillis();
			Long spent = end - begin;
			return spent;
		}

	}

}
