package com.excerpts.springboot;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.excerpts.springboot.dao.TagDAO;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ExcerptApplication.class)
class TagDAOUnitTests {

	@InjectMocks
	private TagDAO tagDAO = new TagDAO();

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(tagDAO, "jdbcTemplate", jdbcTemplate);
	}

	@Test
	public void testSave() throws Exception {

	}

	@Test
	public void testEdit() throws Exception {

	}

	@Test
	public void testDelete() throws Exception {

	}

	@Test
	void testgetAll() throws Exception {

	}

	@Test
	public void testgetByAuthor() throws Exception {

	}

	@Test
	public void testgetByTitle() throws Exception {

	}

	@Test
	public void testgetByTag() throws Exception {

	}

	@Test
	public void testgetByID() throws Exception {

	}

	@Test
	void testCountAll() throws Exception {
		/*
		 * Mockito.when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt",
		 * Integer.class)).thenReturn(4); assertEquals(4, excerptDAO.countAll());
		 */
	}

	@Test
	public void testCountSelectedByAuthor() throws Exception {

	}
}
