/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {

	protected D d;

	public FourTuple() {
		super();
	}

	public FourTuple(A a, B b, C c, D d) {
		super(a, b, c);
		this.d = d;
	}

	public D getD() {
		return d;
	}

	public void setD(D d) {
		this.d = d;
	}

	@Override
	public Object get(int i) {
		Object result = super.get(i);
		if (null != result) {
			return result;
		}
		if (i == 4) {
			return d;
		}
		return null;
	}

	@Override
	public String toString() {
		return "FourTuple [d=" + d + ", c=" + c + ", b=" + b + ", a=" + a + "]";
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
		result = prime * result + ((d == null) ? 0 : d.hashCode());
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
		if (!(obj instanceof FourTuple)) {
			return false;
		}
		FourTuple other = (FourTuple) obj;
		if (d == null) {
			if (other.d != null) {
				return false;
			}
		} else if (!d.equals(other.d)) {
			return false;
		}
		return true;
	}

}
