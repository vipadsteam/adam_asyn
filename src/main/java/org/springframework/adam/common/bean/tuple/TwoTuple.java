/**
 * 
 */
package org.springframework.adam.common.bean.tuple;

/**
 * @author USER
 *
 */
public class TwoTuple<A, B> extends OneTuple<A> {

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
	public Object get(int i){
		Object result = super.get(i);
		if(null != result){
			return result;
		}
		if(i == 2){
			return b;
		}
		return null;
	}
}
