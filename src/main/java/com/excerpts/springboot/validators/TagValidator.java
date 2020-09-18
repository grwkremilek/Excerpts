package com.excerpts.springboot.validators;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excerpts.springboot.domain.Tag;

@Component
public class TagValidator implements Validator {

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Tag.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.description", "Please enter at least one tag.");

		Tag tag = (Tag) obj;

		if (tag.getDescription() != null) {

			byte[] bytesTags = tag.getDescription().getBytes(StandardCharsets.UTF_8);
			int tagsInBytes = bytesTags.length;
			if (tagsInBytes > 45) {
				errors.rejectValue("tags", "field.max.length", "There are too many tags.");
			}
		}
	}
}