package com.excerpts.springboot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExcerptDAO implements DAO<Excerpt> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void save(String... params) {
		String author = params[0].trim();
		String title = params[1].trim();
		String text = params[2].trim();
		String comments = params[3].trim();
		List<String> tags = Arrays.asList(params[4].split("\\s*;\\s*"));

		String insertExcerptSQL = "INSERT INTO Excerpt (author, title, text, comments) values (?, ?, ?, ?)";
		jdbcTemplate.update(insertExcerptSQL, author, title, text, comments);

		String setExcerptIdSQL = "SET @excerptID = LAST_INSERT_ID();";
		jdbcTemplate.update(setExcerptIdSQL);

		for (String tag : tags) {
			String insertTagSQL = "INSERT INTO Tag SET description = ?";
			jdbcTemplate.update(insertTagSQL, tag);
			String setTagIdSQL = "SET @tagID = LAST_INSERT_ID();";
			jdbcTemplate.update(setTagIdSQL);
			String insertTagMapSQL = "INSERT INTO tagmap (excerptID, tagID) VALUES(@excerptID, @tagID)";
			jdbcTemplate.update(insertTagMapSQL);
		}
	}

	@Override
	public void edit(int excerptID, String... params) {
		String author = params[0].trim();
		String title = params[1].trim();
		String text = params[2].trim();
		String tags = params[3].trim();

		String excerptSQL = "UPDATE Excerpt SET author = ?, title = ?, text = ? WHERE excerptID=?";
		jdbcTemplate.update(excerptSQL, author, title, text, excerptID);

		String tagMapSQL = "SELECT tagID FROM TagMap WHERE excerptID = ?";
		int tagID = jdbcTemplate.queryForObject(tagMapSQL, new Object[] { excerptID }, Integer.class);

		String updateTag = "UPDATE Tag SET description = ? WHERE tagID=?";
		jdbcTemplate.update(updateTag, tags, tagID);
	}

	@Override
	public void delete(int excerptId) {
		String SQL = "DELETE FROM Excerpt WHERE excerptID=?";
		jdbcTemplate.update(SQL, excerptId);
	}

	@Override
	public List<Excerpt> getAll() {
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new ExcerptMapper());
		if (excerpts.size() > 0) {
			return formatEntries(excerpts);
		} else {
			return excerpts;
		}
	}

	@Override
	public List<Excerpt> getByAuthor(String... params) {
		String author = params[0];
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE author = ? ORDER BY excerptID";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { author }, new ExcerptMapper());
		if (excerpts.size() > 0) {
			return formatEntries(excerpts);
		} else {
			return excerpts;
		}
	}

	@Override
	public List<Excerpt> getByTitle(String... params) {
		String title = params[0];
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE title = ? ORDER BY excerptID";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { title }, new ExcerptMapper());
		if (excerpts.size() > 0) {
			return formatEntries(excerpts);
		} else {
			return excerpts;
		}
	}

	@Override
	public List<Excerpt> getByTag(String... params) {
		String tag = params[0];
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE t.description = ? ORDER BY excerptID";
		List<Excerpt> excerpts = jdbcTemplate.query(SQL, new String[] { tag }, new ExcerptMapper());
		if (excerpts.size() > 0) {
			return formatEntries(excerpts);
		} else {
			return excerpts;
		}
	}

	@Override
	public List<Map<String, Object>> countSelectedByAuthor(String... params) {
		String title = params[0];
		return jdbcTemplate.queryForList(
				"SELECT author, COUNT(*) as count FROM Excerpt WHERE title = ? GROUP BY author",
				new Object[] { title });
	}

	@Override
	public List<Excerpt> formatEntries(List<Excerpt> excerpts) {

		List<Excerpt> results = new ArrayList<Excerpt>();

		for (int i = 0, j = 1; i < excerpts.size() - 1; i++, j++) {
			if (excerpts.get(i).getExcerptID() == excerpts.get(j).getExcerptID()) {
				excerpts.get(j).mergeTags(excerpts.get(i));
				continue;
			}
			results.add(excerpts.get(i));
		}
		results.add(excerpts.get(excerpts.size() - 1));
		return results;
	}

	@Override
	public void emptyExcerptsDb() {
		String removeChecksSQL = "SET FOREIGN_KEY_CHECKS = 0";
		jdbcTemplate.update(removeChecksSQL);
		String tagmapSQL = "TRUNCATE table Tagmap";
		jdbcTemplate.update(tagmapSQL);
		String excerptSQL = "TRUNCATE table Excerpt";
		jdbcTemplate.update(excerptSQL);
		String tagSQL = "TRUNCATE table Tag";
		jdbcTemplate.update(tagSQL);
		String renewChecksSQL = "SET FOREIGN_KEY_CHECKS = 1";
		jdbcTemplate.update(renewChecksSQL);
	}

	@Override
	public int countExcerpts() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt", Integer.class);
	}

	@Override
	public Excerpt getByID(int excerptID) {
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE e.excerptID = ?";

		try {
			Excerpt excerpt = jdbcTemplate.queryForObject(SQL, new Object[] { excerptID }, new ExcerptMapper());
			return excerpt;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
