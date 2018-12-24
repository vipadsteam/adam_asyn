/**
 * 
 */
package adam.test.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.adam.common.rule.engine.AdamRuleExcutor;

/**
 * @author USER
 *
 */
public class RuleMain {

//	private static final String RULE_STR = "!(!ug001&ug005)|!((!ug002&ug003)|((ug004|!ug005)&ug006))";
	private static final String RULE_STR = "((ug004&!ug005)|ug006)";
//	private static final String RULE_STR = "ug001&!([fr0032|00024_324-2你好:asf]|![fr0033|00044_324-2你好:4sf])";
//	private static final String RULE_STR = "[fr0032|00004_324-2你好:asf]";
	private static Map<String, String> ruleMap = new HashMap<String, String>();
	static {
		 ruleMap.put("ug001", "0");
		 ruleMap.put("ug002", "0");
		 ruleMap.put("ug003", "0");
		 ruleMap.put("ug004", "0");
		 ruleMap.put("ug005", "1");
		 ruleMap.put("ug006", "1");

//		ruleMap.put("ug001", "1");
//		ruleMap.put("[fr0032|00024_324-2你好:asf]", "1");
//		ruleMap.put("[fr0033|00044_324-2你好:4sf]", "1");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AdamRuleExcutor r = new AdamRuleExcutor();
		AdsCache cache = new AdsCache();
		cache.refresh("30");
		r.setRuleCache(cache);
		System.out.println(r.execute(RULE_STR, ruleMap));

//		RuleMain rm = new RuleMain();
//		while (true) {
//			rm.test();
//		}
	}

	public void test() {
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

		private AdamRuleExcutor r;

		public Worker(int i) {
			super();
			this.i = i;

			this.r = new AdamRuleExcutor();
		}

		@Override
		public Long call() throws Exception {
			long begin = System.currentTimeMillis();
			r.execute(RULE_STR, ruleMap);
			Long end = System.currentTimeMillis();
			Long spent = end - begin;
			return spent;
		}

	}

}
