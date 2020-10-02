package com.excerpts.springboot.domain;

public class Author {
	
	private int authorID;
	private String name;
	
	public Author() {
		super();
	}

	public Author(int authorID, String name) {
		super();
		this.authorID = authorID;
		this.name = name;
	}

	public int getAuthorID() {
		return authorID;
	}

	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Author [authorID=" + authorID + ", name=" + name + "]";
	}
}
