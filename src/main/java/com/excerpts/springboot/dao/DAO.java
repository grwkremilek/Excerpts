package com.excerpts.springboot.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DAO<T> {

	public int save(int id, String... params);

	public List<T> getAll();

	public List<T> getByTitle(String... params);

	public List<T> getByAuthor(String... params);

	public List<T> getByTag(String... params);

	public List<T> getByID(int excerptID);

	public int countAll();

	public int delete(int id);

	/**
	 * Method deleting all entries in the database tables and restarting
	 * auto-increment in the ID column
	 */
	public void resetTables();
}
