package com.excerpts.springboot.dao.tag;

import java.util.List;

import com.excerpts.springboot.dao.excerpt.ExcerptDAOInterface;
import com.excerpts.springboot.dao.outline.OutlineDAOInterface;
import com.excerpts.springboot.domain.Outline;
import com.excerpts.springboot.domain.Tag;

public interface TagDAOInterface extends ExcerptDAOInterface, OutlineDAOInterface {

	public List<Tag> getByAuthor(String... params);

	public List<Outline> getByGenre(String... params);

	public List<Outline> getByResource(String... params);

}
