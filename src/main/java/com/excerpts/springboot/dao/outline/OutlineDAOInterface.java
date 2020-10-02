package com.excerpts.springboot.dao.outline;

import java.util.List;

import com.excerpts.springboot.dao.DAOInterface;
import com.excerpts.springboot.domain.Outline;

public interface OutlineDAOInterface<T> extends DAOInterface {

	public List<Outline> getByGenre(String... params);

	public List<Outline> getByResource(String... params);

}