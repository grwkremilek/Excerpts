package com.excerpts.springboot.dao.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.excerpts.springboot.domain.Outline;
import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.mappers.TagMapper;

@Repository
public class TagDAOClass implements TagDAOInterface {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void save(int excerptID, String... params) {

		String[] descriptions = params[0].trim().split("\\s*;\\s*");

		if (excerptID == 0) {

			/* fetch the ID of the newly created excerpt */
			String getExcerptIDSQL = "SELECT LAST_INSERT_ID()";
			excerptID = jdbcTemplate.queryForObject(getExcerptIDSQL, Integer.class);

		} else {

			/* delete residual mappings of the edited excerpt */
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

	@Override
	public List<Tag> getAll() {

		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID";
		List<Tag> tags = jdbcTemplate.query(SQL, new TagMapper());
		return tags;
	}

	@Override
	public List<Tag> getByTitle(String... params) {

		String title = params[0].trim();
		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE e.title = ?";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { title }, new TagMapper());
		return tags;
	}

	@Override
	public List<Tag> getByAuthor(String... params) {

		String author = params[0].trim();
		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE e.author = ? ORDER BY e.title";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { author }, new TagMapper());
		return tags;
	}

	//returns all tags that co-occur with the searched tag in the given entries
	@Override
	public List<Tag> getByTag(String... params) {

		String description = params[0].trim();
		String SQL = "select tm.excerptID, t.description from tagmap as tm left join tag as t on tm.tagID = t.tagID where tm.excerptID in ((select tm1.excerptID from tagmap tm1 join tagmap tm2 on tm1.excerptid = tm2.excerptid join tag t2 on tm2.tagid = t2.tagid where tm1.tagid = t2.tagid and t2.description = ?))";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { description }, new TagMapper());
		return tags;

	}

	@Override
	public List<Tag> getByID(int excerptID) {

		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE e.excerptID = ?";
		List<Tag> tags = jdbcTemplate.query(SQL, new Integer[] { excerptID }, new TagMapper());
		return tags;
	}

	@Override
	public int countAll() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Tag", Integer.class);
	}

	@Override
	public void delete(int excerptID) {

		String deleteTagSQL = "DELETE FROM Tagmap WHERE excerptID = ?";
		jdbcTemplate.update(deleteTagSQL, excerptID);
	}

	@Override
	public List<Outline> getByGenre(String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Outline> getByResource(String... params) {
		// TODO Auto-generated method stub
		return null;
	}

}
