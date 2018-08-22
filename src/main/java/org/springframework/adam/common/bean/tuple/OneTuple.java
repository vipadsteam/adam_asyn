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

	@Override
	public String toString() {
		return "OneTuple [a=" + a + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OneTuple)) {
			return false;
		}
		OneTuple other = (OneTuple) obj;
		if (a == null) {
			if (other.a != null) {
				return false;
			}
		} else if (!a.equals(other.a)) {
			return false;
		}
		return true;
	}

}
