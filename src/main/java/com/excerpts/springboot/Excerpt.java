package com.excerpts.springboot;

public class Excerpt {

	private int excerptID;
	private String author;
	private String title;
	private String text;
	private String comments;
	private String tags;

	public Excerpt() {
		super();
	}

	public Excerpt(int excerptID, String author, String title, String text, String comments, String tags) {
		super();
		this.excerptID = excerptID;
		this.author = author;
		this.title = title;
		this.text = text;
		this.comments = comments;
		this.tags = tags;
	}

	public int getExcerptID() {
		return excerptID;
	}

	public void setExcerptID(int excerptID) {
		this.excerptID = excerptID;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Excerpt [excerptID=" + excerptID + ", author=" + author + ", title=" + title + ", text=" + text
				+ ", comments=" + comments + ", tags=" + tags + "]";
	}

	public void mergeTags(Excerpt other) {
		this.tags += "; ";
		this.tags += other.tags;
	}
}
