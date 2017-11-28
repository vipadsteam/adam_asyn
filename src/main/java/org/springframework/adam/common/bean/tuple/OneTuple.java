/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class OneTuple<A> {

	protected A a;

	public OneTuple() {
		super();
	}

	public OneTuple(A a) {
		super();
		this.a = a;
	}

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public Object get(int i) {
		if (i == 1) {
			return a;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(int i, Class<T> clazz) {
		Object result = this.get(i);
		return (T) result;
	}
}
