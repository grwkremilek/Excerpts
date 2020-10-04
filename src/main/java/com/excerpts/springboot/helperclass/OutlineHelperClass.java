package com.excerpts.springboot.helperclass;

import java.util.List;

import com.excerpts.springboot.domain.Outline;

public class OutlineHelperClass {

	public static List<Outline> replaceEmptyCommentsOutline(List<Outline> outlines) {

		for (Outline outline : outlines) {
			String comments = outline.getComments();
			if (comments.isEmpty()) {
				outline.setComments("No comment yet");
			}
		}
		return outlines;
	}

}
