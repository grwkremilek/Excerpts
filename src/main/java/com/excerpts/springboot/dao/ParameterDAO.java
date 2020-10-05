package com.excerpts.springboot.dao;

import java.util.List;

public interface ParameterDAO<T> extends DAO<T> {
	
	List<T> getByAuthor(String... params);

}
