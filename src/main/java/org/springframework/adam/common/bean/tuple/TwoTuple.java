/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class TwoTuple<A, B> extends OneTuple<A> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2923295655560763953L;

	protected B b;

	public TwoTuple() {
		super();
	}

	public TwoTuple(A a, B b) {
		super(a);
		this.b = b;
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	@Override
	public Object get(int i) {
		Object result = super.get(i);
		if (null != result) {
			return result;
		}
		if (i == 2) {
			return b;
		}
		return null;
	}

	@Override
	public String toString() {
		return "TwoTuple [b=" + b + ", a=" + a + "]";
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
		result = prime * result + ((b == null) ? 0 : b.hashCode());
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
		if (!(obj instanceof TwoTuple)) {
			return false;
		}
		TwoTuple other = (TwoTuple) obj;
		if (b == null) {
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		return true;
	}

}
