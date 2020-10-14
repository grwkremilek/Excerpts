package com.excerpts.springboot.dao;

import com.excerpts.springboot.domain.Excerpt;

public interface ExcerptDAO extends ParameterDAO<Excerpt> {
	
	public void delete(int id);
	
	//TODO update via foreign key constraint
	//public void update(int id);

}
