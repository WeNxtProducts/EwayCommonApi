package com.maan.eway.upgrade.criteria;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecCriteria {
	private Class tableName;
	private List<String> columns;
	private List<SearchCriteria> wheres;
	private List<String> orderby;
	private List<JoinCriteria> joins;
	private List<String> groupBy;
	
}
