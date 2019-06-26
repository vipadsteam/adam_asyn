/**
 * 
 */
package org.springframework.adam.common.utils.template;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author USER
 *
 */
public class DBInfo {

	@JsonProperty("db_cluster")
	private String dbCluster;

	private String db;

	private String table;

	private String project;

	public String getDbCluster() {
		return dbCluster;
	}

	public void setDbCluster(String dbCluster) {
		this.dbCluster = dbCluster;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(128);
		builder.append("DBInfo [dbCluster=").append(dbCluster).append(", db=").append(db).append(", table=").append(table).append(", project=").append(project).append("]");
		return builder.toString();
	}

}
