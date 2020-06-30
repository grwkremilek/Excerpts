package com.excerpts.springboot;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DAO<T> {
	
	public List<T> getAll();
	
	public int countAll();
    
    public void save(String... params);
    
    public void edit(int excerptID, String... params);
     
    public void delete(int excerptId);
    
    public List<T> getByAuthor(String... params);
	
    public List<T> getByTitle(String... params);
    
    public List<T> getByTag(String... params);

	public List<Map<String, Object>> countSelectedByAuthor(String[] params);
	
}
