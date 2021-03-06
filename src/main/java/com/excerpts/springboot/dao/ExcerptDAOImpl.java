package com.excerpts.springboot.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.mappers.ExcerptMapper;

@Component("excerptDAO")
public class ExcerptDAOImpl implements ExcerptDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	@Override
	public void save(int excerptID, String... params) {

		String title = params[0].trim();
		String text = params[1].trim();
		String comments = params[2].trim();

		if (excerptID == 0) {

			String insertExcerptSQL = "INSERT INTO Excerpt (title, text, comments) values (?, ?, ?)";
			jdbcTemplate.update(insertExcerptSQL, title, text, comments);

		} else {

			String updateExcerptSQL = "UPDATE Excerpt SET title = ?, text = ? WHERE excerptID=?";
			jdbcTemplate.update(updateExcerptSQL, title, text, excerptID);
		}
	}

	@Override
	public List<Excerpt> getAll() {

		String SQL = "SELECT * FROM Excerpt";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new ExcerptMapper());
		return excerpts;
	}

	@Override
	public List<Excerpt> getByTitle(String... params) {

		String title = params[0];
		String SQL = "SELECT * FROM Excerpt WHERE title = ?";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { title }, new ExcerptMapper());
		return excerpts;
	}

	@Override
	public List<Excerpt> getByAuthor(String... params) {

		String name = params[0];
		String SQL = "SELECT e.* from Excerpt AS e LEFT JOIN Authormap AS m ON m.excerptID = e.excerptID LEFT JOIN Author AS a ON a.authorID = m.authorID WHERE a.name = ?";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { name }, new ExcerptMapper());
		return excerpts;
	}

	@Override
	public List<Excerpt> getByTag(String... params) {

		String description = params[0];
		String SQL = "SELECT e.* from Excerpt AS e LEFT JOIN Tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN Tag AS t ON t.tagID = m.tagID WHERE t.description = ?";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { description }, new ExcerptMapper());
		return excerpts;
	}

	@Override
	public List<Excerpt> getByID(int excerptID) {

		String SQL = "SELECT * FROM Excerpt WHERE excerptID = ?";

		try {

			List<Excerpt> excerpts = jdbcTemplate.query(SQL, new Object[] { excerptID }, new ExcerptMapper());
			return excerpts;

		} catch (EmptyResultDataAccessException e) {

			return null;
		}
	}

	@Override
	public int countAll() {

		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt", Integer.class);
	}

	// deletes the excerpt and all the mappings (the tags and author do not get
	// deleted in Tag, Author)
	@Override
	public void delete(int excerptID) {

		String excerptSQL = "DELETE FROM Excerpt WHERE excerptID = ?";
		jdbcTemplate.update(excerptSQL, excerptID);
	}

}
