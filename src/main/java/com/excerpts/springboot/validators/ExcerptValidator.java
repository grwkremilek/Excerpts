package com.excerpts.springboot.validators;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excerpts.springboot.domain.Excerpt;

@Component
public class ExcerptValidator implements Validator {

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Excerpt.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {

		Excerpt excerpt = (Excerpt) obj;

		if (excerpt.getExcerptID() == 0) {
			if (excerpt.getAuthor() == null && excerpt.getTitle() == null && excerpt.getText() == null
					&& excerpt.getComments() == null) {
				errors.rejectValue("excerptID", "field.min.value", "Please entrer a valid ID.");
			}
		}

		if (excerpt.getAuthor() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "author", "error.author",
					"Please enter the name of the author.");

			byte[] bytesAuthor = excerpt.getAuthor().getBytes(StandardCharsets.UTF_8);
			int authorInBytes = bytesAuthor.length;
			if (authorInBytes > 45) {
				errors.rejectValue("author", "field.max.length", "The author's name is too long.");
			}
		}

		if (excerpt.getTitle() != null) {

			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "error.title",
					"Please enter the title of the book.");

			byte[] bytesTitle = excerpt.getTitle().getBytes(StandardCharsets.UTF_8);
			int titleInBytes = bytesTitle.length;
			if (titleInBytes > 255) {
				errors.rejectValue("title", "field.max.length", "The title is too long.");
			}
		}

		if (excerpt.getText() != null) {

			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "text", "error.text",
					"Please enter the text of the excerpt.");

			byte[] bytesText = excerpt.getText().getBytes(StandardCharsets.UTF_8);
			int textInBytes = bytesText.length;
			if (textInBytes > 1000) {
				errors.rejectValue("text", "field.max.length", "The text is too long.");
			}
		}

		if (excerpt.getComments() != null) {

			byte[] bytesComments = excerpt.getComments().getBytes(StandardCharsets.UTF_8);
			int commentsInBytes = bytesComments.length;
			if (commentsInBytes > 1000) {
				errors.rejectValue("comments", "field.max.length", "The comments are too long.");
			}
		}
	}
}