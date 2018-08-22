/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class SevenTuple<A, B, C, D, E, F, G> extends SixTuple<A, B, C, D, E, F> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -793453029488427973L;

	protected G g;

	public SevenTuple() {
		super();
	}

	public SevenTuple(A a, B b, C c, D d, E e, F f, G g) {
		super(a, b, c, d, e, f);
		this.g = g;
	}

	public G getG() {
		return g;
	}

	public void setG(G g) {
		this.g = g;
	}

	@Override
	public Object get(int i) {
		Object result = super.get(i);
		if (null != result) {
			return result;
		}
		if (i == 7) {
			return g;
		}
		return null;
	}

	@Override
	public String toString() {
		return "SevenTuple [g=" + g + ", f=" + f + ", e=" + e + ", d=" + d + ", c=" + c + ", b=" + b + ", a=" + a + "]";
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
		result = prime * result + ((g == null) ? 0 : g.hashCode());
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
		if (!(obj instanceof SevenTuple)) {
			return false;
		}
		SevenTuple other = (SevenTuple) obj;
		if (g == null) {
			if (other.g != null) {
				return false;
			}
		} else if (!g.equals(other.g)) {
			return false;
		}
		return true;
	}

}
