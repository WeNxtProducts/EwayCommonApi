package com.maan.eway.workflow.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.workflow.dto.JsonField;

import jakarta.persistence.Column;
import jakarta.persistence.Tuple;

public class FieldFromTuple implements Function<Tuple,JsonField> {

	@Override
	public JsonField apply(Tuple t) {
		try {
			JsonField field=JsonField.builder()
					.companyId(t.get("companyId")==null?BigDecimal.ZERO:new BigDecimal(t.get("companyId").toString()))
					.datatype(t.get("datatype")==null?"":t.get("datatype").toString())
					.defaultValue(t.get("defaultValue")==null?"":t.get("defaultValue").toString())
					.defaultYn(t.get("defaultYn")==null?"N":t.get("defaultYn").toString())
					.headerKeyid(t.get("headerKeyid")==null?BigDecimal.ZERO:new BigDecimal(t.get("headerKeyid").toString()))
					.isarray(t.get("isarray")==null?"N":t.get("isarray").toString())
					.isHeader(t.get("isHeader")==null?"N":t.get("isHeader").toString())
					.jsonKey(t.get("jsonKey")==null?"":t.get("jsonKey").toString())
					.keyId(t.get("keyId")==null?BigDecimal.ZERO:new BigDecimal(t.get("keyId").toString()))
					.pattern(t.get("pattern")==null?"":t.get("pattern").toString())
					.productId(t.get("productId")==null?BigDecimal.ZERO:new BigDecimal(t.get("productId").toString()))
					.status(t.get("status")==null?"N":t.get("status").toString())
					.queryId(t.get("queryId")==null?BigDecimal.ZERO:new BigDecimal(t.get("queryId").toString()))
					.queryCol(t.get("queryCol")==null?"":t.get("queryCol").toString())
					.queryAlias(t.get("queryAlias")==null?"":t.get("queryAlias").toString())
					.build();
			// private BigDecimal queryId ; 
		    //private String queryCol ;
			return field;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
