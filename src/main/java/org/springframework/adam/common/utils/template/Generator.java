/**
 * 
 */
package org.springframework.adam.common.utils.template;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import sun.misc.SharedSecrets;

/**
 * @author USER
 *
 */
@Service
public class Generator {

	/**
	 * 模板字符串和模板的缓存
	 */
	protected static LoadingCache<String, List<String>[]> cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(20000).concurrencyLevel(256).initialCapacity(2048)
			.build(new CacheLoader<String, List<String>[]>() {
				@Override
				public List<String>[] load(String template) throws Exception {
					return getTemplateInfos(template);
				}
			});

	/**
	 * 生成字符串
	 * 
	 * @param template
	 * @param root
	 * @return
	 */
	public String gen(String template, JsonNode root) {
		if (null == template || "".equals(template.trim()) || null == root) {
			return null;
		}

		// 获取模板信息
		List<String>[] infos = null;
		try {
			infos = cache.get(template);
		} catch (ExecutionException e) {
			infos = getTemplateInfos(template);
		}
		if (null == infos) {
			return null;
		}

		// 拼凑信息
		StringBuffer result = new StringBuffer(template.length());
		List<String> msgList = infos[0];
		List<String> paramList = infos[1];
		for (int i = 0; i < msgList.size(); i++) {
			result.append(msgList.get(i));
			if (i + 1 > paramList.size()) {
				continue;
			}
			String nodeStr = getNodeStr(root, paramList.get(i));
			result.append(nodeStr);
		}

		return result.toString();
	}

	/**
	 * 获取Node信息
	 * 
	 * @param root
	 * @param nodeInfoStr
	 * @return
	 */
	private String getNodeStr(JsonNode root, String nodeInfoStr) {
		String[] nodeInfoArr = nodeInfoStr.split("\\.");
		if (null == nodeInfoArr || nodeInfoArr.length == 0) {
			return "";
		}
		JsonNode node = root;
		for (String nodeInfo : nodeInfoArr) {
			node = node.get(nodeInfo);
			if (null == node) {
				return "";
			}
		}

		return node.asText();
	}

	/**
	 * 根据模板字符串获取模板信息
	 * 
	 * @param template
	 * @return
	 */
	private static List<String>[] getTemplateInfos(String template) {
		List<String>[] result = new List[2];
		result[0] = new ArrayList<String>();
		result[1] = new ArrayList<String>();
		FastCharArrayWriter writer = new FastCharArrayWriter(template.length());
		for (int i = 0; i < template.length(); i++) {
			char charTmp = template.charAt(i);
			if ('{' == charTmp) {
				result[0].add(SharedSecrets.getJavaLangAccess().newStringUnsafe(writer.toCharArray()));
				writer = new FastCharArrayWriter(template.length() - i);
				continue;
			} else if ('}' == charTmp) {
				result[1].add(SharedSecrets.getJavaLangAccess().newStringUnsafe(writer.toCharArray()));
				writer = new FastCharArrayWriter(template.length() - i);
				continue;
			}
			writer.write(charTmp);
		}
		if (writer.size() > 0) {
			result[0].add(SharedSecrets.getJavaLangAccess().newStringUnsafe(writer.toCharArray()));
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		Generator gen = new Generator();
		DBInfo db = new DBInfo();
		db.setDb("db_name");
		db.setDbCluster("db_cluster_name");
		db.setProject("project_name");
		db.setTable("table_name");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.convertValue(db, JsonNode.class);
		String template = "当前集群名：{db_cluster} 数据库名：{db} \n项目名：{project} 表名：{table} \n详情请点击：http://bd.fxwork.kugou.net/retrievers/indicator/center/indicators/1846?domain=%s ";
		String result = gen.gen(template, root);
		System.out.println(result);
	}
}
