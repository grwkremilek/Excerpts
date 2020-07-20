package com.excerpts.springboot.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.excerpts.springboot.domain.Tag;

public class TagMapper implements RowMapper<Tag> {
	public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {

		Tag tag = new Tag();

		tag.setExcerptID(rs.getInt("excerptID"));
		tag.setDescription(rs.getString("description"));

		return tag;
	}
}
