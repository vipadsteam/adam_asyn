/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class EightTuple<A, B, C, D, E, F, G, H> extends SevenTuple<A, B, C, D, E, F, G> {

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

}
