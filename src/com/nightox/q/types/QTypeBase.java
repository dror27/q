package com.nightox.q.types;

import java.util.List;

public abstract class QTypeBase implements IQType {

	private String				displayName;
	private String				dataType;
	
	private List<QTypeField>	uploadFields;
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public List<QTypeField> getUploadFields() {
		return uploadFields;
	}
	public void setUploadFields(List<QTypeField> uploadFields) {
		this.uploadFields = uploadFields;
	}
}
