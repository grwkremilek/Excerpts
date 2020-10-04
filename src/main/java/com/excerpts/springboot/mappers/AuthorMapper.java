package com.excerpts.springboot.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.excerpts.springboot.domain.Author;

public class AuthorMapper implements RowMapper<Author> {
	public Author mapRow(ResultSet rs, int rowNum) throws SQLException {

		Author author = new Author();

		author.setExcerptID(rs.getInt("excerptID"));
		author.setName(rs.getString("name"));

		return author;
	}
}
