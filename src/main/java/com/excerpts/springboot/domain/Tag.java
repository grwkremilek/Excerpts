package com.excerpts.springboot.domain;

public class Tag {

	private int excerptID;
	private String description;

	public Tag() {
		super();
	}

	public Tag(int excerptID, String description) {
		super();
		this.excerptID = excerptID;
		this.description = description;
	}

	public int getExcerptID() {
		return excerptID;
	}

	public void setExcerptID(int tagID) {
		this.excerptID = tagID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Tag [excerptID=" + excerptID + ", description=" + description + "]";
	}
}
