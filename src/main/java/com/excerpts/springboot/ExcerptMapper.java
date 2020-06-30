package com.excerpts.springboot;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ExcerptMapper implements RowMapper<Excerpt> {
	public Excerpt mapRow(ResultSet rs, int rowNum) throws SQLException {

		Excerpt excerpt = new Excerpt();

		excerpt.setExcerptID(rs.getInt("excerptID"));
		excerpt.setAuthor(rs.getString("author"));
		excerpt.setTitle(rs.getString("title"));
		excerpt.setText(rs.getString("text"));
		excerpt.setTags(rs.getString("tags"));

		return excerpt;
	}
}
