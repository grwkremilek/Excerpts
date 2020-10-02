package com.excerpts.springboot.validators;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excerpts.springboot.domain.Outline;

@Component
public class OutlineValidator implements Validator {

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Outline.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {

		Outline outline = (Outline) obj;

		if (outline.getOutlineID() == 0) {

			if (outline.getPlot() == null && outline.getComments() == null) {
				
				errors.rejectValue("outlineID", "field.min.value", "Please entrer a valid ID.");
			}
		}

		if (outline.getPlot() != null) {

			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "plot", "error.plot",
					"Please enter at least something.");

			byte[] bytesPlot = outline.getPlot().getBytes(StandardCharsets.UTF_8);
			int plotInBytes = bytesPlot.length;
			
			if (plotInBytes > 5000) {
				errors.rejectValue("plot", "field.max.length", "The plot outline is too long.");
			}
		}

		if (outline.getComments() != null) {

			byte[] bytesComments = outline.getComments().getBytes(StandardCharsets.UTF_8);
			int commentsInBytes = bytesComments.length;
			
			if (commentsInBytes > 2500) {

				errors.rejectValue("comments", "field.max.length", "The comments are too long.");
			}
		}
	}
}