/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6734744524246646423L;

	protected C c;

	public ThreeTuple() {
		super();
	}

	public ThreeTuple(A a, B b, C c) {
		super(a, b);
		this.c = c;
	}

	public C getC() {
		return c;
	}

	public void setC(C c) {
		this.c = c;
	}

	@Override
	public Object get(int i) {
		Object result = super.get(i);
		if (null != result) {
			return result;
		}
		if (i == 3) {
			return c;
		}
		return null;
	}

	@Override
	public String toString() {
		return "ThreeTuple [c=" + c + ", b=" + b + ", a=" + a + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((c == null) ? 0 : c.hashCode());
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
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ThreeTuple)) {
			return false;
		}
		ThreeTuple other = (ThreeTuple) obj;
		if (c == null) {
			if (other.c != null) {
				return false;
			}
		} else if (!c.equals(other.c)) {
			return false;
		}
		return true;
	}

}
