package com.excerpts.springboot.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.excerpts.springboot.domain.Author;
import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.mappers.AuthorMapper;
import com.excerpts.springboot.mappers.ExcerptMapper;

@Component("authorDAO")
public class AuthorDAOImpl implements DAO<Author> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void save(int excerptID, String... params) {

		String name = params[0];

		if (excerptID == 0) {

			// fetch the ID of the newly created excerpt
			//String getExcerptIDSQL = "SELECT LAST_INSERT_ID()";
			String getExcerptIDSQL = "SELECT excerptID FROM Excerpt ORDER BY excerptID DESC LIMIT 1";
			excerptID = jdbcTemplate.queryForObject(getExcerptIDSQL, Integer.class);

		} else {

			// delete residual mappings of the edited excerpt
			String deleteMappingsSQL = "DELETE FROM Authormap WHERE excerptID = ?";
			jdbcTemplate.update(deleteMappingsSQL, excerptID);
		}

		String findAuthorSQL = "SELECT count(*) FROM Author WHERE name = ?";
		int authorExists = jdbcTemplate.queryForObject(findAuthorSQL, new Object[] { name }, Integer.class);

		// if the author does not exist, create one and also create mappings
		if (authorExists == 0) {

			String insertAuthorSQL = "INSERT INTO Author (name) values (?)";
			jdbcTemplate.update(insertAuthorSQL, name);

			String getAuthorIdSQL = "SELECT LAST_INSERT_ID();";
			int autorIDCurrent = jdbcTemplate.queryForObject(getAuthorIdSQL, Integer.class);

			String insertAuthorMapSQL = "INSERT INTO Authormap (excerptID, authorID) VALUES(?, ?)";
			jdbcTemplate.update(insertAuthorMapSQL, excerptID, autorIDCurrent);

			// if the author exists, get its ID and use it to update mappings
		} else {

			String getAuthorIdSQL = "SELECT authorID FROM Author WHERE name = ?";
			int authorIDCurrent = jdbcTemplate.queryForObject(getAuthorIdSQL, new String[] { name }, Integer.class);

			String insertAuthorMapSQL = "INSERT INTO Authormap (excerptID, authorID) VALUES(?, ?)";
			jdbcTemplate.update(insertAuthorMapSQL, excerptID, authorIDCurrent);
		}
	}

	@Override
	public List<Author> getAll() {

		String SQL = "SELECT e.excerptID, a.name from Excerpt AS e LEFT JOIN Authormap AS m ON m.excerptID = e.excerptID LEFT JOIN Author AS a ON a.authorID = m.authorID";
		List<Author> authors = jdbcTemplate.query(SQL, new AuthorMapper());
		return authors;
	}

	@Override
	public List<Author> getByTitle(String... params) {

		String title = params[0].trim();
		String SQL = "SELECT e.excerptID, a.name from Excerpt AS e LEFT JOIN Authormap AS m ON m.excerptID = e.excerptID LEFT JOIN Author AS a ON a.authorID = m.authorID WHERE e.title = ?";
		List<Author> authors = jdbcTemplate.query(SQL, new String[] { title }, new AuthorMapper());
		return authors;
	}

	@Override
	public List<Author> getByTag(String... params) {

		String description = params[0];
		String SQL = "SELECT am.excerptID, a.name from Author AS a LEFT JOIN Authormap AS am ON a.authorID = am.authorID LEFT JOIN Tagmap AS tm ON am.excerptID = tm.excerptID LEFT JOIN Tag as t ON tm.tagID = t.tagID WHERE t.description = ?";
		List<Author> authors = jdbcTemplate.query(SQL, new String[] { description }, new AuthorMapper());
		return authors;
	}

	@Override
	public List<Author> getByID(int excerptID) {

		String SQL = "SELECT e.excerptID, a.name from Excerpt AS e LEFT JOIN Authormap AS m ON m.excerptID = e.excerptID LEFT JOIN Author AS a ON a.authorID = m.authorID WHERE e.excerptID = ?";
		List<Author> authors = jdbcTemplate.query(SQL, new Integer[] { excerptID }, new AuthorMapper());
		return authors;
	}

	@Override
	public int countAll() {

		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Author", Integer.class);
	}
}
