/**
 * 
 */
package org.springframework.adam.common.rule.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.adam.common.ICache;
import org.springframework.adam.common.rule.bean.CalculNode;
import org.springframework.adam.common.rule.bean.SyntaxNode;
import org.springframework.adam.common.rule.bean.SyntaxToken;
import org.springframework.stereotype.Component;

/**
 * @author USER
 *
 */
@Component
public class AdamRuleExcutor {

	private static final Pattern BRACKETS = Pattern.compile("\\([^(^)]*\\)");

	private static final String REPLACE_KEEP_STR = "A__";
	
	private static final String MAP_SUFF = "_m";
	
	private static final String LIST_SUFF = "_l";

	private ICache ruleCache;

	public ICache getRuleCache() {
		return ruleCache;
	}

	public AdamRuleExcutor setRuleCache(ICache ruleCache) {
		this.ruleCache = ruleCache;
		return this;
	}

	/**
	 * @param condition
	 * @param ruleMap
	 * @return
	 */
	public boolean execute(String condition, Map<String, String> ruleMap) {
		Map<String, String> bracketsNodeMap = getBracketsNodeFromCache(condition);
		boolean tmpResult = false;
		for (Entry<String, String> entry : bracketsNodeMap.entrySet()) {
			String conditionTmp = entry.getValue();
			List<SyntaxNode> syntaxNodes = getSyntaxNodesFromCache(conditionTmp);
			if(CollectionUtils.isEmpty(syntaxNodes)){
				return false;
			}
			tmpResult = executeSyntaxNode(syntaxNodes, ruleMap);
			String tmpResultStr = "0";
			if (tmpResult) {
				tmpResultStr = "1";
			}
			ruleMap.put(entry.getKey(), tmpResultStr);
		}
		return tmpResult;
	}

	/**
	 * 拆分括号（从缓存中）
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getBracketsNodeFromCache(String condition) {
		Map<String, String> bracketsMap = null;
		if (null != ruleCache) {
			bracketsMap = ruleCache.get(condition + MAP_SUFF, Map.class);
			if (MapUtils.isEmpty(bracketsMap)) {
				bracketsMap = getBracketsNode(condition);
				ruleCache.put(condition + MAP_SUFF, bracketsMap);
			}
		} else {
			bracketsMap = getBracketsNode(condition);
		}
		return bracketsMap;
	}

	/**
	 * 拆分括号
	 * 
	 * @param condition
	 * @return
	 */
	private Map<String, String> getBracketsNode(String condition) {
		condition = condition.replace(" ", "");
		int leftCount = getLeftBracketsCount(condition);
		Map<String, String> bracketsMap = new LinkedHashMap<String, String>(leftCount + 1);
		int bracketIndex = 0;
		// 如果有括号
		if (leftCount > 0) {
			for (; bracketIndex < leftCount; bracketIndex++) {
				Matcher matcher = BRACKETS.matcher(condition);
				// 如果匹配中
				if (matcher.find()) {
					String bracketsStr = matcher.group();
					String flag = REPLACE_KEEP_STR + bracketIndex;
					condition = StringUtils.replace(condition, bracketsStr, flag);
					bracketsStr = StringUtils.replace(bracketsStr, "(", "");
					bracketsStr = StringUtils.replace(bracketsStr, ")", "");
					bracketsMap.put(flag, bracketsStr);
				}
			}
		}
		String flag = REPLACE_KEEP_STR + bracketIndex;
		bracketsMap.put(flag, condition);
		return bracketsMap;
	}

