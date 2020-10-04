package com.excerpts.springboot.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DbMaintenanceImpl implements DbMaintenance {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void resetExcerptTables() {

		// it is required to switch off foreign key checks to change the tables and
		// afterwards reset it again

		String removeChecksSQL = "SET FOREIGN_KEY_CHECKS = 0";
		jdbcTemplate.update(removeChecksSQL);

		String excerptSQL = "TRUNCATE table Excerpt";
		jdbcTemplate.update(excerptSQL);

		String renewChecksSQL = "SET FOREIGN_KEY_CHECKS = 1";
		jdbcTemplate.update(renewChecksSQL);
	}

	@Override
	public void resetOutlineTables() {

		String removeChecksSQL = "SET FOREIGN_KEY_CHECKS = 0";
		jdbcTemplate.update(removeChecksSQL);

		String excerptSQL = "TRUNCATE table Outline";
		jdbcTemplate.update(excerptSQL);

		String renewChecksSQL = "SET FOREIGN_KEY_CHECKS = 1";
		jdbcTemplate.update(renewChecksSQL);

	}

	@Override
	public void resetTagTables() {

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
