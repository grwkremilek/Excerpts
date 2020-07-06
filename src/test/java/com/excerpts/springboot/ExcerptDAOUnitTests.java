package com.excerpts.springboot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ExcerptApplication.class)
class ExcerptDAOUnitTests {

	Excerpt faulkner = new Excerpt(1, "William Faulkner", "Requiem for a Nun",
			"The past is never dead. It's not even past.", "past; history", "comment no 1");
	Excerpt wilde1 = new Excerpt(2, "Oscar Wilde", "Lady Windermere's Fan",
			"We are all in the gutter, but some of us are looking at the stars.", "idealism", "comment no 2");
	Excerpt hasek = new Excerpt(3, "Jaroslav Hašek", "Osudy dobrého vojáka Švejka za světové války",
			"Nejspokojenější lidé jsou právě ti, kterým násilím nebyla vnucena vzdělanost.", "education",
			"comment no 3");
	Excerpt wilde2 = new Excerpt(4, "Oscar Wilde", "De Profundis",
			"Most people are other people. Their thoughts are someone else's opinions, their lives a mimicry, their passions a quotation.",
			"people", "comment no 4");
	Excerpt pinter1 = new Excerpt(5, "Harold Pinter", "The Homecoming",
			"The earth's about five million years old, at least. Who can afford to live in the past?", "past",
			"comment no 5");
	Excerpt bradbury = new Excerpt(6, "Ray Bradbury", "The Homecoming",
			"If you hide your ignorance, no one will hit you and you'll never learn.", "learning; intelligence",
			"comment no 6");
	Excerpt pinter2 = new Excerpt(7, "Harold Pinter", "The Homecoming",
			"The past is what you remember, imagine you remember, convince yourself you remember, or pretend you remember.",
			"past", "comment no 7");

	@InjectMocks
	private ExcerptDAO excerptDAO = new ExcerptDAO();

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(excerptDAO, "jdbcTemplate", jdbcTemplate);

	}

	@Test
	void testCountExcerpts() throws Exception {
		Mockito.when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt", Integer.class)).thenReturn(4);
		assertEquals(4, excerptDAO.countExcerpts());
	}

	@Test
	void testgetAll() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(faulkner);
		result.add(wilde1);
		result.add(hasek);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getAll());
	}

	@Test
	public void testDelete() throws Exception {
		excerptDAO.delete(3);
		Excerpt nullExcerptDAO = excerptDAO.getByID(3);
		assertNull(nullExcerptDAO);
	}

	@Test
	public void testGetByAuthor() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(wilde1);
		result.add(wilde2);

		ReflectionTestUtils.setField(excerptDAO, "jdbcTemplate", jdbcTemplate);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getByAuthor("Oscar Wilde"));
	}

	@Test
	public void testGetByTitle() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(pinter1);
		result.add(bradbury);
		result.add(pinter2);

		ReflectionTestUtils.setField(excerptDAO, "jdbcTemplate", jdbcTemplate);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getByTitle("The Homecoming"));
	}

	@Test
	public void testGetByTag() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(pinter1);
		result.add(faulkner);
		result.add(pinter2);

		ReflectionTestUtils.setField(excerptDAO, "jdbcTemplate", jdbcTemplate);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getByTag("past"));
	}

	@Test
	public void testCountSelectedByAuthor() throws Exception {

		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> home = new HashMap<>();
		home.put("author", "Harold Pinter");
		home.put("count", "2");
		result.add(home);

		ReflectionTestUtils.setField(excerptDAO, "jdbcTemplate", jdbcTemplate);
		Mockito.when(jdbcTemplate.queryForList(anyString(), ArgumentMatchers.<Object>any())).thenReturn(result);
		List<Map<String, Object>> returnedResult = excerptDAO.countSelectedByAuthor("The Homecoming");
		assertEquals(result, returnedResult);
	}
}
