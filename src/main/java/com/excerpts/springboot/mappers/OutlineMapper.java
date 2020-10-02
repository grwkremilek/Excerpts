package com.excerpts.springboot.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.excerpts.springboot.domain.Outline;

public class OutlineMapper implements RowMapper<Outline> {
	public Outline mapRow(ResultSet rs, int rowNum) throws SQLException {

		Outline outline = new Outline();

		outline.setOutlineID(rs.getInt("outlineID"));
		outline.setPlot(rs.getString("plot"));
		outline.setComments(rs.getString("comments"));

		return outline;
	}
}