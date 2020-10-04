package com.excerpts.springboot.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.excerpts.springboot.domain.Excerpt;

public class ExcerptMapper implements RowMapper<Excerpt> {
	public Excerpt mapRow(ResultSet rs, int rowNum) throws SQLException {

		Excerpt excerpt = new Excerpt();

		excerpt.setExcerptID(rs.getInt("excerptID"));
		excerpt.setTitle(rs.getString("title"));
		excerpt.setText(rs.getString("text"));
		excerpt.setComments(rs.getString("comments"));
		
		return excerpt;
	}
}
