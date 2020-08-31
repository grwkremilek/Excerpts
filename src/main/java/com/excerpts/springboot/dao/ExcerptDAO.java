package com.excerpts.springboot.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.mappers.ExcerptMapper;

@Repository
public class ExcerptDAO implements DAO<Excerpt> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Method creating a new excerpt in the table Excerpt
	 *
	 * @param author   user provided name of the author
	 * @param title    user provided title of the book
	 * @param text     user provided text of the excerpt
	 * @param comments user provided comments to the text
	 */

	@Transactional
	@Override
	public void save(int excerptID, String... params) {

		String author = params[0].trim();
		String title = params[1].trim();
		String text = params[2].trim();
		String comments = params[3].trim();

		if (excerptID == 0) {

			String insertExcerptSQL = "INSERT INTO Excerpt (author, title, text, comments) values (?, ?, ?, ?)";
			jdbcTemplate.update(insertExcerptSQL, author, title, text, comments);

		} else {

			String updateExcerptSQL = "UPDATE Excerpt SET author = ?, title = ?, text = ? WHERE excerptID=?";
			jdbcTemplate.update(updateExcerptSQL, author, title, text, excerptID);
		}
	}

	/**
	 * Method returning a list of all excerpts
	 *
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Excerpt> getAll() {

		String SQL = "SELECT * FROM Excerpt";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new ExcerptMapper());
		return excerpts;
	}

	/**
	 * Method returning a list of all excerpts from a book of the given title.
	 *
	 * @param title user provided title of the book
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Excerpt> getByTitle(String... params) {

		String title = params[0];
		String SQL = "SELECT * FROM Excerpt WHERE title = ? ORDER BY author";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { title }, new ExcerptMapper());
		return excerpts;
	}

	/**
	 * Method returning a list of all excerpts by an author.
	 *
	 * @param author user provided name of an author
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Excerpt> getByAuthor(String... params) {
		String author = params[0];
		String SQL = "SELECT * FROM Excerpt WHERE author = ? ORDER BY title";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { author }, new ExcerptMapper());	
		return excerpts;
	}

	/**
	 * Method returning a list of all excerpts with the given tag
	 *
	 * @param tag user provided tag
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Excerpt> getByTag(String... params) {

		String description = params[0];
		String SQL = "SELECT e.* from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE t.description = ? ORDER BY e.author";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { description }, new ExcerptMapper());
		return excerpts;
	}

	/**
	 * Method returning an excerpt with the given ID
	 *
	 * @param excerptID user provided excerptID
	 * @return excerpt
	 */
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

	/**
	 * Help method counting all entries in Excerpt used in unit tests
	 * 
	 * @return count
	 */
	@Override
	public int countAll() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt", Integer.class);
	}

	/**
	 * Method deleting an existing excerpt
	 *
	 * @param excerptID
	 */
	@Override
	public void delete(int excerptID) {

		String excerptSQL = "DELETE FROM Excerpt WHERE excerptID = ?";
		jdbcTemplate.update(excerptSQL, excerptID);
	}

	/**
	 * Method deleting all entries in the table Excerpt and restarting auto-increment
	 */
	@Override
	public void resetTables() {
		/*
		 * it is required to switch off foreign key checks to change the tables and
		 * afterwards reset it again
		 */
		String removeChecksSQL = "SET FOREIGN_KEY_CHECKS = 0";
		jdbcTemplate.update(removeChecksSQL);

		String excerptSQL = "TRUNCATE table Excerpt";
		jdbcTemplate.update(excerptSQL);

		String renewChecksSQL = "SET FOREIGN_KEY_CHECKS = 1";
		jdbcTemplate.update(renewChecksSQL);
	}
}
