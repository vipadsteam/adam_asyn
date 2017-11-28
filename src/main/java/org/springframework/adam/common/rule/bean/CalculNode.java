package org.springframework.adam.common.rule.bean;

import java.util.List;

/**
 * @author USER
 *
 */
public class CalculNode {

	/**
	 * 当前计算位
	 */
	private Integer calIndex = 0;

	/**
	 * 当前结果
	 */
	private Boolean result = true;

	/**
	 * 运算队列
	 */
	private List<SyntaxNode> syntaxNodes;

	/**
	 * 计算节点
	 * 
	 * @param syntaxNodes
	 */
	public CalculNode(List<SyntaxNode> syntaxNodes) {
		this.syntaxNodes = syntaxNodes;
	}

	public Integer getCalIndex() {
		return calIndex;
	}

	public void setCalIndex(Integer calIndex) {
		this.calIndex = calIndex;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public List<SyntaxNode> getSyntaxNodes() {
		return syntaxNodes;
	}

	public void setSyntaxNodes(List<SyntaxNode> syntaxNodes) {
		this.syntaxNodes = syntaxNodes;
	}

}
