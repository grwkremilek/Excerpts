package com.excerpts.springboot.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.mappers.TagMapper;

@Component("tagDAO")
public class TagDAO implements ParameterDAO<Tag> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void save(int excerptID, String... params) {

		String[] descriptions = params[0].trim().split("\\s*;\\s*");

		if (excerptID == 0) {

			/* fetch the ID of the newly created excerpt */
			String getExcerptIDSQL = "SELECT excerptID FROM Excerpt ORDER BY excerptID DESC LIMIT 1";
			excerptID = jdbcTemplate.queryForObject(getExcerptIDSQL, Integer.class);

		} else {

			/* delete residual mappings of the edited excerpt */
			String deleteMappingsSQL = "DELETE FROM Tagmap WHERE excerptID = ?";
			jdbcTemplate.update(deleteMappingsSQL, excerptID);
		}

		for (String description : descriptions) {

			String findTagSQL = "SELECT count(*) FROM Tag WHERE description = ?";
			int tagExists = jdbcTemplate.queryForObject(findTagSQL, new Object[] { description }, Integer.class);

			/* if the tag does not exist, create one and also update mappings */
			if (tagExists == 0) {

				String insertTagSQL = "INSERT INTO Tag (description) values (?)";
				jdbcTemplate.update(insertTagSQL, description);

				String getTagIdSQL = "SELECT LAST_INSERT_ID();";
				int tagIDCurrent = jdbcTemplate.queryForObject(getTagIdSQL, Integer.class);

				String insertTagMapSQL = "INSERT INTO Tagmap (excerptID, tagID) VALUES(?, ?)";
				jdbcTemplate.update(insertTagMapSQL, excerptID, tagIDCurrent);

				/* if the tag exists, get its ID and use it to update mappings */
			} else {

				String getTagIdSQL = "SELECT tagID FROM Tag WHERE description = ?";
				int tagIDCurrent = jdbcTemplate.queryForObject(getTagIdSQL, new String[] { description },
						Integer.class);

				String insertTagMapSQL = "INSERT INTO Tagmap (excerptID, tagID) VALUES(?, ?)";
				jdbcTemplate.update(insertTagMapSQL, excerptID, tagIDCurrent);
			}
		}
	}

	@Override
	public List<Tag> getAll() {

		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN Tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN Tag AS t ON t.tagID = m.tagID";
		List<Tag> tags = jdbcTemplate.query(SQL, new TagMapper());
		return tags;
	}

	@Override
	public List<Tag> getByTitle(String... params) {

		String title = params[0].trim();
		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN Tag AS t ON t.tagID = m.tagID WHERE e.title = ?";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { title }, new TagMapper());
		return tags;
	}

	//returns all tags that co-occur with the searched tag in the given entries
	@Override
	public List<Tag> getByTag(String... params) {

		String description = params[0].trim();
		String SQL = "select tm.excerptID, t.description from Tagmap as tm left join Tag as t on tm.tagID = t.tagID where tm.excerptID in ((select tm1.excerptID from Tagmap tm1 join Tagmap tm2 on tm1.excerptid = tm2.excerptid join Tag t2 on tm2.tagid = t2.tagid where tm1.tagid = t2.tagid and t2.description = ?))";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { description }, new TagMapper());
		return tags;
	}

	@Override
	public List<Tag> getByID(int excerptID) {

		String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN Tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN Tag AS t ON t.tagID = m.tagID WHERE e.excerptID = ?";
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
	public List<Tag> getByAuthor(String... params) {

		String name = params[0].trim();
		// String SQL = "SELECT e.excerptID, t.description from Excerpt AS e LEFT JOIN
		// Tagmap AS m ON m.excerptID = e.excerptID LEFT JOIN Tag AS t ON t.tagID =
		// m.tagID WHERE e.author = ? ORDER BY e.title";
		String SQL = "SELECT tm.excerptID, t.description from Tag AS t LEFT JOIN Tagmap AS tm ON t.tagID = tm.tagID LEFT JOIN Authormap AS am ON tm.excerptID = am.excerptID LEFT JOIN Author as a ON am.authorID = a.authorID WHERE a.name = ?";
		List<Tag> tags = jdbcTemplate.query(SQL, new String[] { name }, new TagMapper());
		return tags;
	}
}
