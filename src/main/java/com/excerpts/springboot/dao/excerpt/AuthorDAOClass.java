package com.excerpts.springboot.dao.excerpt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.excerpts.springboot.dao.DAOInterface;
import com.excerpts.springboot.domain.Author;
import com.excerpts.springboot.mappers.AuthorMapper;

@Component("authorDAOClass")
public class AuthorDAOClass implements DAOInterface<Author> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void save(int excerptID, String... params) {

		String name = params[0];

		if (excerptID == 0) {

			/* fetch the ID of the newly created excerpt */
			String getExcerptIDSQL = "SELECT LAST_INSERT_ID()";
			excerptID = jdbcTemplate.queryForObject(getExcerptIDSQL, Integer.class);

		} else {

			/* delete residual mappings of the edited excerpt */
			String deleteMappingsSQL = "DELETE FROM authormap WHERE excerptID = ?";
			jdbcTemplate.update(deleteMappingsSQL, excerptID);
		}

		String findAuthorSQL = "SELECT count(*) FROM author WHERE name = ?";
		int authorExists = jdbcTemplate.queryForObject(findAuthorSQL, new Object[] { name }, Integer.class);

		/* if the author does not exist, create one and also update mappings */
		if (authorExists == 0) {

			String insertAuthorSQL = "INSERT INTO author (name) values (?)";
			jdbcTemplate.update(insertAuthorSQL, name);

			String getAuthorIdSQL = "SELECT LAST_INSERT_ID();";
			int autorIDCurrent = jdbcTemplate.queryForObject(getAuthorIdSQL, Integer.class);

			String insertAuthorMapSQL = "INSERT INTO authormap (excerptID, authorID) VALUES(?, ?)";
			jdbcTemplate.update(insertAuthorMapSQL, excerptID, autorIDCurrent);

			/* if the author exists, get its ID and use it to update mappings */
		} else {

			String getAuthorIdSQL = "SELECT authorID FROM author WHERE name = ?";
			int authorIDCurrent = jdbcTemplate.queryForObject(getAuthorIdSQL, new String[] { name }, Integer.class);

			String insertAuthorMapSQL = "INSERT INTO authormap (excerptID, authorID) VALUES(?, ?)";
			jdbcTemplate.update(insertAuthorMapSQL, excerptID, authorIDCurrent);
		}
	}

	@Override
	public List<Author> getAll() {

		String SQL = "SELECT e.excerptID, a.name from Excerpt AS e LEFT JOIN authormap AS m ON m.excerptID = e.excerptID LEFT JOIN author AS a ON a.authorID = m.authorID";
		List<Author> authors = jdbcTemplate.query(SQL, new AuthorMapper());
		return authors;
	}

	@Override
	public List<Author> getByTitle(String... params) {

		String title = params[0].trim();
		String SQL = "SELECT e.excerptID, a.name from Excerpt AS e LEFT JOIN authormap AS m ON m.excerptID = e.excerptID LEFT JOIN author AS a ON a.authorID = m.authorID WHERE e.title = ?";
		List<Author> authors = jdbcTemplate.query(SQL, new String[] { title }, new AuthorMapper());
		return authors;
	}

	@Override
	public List<Author> getByTag(String... params) {
		// TODO once there is an option to include multiple authors per db entry
		return null;
	}

	@Override
	public List<Author> getByID(int excerptID) {

		String SQL = "SELECT e.excerptID, a.name from Excerpt AS e LEFT JOIN authormap AS m ON m.excerptID = e.excerptID LEFT JOIN author AS a ON a.authorID = m.authorID WHERE e.excerptID = ?";
		List<Author> authors = jdbcTemplate.query(SQL, new Integer[] { excerptID }, new AuthorMapper());
		return authors;
	}

	@Override
	public int countAll() {

		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Author", Integer.class);
	}

	@Override
	public void delete(int excerptID) {

		String deleteAuthorSQL = "DELETE FROM Authormap WHERE excerptID = ?";
		jdbcTemplate.update(deleteAuthorSQL, excerptID);
	}
}
