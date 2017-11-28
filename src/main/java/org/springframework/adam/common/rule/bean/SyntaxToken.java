package org.springframework.adam.common.rule.bean;

import java.util.regex.Pattern;

/**
 * @author USER
 *
 */
public enum SyntaxToken {

	OR(Pattern.compile("^\\|"), 2),

	AND(Pattern.compile("^&"), 2),

	NOT(Pattern.compile("^!"), 1),

	RULE(Pattern.compile("^ug\\d*|^\\[[^\\]]*\\]|^A__\\d*"), 0);

	private Pattern symbol;

	private Integer dimention;

	SyntaxToken(Pattern symbol, Integer dimention) {
		this.symbol = symbol;
		this.dimention = dimention;
	}

	public Pattern getSymbol() {
		return symbol;
	}

	public void setSymbol(Pattern symbol) {
		this.symbol = symbol;
	}

	public Integer getDimention() {
		return dimention;
	}

	public void setDimention(Integer dimention) {
		this.dimention = dimention;
	}

}