	/**
	 * 计算右括号数量
	 * 
	 * @param condition
	 * @return
	 */
	private int getLeftBracketsCount(String condition) {
		char[] c = condition.toCharArray();
		int total = 0;
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ')') {
				total++;
			}
		}
		return total;
	}

	/**
	 * 获取语法结构（从缓存中）
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<SyntaxNode> getSyntaxNodesFromCache(String condition) {
		List<SyntaxNode> syntaxNodes = null;
		if (null != ruleCache) {
			syntaxNodes = ruleCache.get(condition + LIST_SUFF, List.class);
			if (CollectionUtils.isEmpty(syntaxNodes)) {
				syntaxNodes = getSyntaxNodes(condition);
				ruleCache.put(condition + LIST_SUFF, syntaxNodes);
			}
		} else {
			syntaxNodes = getSyntaxNodes(condition);
		}
		return syntaxNodes;
	}

	/**
	 * 排列计算队列
	 * 
	 * @param condition
	 * @param ruleMap
	 * @return
	 */
	private static List<SyntaxNode> getSyntaxNodes(String condition) {
		List<SyntaxNode> syntaxNodes = new ArrayList<SyntaxNode>();
		String tmpCondition = condition;
		// 保护数字，如果节点超过100个则无法计算，避免死循环
		int protectNum = 1000;
		// 循环临时条件
		while (!StringUtils.isBlank(tmpCondition) && protectNum >= 0) {
			protectNum--;
			// 循环所有支持的元素
			for (SyntaxToken syntaxToken : SyntaxToken.values()) {
				// 匹配正则
				Matcher matcher = syntaxToken.getSymbol().matcher(tmpCondition);
				// 如果匹配中
				if (matcher.find()) {
					String token = matcher.group();
					SyntaxNode syntaxNode = null;
					// 如果是实际元素，非运算符元素
					if (SyntaxToken.RULE.equals(syntaxToken)) {
						syntaxNode = new SyntaxNode(syntaxToken, token);
					} else {
						syntaxNode = new SyntaxNode(syntaxToken);
					}
					syntaxNodes.add(syntaxNode);
					// 截取处理了的字符串
					tmpCondition = tmpCondition.substring(token.length());
					break;
				}
			}
		}
		return syntaxNodes;
	}

	/**
	 * 计算语法
	 * 
	 * @param syntaxNodes
	 * @param ruleMap
	 * @return
	 */
	private static boolean executeSyntaxNode(List<SyntaxNode> syntaxNodes, Map<String, String> ruleMap) {
		CalculNode calculNode = new CalculNode(syntaxNodes);
		// 循环替换掉表达式中元元素的真实值
		for (SyntaxNode syntaxNode : syntaxNodes) {
			if (SyntaxToken.RULE.equals(syntaxNode.getOperator())) {
				String valNode = ruleMap.get(syntaxNode.getValStr());
				if ("1".equals(valNode)) {
					syntaxNode.setValue(true);
				} else {
					syntaxNode.setValue(false);
				}
			}
		}
		while (calculNode.getCalIndex() < syntaxNodes.size()) {
			calculate(calculNode);
		}
		return calculNode.getResult();
	}

	/**
	 * 计算队列
	 * 
	 * @param calculNode
	 * @return
	 */
	private static boolean calculate(CalculNode calculNode) {
		List<SyntaxNode> syntaxNodes = calculNode.getSyntaxNodes();
		Integer calIndex = calculNode.getCalIndex();
		// 如果当前节点游标已经大于计算队列说明已经计算完成，且发生错误
		if (calIndex >= syntaxNodes.size()) {
			return false;
		}
		// 当前结果是result
		Boolean result = calculNode.getResult();
		SyntaxNode syntaxNode = syntaxNodes.get(calculNode.getCalIndex());
		Integer dimention = syntaxNode.getOperator().getDimention();
		// 当前节点游标往前推一位
		calculNode.setCalIndex(calIndex + 1);
		if (dimention.equals(0)) { // 如果为实际元素
			// 真真为真，假假为真
			if (result == syntaxNode.getValue()) {
				calculNode.setResult(true);
			} else {// 假真为假
				calculNode.setResult(false);
			}
		} else if (dimention.equals(1)) { // 如果为一元运算符
			// 目前一元运算符支持！
			if (SyntaxToken.NOT.equals(syntaxNode.getOperator())) {
				CalculNode calculNodeTmp = new CalculNode(syntaxNodes);
				calculNodeTmp.setCalIndex(calculNode.getCalIndex());
				calculNode.setResult(!calculate(calculNodeTmp));
				calculNode.setCalIndex(calculNodeTmp.getCalIndex());
			}
		} else { // 如果为二元运算符
			if (SyntaxToken.OR.equals(syntaxNode.getOperator())) {// 目前二元运算符支持OR
				CalculNode calculNodeTmp = new CalculNode(syntaxNodes);
				calculNodeTmp.setCalIndex(calculNode.getCalIndex());
				boolean tmpResult = calculate(calculNodeTmp);
				calculNode.setResult(result || tmpResult);
				calculNode.setCalIndex(calculNodeTmp.getCalIndex());
			} else if (SyntaxToken.AND.equals(syntaxNode.getOperator())) {// 目前二元运算符支持AND
				CalculNode calculNodeTmp = new CalculNode(syntaxNodes);
				calculNodeTmp.setCalIndex(calculNode.getCalIndex());
				boolean tmpResult = calculate(calculNodeTmp);
				calculNode.setResult(result && tmpResult);
				calculNode.setCalIndex(calculNodeTmp.getCalIndex());
			}
		}
		return calculNode.getResult();
	}
}
