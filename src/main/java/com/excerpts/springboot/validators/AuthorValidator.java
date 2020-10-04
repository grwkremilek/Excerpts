package com.excerpts.springboot.validators;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excerpts.springboot.domain.Author;

@Component
public class AuthorValidator implements Validator {
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Author.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object obj, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.name", "Please enter the name of the author.");

		Author author = (Author) obj;

		if (author.getName() != null) {

			byte[] bytesAuthor = author.getName().getBytes(StandardCharsets.UTF_8);
			int authorInBytes = bytesAuthor.length;
			
			if (authorInBytes > 45) {
				
				errors.rejectValue("author", "field.max.length", "The name is too long.");
			}
		}
	}
}
