package com.excerpts.springboot.dao.excerpt;

import java.util.List;

import com.excerpts.springboot.dao.DAOInterface;
import com.excerpts.springboot.domain.Excerpt;


public interface ExcerptDAOInterface<T> extends DAOInterface{
	
	public List<T> getByAuthor(String... params);

}
