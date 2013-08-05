package com.nightox.q.types;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.nightox.q.model.m.Q;

public interface IQType {

	String				getDisplayName();
	String				getDataType();
	
	List<QTypeField>	getUploadFields();
	
	void				setUploadItems(Q q, Map<String, FileItem> items) throws IOException;
	
	String				renderHtml(Q q);
}
