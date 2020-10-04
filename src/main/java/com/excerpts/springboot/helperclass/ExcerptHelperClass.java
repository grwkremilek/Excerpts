package com.excerpts.springboot.helperclass;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.excerpts.springboot.domain.Excerpt;

public class ExcerptHelperClass {

	public static List<Excerpt> replaceEmptyCommentsExcerpts(List<Excerpt> excerpts) {

		for (Excerpt excerpt : excerpts) {
			String comments = excerpt.getComments();
			if (comments.isEmpty()) {
				excerpt.setComments("No comment yet");
			}
		}
		return excerpts;
	}

	// decode encoded comments and text before passing to editForm.html
	public static Excerpt decode(Excerpt excerpt) {

		try {

			String decodedComments = java.net.URLDecoder.decode(excerpt.getComments(), StandardCharsets.UTF_8.name());
			excerpt.setComments(decodedComments);

			String decodedText = java.net.URLDecoder.decode(excerpt.getText(), StandardCharsets.UTF_8.name());
			excerpt.setText(decodedText);

		} catch (UnsupportedEncodingException e) {
		}

		return excerpt;
	}

}
