package com.excerpts.springboot.maintenance;

public interface DbMaintenance {

	// Methods deleting all entries in the database tables and restarting
	// auto-increment in the ID column

	public void resetExcerptTables();

	public void resetOutlineTables();

	public void resetTagTables();

}
