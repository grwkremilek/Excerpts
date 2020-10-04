package com.excerpts.springboot.domain;

public class Author {
	
	private int excerptID;
	private String name;
	
	public Author() {
		super();
	}

	public Author(int excerptID, String name) {
		super();
		this.excerptID = excerptID;
		this.name = name;
	}

	public int getExcerptID() {
		return excerptID;
	}

	public void setExcerptID(int excerptID) {
		this.excerptID = excerptID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Author [excerptID=" + excerptID + ", name=" + name + "]";
	}

}
