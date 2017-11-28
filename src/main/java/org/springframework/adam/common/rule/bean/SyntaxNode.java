package org.springframework.adam.common.rule.bean;

public class SyntaxNode {

	private SyntaxToken operator;

	private Boolean value;
	
	private String valStr;

	public SyntaxNode(SyntaxToken operator, String valStr) {
		super();
		this.operator = operator;
		this.valStr = valStr;
	}

	public SyntaxNode(SyntaxToken operator) {
		super();
		this.operator = operator;
	}

	public SyntaxToken getOperator() {
		return operator;
	}

	public void setOperator(SyntaxToken operator) {
		this.operator = operator;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	public String getValStr() {
		return valStr;
	}

	public void setValStr(String valStr) {
		this.valStr = valStr;
	}

}
