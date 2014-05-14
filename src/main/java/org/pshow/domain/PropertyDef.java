package org.pshow.domain;

public class PropertyDef {
	private String name;
	private boolean mandatory;
	private boolean multiple;
	private boolean fullTextSearchable;
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isFullTextSearchable() {
		return fullTextSearchable;
	}

	public void setFullTextSearchable(boolean fullTextSearchable) {
		this.fullTextSearchable = fullTextSearchable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
