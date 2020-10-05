package com.excerpts.springboot.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface DAO<T> {

	public void save(int id, String... params);

	public List<T> getAll();

	public List<T> getByTitle(String... params);

	public List<T> getByTag(String... params);

	public List<T> getByID(int excerptID);

	public int countAll();

	public void delete(int id);

}
