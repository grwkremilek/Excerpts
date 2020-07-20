package com.excerpts.springboot.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excerpts.springboot.domain.Excerpt;

@Component
public class TagValidator implements Validator {

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Excerpt.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.description", "Please enter the tag");
		
		/*
		 * if (excerpt.getTags() != null) {
		 * 
		 * byte[] bytesTags = excerpt.getTags().getBytes(StandardCharsets.UTF_8); int
		 * tagsInBytes = bytesTags.length; if (tagsInBytes > 45) {
		 * errors.rejectValue("tags", "field.max.length", "There are too many tags."); }
		 * }
		 */
	}
}