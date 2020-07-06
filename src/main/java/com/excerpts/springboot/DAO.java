package com.excerpts.springboot;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DAO<T> {

	/**
	 * Method creating a new excerpt in the table Excerpts and adding new tags to
	 * the table Tag.
	 *
	 * @param author   user provided name of the author
	 * @param title    user provided title of the book
	 * @param text     user provided text of the excerpt
	 * @param comments user provided comments to the text
	 * @param tags     user provided tags
	 */
	public void save(String... params);

	/**
	 * Method editing an existing excerpt (incl. tags)
	 *
	 * @param excerptID
	 * @param author
	 * @param title
	 * @param text
	 * @param comments
	 * @param tags
	 */
	public void edit(int excerptID, String... params);

	/**
	 * Method deleting an existing excerpt (table Excerpt) and respective tags
	 * (tables Tag + Tagmap)
	 *
	 * @param excerptID
	 */
	public void delete(int excerptId);

	/**
	 * Method returning a list of all excerpts (incl. tags from the table Tag)
	 *
	 * @return excerpts a list of excerpts
	 */
	public List<T> getAll();

	/**
	 * Method returning a list of all excerpts by an author.
	 *
	 * @param author user provided name of an author
	 * @return excerpts a list of excerpts
	 */
	public List<T> getByAuthor(String... params);

	/**
	 * Method returning a list of all excerpts from a book of the given title.
	 *
	 * @param title user provided title of the book
	 * @return excerpts a list of excerpts
	 */
	public List<T> getByTitle(String... params);

	/**
	 * Method returning a list of all excerpts with the given tag
	 *
	 * @param tag user provided tag
	 * @return excerpts a list of excerpts
	 */
	public List<T> getByTag(String... params);

	/**
	 * Method counting results returned from getByTitle(String... params) (in case
	 * of two books with the same title)
	 *
	 * @param title
	 * @return excerpts a list of excerpts
	 */
	public List<Map<String, Object>> countSelectedByAuthor(String... params);

	/**
	 * Method formatting output from the table Excerpt and the table Tag to create a
	 * requested extract object with all the tags
	 *
	 * @param excerpts a lists of unformatted excerpts
	 * @return excerpts a list of formatted excerpts
	 */
	public List<T> formatEntries(List<T> excerpts);

	/**
	 * Method deleting all entries in all the tables and restarting auto-increment
	 */
	public void emptyExcerptsDb();

	public int countExcerpts();
	
	public T getByID(int excerptID);

}
