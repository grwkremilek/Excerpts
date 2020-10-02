package com.excerpts.springboot.domain;

public class Outline {

	private int outlineID;
	private String plot;
	private String comments;

	public Outline() {
		super();
	}

	public Outline(int outlineID, String plot, String comments) {
		super();
		this.outlineID = outlineID;
		this.plot = plot;
		this.comments = comments;
	}

	public int getOutlineID() {
		return outlineID;
	}

	public void setOutlineID(int outlineID) {
		this.outlineID = outlineID;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Outline [outlineID=" + outlineID + ", plot=" + plot + ", comments=" + comments + "]";
	}
}
