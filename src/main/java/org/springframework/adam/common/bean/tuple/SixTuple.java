/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class SixTuple<A, B, C, D, E, F> extends FiveTuple<A, B, C, D, E> {

	protected F f;

	public SixTuple() {
		super();
	}

	public SixTuple(A a, B b, C c, D d, E e, F f) {
		super(a, b, c, d, e);
		this.f = f;
	}

	public F getF() {
		return f;
	}

	public void setF(F f) {
		this.f = f;
	}

	@Override
	public Object get(int i){
		Object result = super.get(i);
		if(null != result){
			return result;
		}
		if(i == 6){
			return f;
		}
		return null;
	}
}
