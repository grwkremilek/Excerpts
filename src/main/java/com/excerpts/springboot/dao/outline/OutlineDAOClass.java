package com.excerpts.springboot.dao.outline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.excerpts.springboot.domain.Outline;
import com.excerpts.springboot.mappers.OutlineMapper;

@Repository
public class OutlineDAOClass implements OutlineDAOInterface<Outline> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void save(int outlineID, String... params) {

		String plot = params[0].trim();
		String comments = params[1].trim();

		if (outlineID == 0) {

			String insertOutlineSQL = "INSERT INTO Outline (plot, comments) values (?, ?)";
			jdbcTemplate.update(insertOutlineSQL, plot, comments);

		} else {

			String updateOutlineSQL = "UPDATE Outline SET plot = ?, comments = ? WHERE outlineID=?";
			jdbcTemplate.update(updateOutlineSQL, plot, comments);
		}
	}

	@Override
	public List<Outline> getAll() {

		String SQL = "SELECT * FROM Outline";
		List<Outline> outlines = jdbcTemplate.query(SQL, new OutlineMapper());
		return outlines;
	}

	@Override
	public List<Outline> getByTitle(String... params) {

		String title = params[0];
		String SQL = "SELECT * FROM Outline WHERE title = ? ORDER BY outlineID";
		List<Outline> outlines = jdbcTemplate.query(SQL, new String[] { title }, new OutlineMapper());
		return outlines;
	}

	@Override
	public List<Outline> getByTag(String... params) {

		String description = params[0];
		String SQL = "SELECT e.* from Outline AS e LEFT JOIN tagmap AS m ON m.outlineID = e.outlineID LEFT JOIN tag AS t ON t.tagID = m.tagID WHERE t.description = ?";
		List<Outline> outlines = jdbcTemplate.query(SQL, new String[] { description }, new OutlineMapper());
		return outlines;
	}

	@Override
	public List<Outline> getByID(int outlineID) {

		String SQL = "SELECT * FROM Outline WHERE excerptID = ?";

		try {
			List<Outline> outlines = jdbcTemplate.query(SQL, new Object[] { outlineID }, new OutlineMapper());
			return outlines;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
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

	@Override
	public int countAll() {

		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Outline", Integer.class);
	}

	@Override
	public void delete(int outlineID) {

		String SQL = "DELETE FROM Outline WHERE outlineID = ?";
		jdbcTemplate.update(SQL, outlineID);

	}
}
