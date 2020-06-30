package com.excerpts.springboot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExcerptDAO implements DAO<Excerpt> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Excerpt> getAll() {
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID";
		/*
		 * String SQL =
		 * "select excerptID, author, title, text, description as tags from tagmap natural join excerpt natural join tag;"
		 * ;
		 */
		List<Excerpt> extracts = jdbcTemplate.query(SQL, new ExcerptMapper());
		return extracts;
	}

	@Override
	public int countAll() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt", Integer.class);
	}

	@Override
	public void save(String... params) {
		String author = params[0].trim();
		String title = params[1].trim();
		String text = params[2].trim();
		List<String> tags = Arrays.asList(params[3].split("\\s*;\\s*"));

		String insertExcerptSQL = "INSERT INTO Excerpt (author, title, text) values (?, ?, ?)";
		jdbcTemplate.update(insertExcerptSQL, author, title, text);

		String setExcerptIdSQL = "SET @excerptID = LAST_INSERT_ID();";
		jdbcTemplate.update(setExcerptIdSQL);

		for (String tag : tags) {
			String insertTagSQL = "INSERT IGNORE INTO Tag SET description = ?";

			// if the tags is already present in the tag table, add a new excerptID, else
			// create a new row
			if (jdbcTemplate.update(insertTagSQL, tag) == 0) {
				String getTagID = "SELECT tagID FROM tag WHERE description=?";
				int tagID = jdbcTemplate.queryForObject(getTagID, new Object[] { tag }, Integer.class);
				String insertTagMapSQL = "INSERT INTO tagmap (excerptID, tagID) VALUES(@excerptID, ?)";
				jdbcTemplate.update(insertTagMapSQL, tagID);

			} else {
				String setTagIdSQL = "SET @tagID = LAST_INSERT_ID();";
				jdbcTemplate.update(setTagIdSQL);
				String insertTagMapSQL = "INSERT INTO tagmap (excerptID, tagID) VALUES(@excerptID, @tagID)";
				jdbcTemplate.update(insertTagMapSQL);
			}
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
	public List<Excerpt> getByAuthor(String... params) {
		String author = params[0];
		/* String SQL = "SELECT * FROM Excerpt WHERE author = ? ORDER BY excerptID"; */
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE author = ? ORDER BY excerptID";
		List<Excerpt> extracts = jdbcTemplate.query(SQL, new String[] { author }, new ExcerptMapper());
		return extracts;
	}

	@Override
	public List<Excerpt> getByTitle(String... params) {
		String title = params[0];
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE title = ? ORDER BY excerptID";
		List<Excerpt> extracts = jdbcTemplate.query(SQL, new String[] { title }, new ExcerptMapper());
		return extracts;
	}

	public List<Excerpt> getByTag(String... params) {
		String tag = params[0];
		System.out.println(tag);
		String SQL = "SELECT e.*, t.description AS tags from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE t.description = ? ORDER BY excerptID";
		List<Excerpt> extracts = jdbcTemplate.query(SQL, new String[] { tag }, new ExcerptMapper());
		return extracts;
	}

	@Override
	public List<Map<String, Object>> countSelectedByAuthor(String[] params) {
		String title = params[0];
		return jdbcTemplate.queryForList(
				"SELECT author, COUNT(*) as count FROM Excerpt WHERE title = ? GROUP BY author",
				new Object[] { title });
	}
}
