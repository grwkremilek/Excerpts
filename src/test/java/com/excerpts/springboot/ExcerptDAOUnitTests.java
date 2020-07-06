package com.excerpts.springboot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
	Excerpt wilde = new Excerpt(2, "Oscar Wilde", "Lady Windermere's Fan",
			"We are all in the gutter, but some of us are looking at the stars.", "idealism", "comment no 2");
	Excerpt hasek = new Excerpt(3, "Jaroslav Hašek", "Osudy dobrého vojáka Švejka za světové války",
			"Nejspokojenější lidé jsou právě ti, kterým násilím nebyla vnucena vzdělanost.", "education",
			"comment no 3");

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
	void testCountExcerpts() {
		Mockito.when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt", Integer.class)).thenReturn(4);
		assertEquals(4, excerptDAO.countExcerpts());
	}

	@Test
	void testgetAll() {
		List<Excerpt> result = new ArrayList<>();
		result.add(faulkner);
		result.add(wilde);
		result.add(hasek);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getAll());
	}
}
