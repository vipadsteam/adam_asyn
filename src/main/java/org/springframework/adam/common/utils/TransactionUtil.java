/**
 * 
 */
package org.springframework.adam.common.utils;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author user
 *
 */
public class TransactionUtil {

	/**
	 * 检查当前是否有事务的存在
	 * 
	 * @return
	 */
	public static boolean checkTransaction() {
		boolean hasTransaction = false;
		try {
			if (TransactionAspectSupport.currentTransactionStatus() != null) {
				hasTransaction = true;
			}
		} catch (Exception e) {
			hasTransaction = false;
		}
		return hasTransaction;
	}

	/**
	 * 当前有事务回滚
	 * 
	 * @return
	 */
	public static boolean transactionRollBack() {
		boolean hasTransaction = checkTransaction();
		if(hasTransaction){
			try{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}catch (Exception e) {
				return false;
			}
		}
		return hasTransaction;
	}
}
