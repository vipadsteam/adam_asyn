/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class EightTuple<A, B, C, D, E, F, G, H> extends SevenTuple<A, B, C, D, E, F, G> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6857777821122824818L;

	protected H h;

	public EightTuple() {
		super();
	}

	public EightTuple(A a, B b, C c, D d, E e, F f, G g, H h) {
		super(a, b, c, d, e, f, g);
		this.h = h;
	}

	public H getH() {
		return h;
	}

	public void setH(H h) {
		this.h = h;
	}

	@Override
	public Object get(int i) {
		Object result = super.get(i);
		if (null != result) {
			return result;
		}
		if (i == 8) {
			return h;
		}
		return null;
	}

	@Override
	public String toString() {
		return "EightTuple [h=" + h + ", g=" + g + ", f=" + f + ", e=" + e + ", d=" + d + ", c=" + c + ", b=" + b + ", a=" + a + "]";
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
		result = prime * result + ((h == null) ? 0 : h.hashCode());
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
		if (!(obj instanceof EightTuple)) {
			return false;
		}
		EightTuple other = (EightTuple) obj;
		if (h == null) {
			if (other.h != null) {
				return false;
			}
		} else if (!h.equals(other.h)) {
			return false;
		}
		return true;
	}

}
