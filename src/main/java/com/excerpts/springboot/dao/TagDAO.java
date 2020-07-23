package com.excerpts.springboot.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.mappers.TagMapper;

@Repository
public class TagDAO implements DAO<Tag> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Method creating a new tag if not already present in the tag table and mapping
	 * between a tag and an excerpt in the tagmap table.
	 *
	 * @param excerptID
	 * 
	 * @return tagID
	 */

	@Transactional
	@Override
	public void save(int excerptID, String... params) {

		String[] descriptions = params[0].trim().split(";");

		if (excerptID == 0) {

			/* fetch the ID of the newly created excerpt */
			String getExcerptIDSQL = "SELECT LAST_INSERT_ID()";
			excerptID = jdbcTemplate.queryForObject(getExcerptIDSQL, Integer.class);
		} else {

			/* delete residual mappings of edited excerpt */
			String deleteMappingsSQL = "DELETE FROM tagmap WHERE excerptID = ?";
			jdbcTemplate.update(deleteMappingsSQL, excerptID);
		}

		for (String description : descriptions) {

			String findTagSQL = "SELECT count(*) FROM tag WHERE description = ?";
			int tagExists = jdbcTemplate.queryForObject(findTagSQL, new Object[] { description }, Integer.class);

			/* if the tag does not exist, create one and also update mappings */
			if (tagExists == 0) {

				String insertTagSQL = "INSERT INTO tag (description) values (?)";
				jdbcTemplate.update(insertTagSQL, description);

				String getTagIdSQL = "SELECT LAST_INSERT_ID();";
				int tagIDCurrent = jdbcTemplate.queryForObject(getTagIdSQL, Integer.class);

				String insertTagMapSQL = "INSERT INTO tagmap (excerptID, tagID) VALUES(?, ?)";
				jdbcTemplate.update(insertTagMapSQL, excerptID, tagIDCurrent);

				/* if the tag exists, get its ID and use it to update mappings */
			} else {

				String getTagIdSQL = "SELECT tagID FROM tag WHERE description = ?";
				int tagIDCurrent = jdbcTemplate.queryForObject(getTagIdSQL, new String[] { description },
						Integer.class);

				String insertTagMapSQL = "INSERT INTO tagmap (excerptID, tagID) VALUES(?, ?)";
				jdbcTemplate.update(insertTagMapSQL, excerptID, tagIDCurrent);

			}
		}
	}

	/**
	 * Method returning a list of all excerpts
	 *
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Tag> getAll() {

		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID";
		List<Tag> tags = jdbcTemplate.query(SQL, new TagMapper());
		return tags;
	}

	/**
	 * Method returning a list of all excerpts from a book of the given title.
	 *
	 * @param title user provided title of the book
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Tag> getByTitle(String... params) {

		String title = params[0].trim();
		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE e.title = ? ORDER BY e.author";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { title }, new TagMapper());
		return tags;
	}

	/**
	 * Method returning a list of all excerpts by an author.
	 *
	 * @param author user provided name of an author
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Tag> getByAuthor(String... params) {

		String author = params[0].trim();
		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE e.author = ? ORDER BY e.title";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { author }, new TagMapper());
		return tags;
	}

	/**
	 * Method returning a list of all excerpts with the given tag
	 *
	 * @param tag user provided tag
	 * @return excerpts a list of excerpts
	 */
	@Override
	public List<Tag> getByTag(String... params) {

		String description = params[0].trim();
		String SQL = "select tm.excerptID, t.description from tagmap as tm left join tag as t on tm.tagID = t.tagID where tm.excerptID in ((select tm1.excerptID from tagmap tm1 join tagmap tm2 on tm1.excerptid = tm2.excerptid join tag t2 on tm2.tagid = t2.tagid where tm1.tagid = t2.tagid and t2.description = ?))";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { description }, new TagMapper());
		return tags;

	}

	/**
	 * Method returning an excerpt with the given ID
	 *
	 * @param excerptID user provided excerptID
	 * @return excerpt
	 */
	@Override
	public List<Tag> getByID(int excerptID) {

		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE e.excerptID = ?";
		List<Tag> tags = jdbcTemplate.query(SQL, new Integer[] { excerptID }, new TagMapper());
		return tags;
	}

	/**
	 * Help method counting all entries in Excerpt used in unit tests
	 * 
	 * @return count
	 */
	@Override
	public int countAll() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Tag", Integer.class);
	}

	/**
	 * Method deleting an existing excerpt
	 *
	 * @param excerptID
	 */
	@Override
	public void delete(int excerptID) {

		String deleteTagSQL = "DELETE FROM Tagmap WHERE excerptID = ?";
		jdbcTemplate.update(deleteTagSQL, excerptID);
	}

	/**
	 * Method deleting all entries in all the tables and restarting auto-increment
	 */
	public void resetTables() {

		String removeChecksSQL = "SET FOREIGN_KEY_CHECKS = 0";
		jdbcTemplate.update(removeChecksSQL);

		String tagmapSQL = "TRUNCATE table Tagmap";
		jdbcTemplate.update(tagmapSQL);
		String tagSQL = "TRUNCATE table Tag";
		jdbcTemplate.update(tagSQL);

		String renewChecksSQL = "SET FOREIGN_KEY_CHECKS = 1";
		jdbcTemplate.update(renewChecksSQL);

	}
}
