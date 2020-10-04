package com.excerpts.springboot.dao.excerpt;

import java.util.List;

import com.excerpts.springboot.dao.DAOInterface;
import com.excerpts.springboot.domain.Excerpt;

public class AuthorDAOClass implements DAOInterface<Excerpt> {

	@Override
	public void save(int id, String... params) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Excerpt> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Excerpt> getByTitle(String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Excerpt> getByTag(String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Excerpt> getByID(int excerptID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub

	}

}
