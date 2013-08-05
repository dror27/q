package com.nightox.q.types;

public class QTypeField {

	private String			name;
	private String			label;
	private String			type = "text";
	private String			inputExtra;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInputExtra() {
		return inputExtra;
	}
	public void setInputExtra(String inputExtra) {
		this.inputExtra = inputExtra;
	}
}
