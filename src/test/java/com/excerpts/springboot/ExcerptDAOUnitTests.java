package com.excerpts.springboot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.excerpts.springboot.dao.ExcerptDAOImpl;
import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.mappers.ExcerptMapper;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ExcerptApplication.class)
class ExcerptDAOUnitTests {

	Excerpt faulkner = new Excerpt(1, "William Faulkner", "Requiem for a Nun",
			"The past is never dead. It's not even past.", "comment no 1");
	Excerpt wilde1 = new Excerpt(2, "Oscar Wilde", "Lady Windermere's Fan",
			"We are all in the gutter, but some of us are looking at the stars.", "comment no 2");
	Excerpt hasek = new Excerpt(3, "Jaroslav Hašek", "Osudy dobrého vojáka Švejka za světové války",
			"Nejspokojenější lidé jsou právě ti, kterým násilím nebyla vnucena vzdělanost.", "comment no 3");
	Excerpt wilde2 = new Excerpt(4, "Oscar Wilde", "De Profundis",
			"Most people are other people. Their thoughts are someone else's opinions, their lives a mimicry, their passions a quotation.",
			"comment no 4");
	Excerpt pinter1 = new Excerpt(5, "Harold Pinter", "The Homecoming",
			"The earth's about five million years old, at least. Who can afford to live in the past?", "comment no 5");
	Excerpt bradbury = new Excerpt(6, "Ray Bradbury", "The Homecoming",
			"If you hide your ignorance, no one will hit you and you'll never learn.", "comment no 6");
	Excerpt pinter2 = new Excerpt(7, "Harold Pinter", "The Homecoming",
			"The past is what you remember, imagine you remember, convince yourself you remember, or pretend you remember.",
			"comment no 7");

	@InjectMocks
	private ExcerptDAOImpl excerptDAO = new ExcerptDAOImpl();

	@Mock
	private JdbcTemplate mockJdbcTemplate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(excerptDAO, "jdbcTemplate", mockJdbcTemplate);
	}

	@Test
	public void shouldSaveExcerpt() throws Exception {
		Mockito.doAnswer(new Answer<Excerpt>() {
			public Excerpt answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return (Excerpt) args[0];
			}
		}).when(mockJdbcTemplate).update(anyString(), anyString(), anyString(), anyString(), anyString());
	}

	@Test
	void shouldGetAllExcerpts() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(faulkner);
		result.add(wilde1);
		result.add(hasek);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getAll());
	}

	@Test
	public void shouldGetExcerptsByTitle() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(pinter1);
		result.add(bradbury);
		result.add(pinter2);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getByTitle("The Homecoming"));
	}

	@Test
	public void shouldGetExcerptsByAuthor() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(wilde1);
		result.add(wilde2);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getByAuthor("Oscar Wilde"));
	}

	@Test
	public void shouldGetExcerptsByTag() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(pinter1);
		result.add(faulkner);
		result.add(pinter2);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getByTag("past"));
	}

	@Test
	public void shouldGetExcerptByID() throws Exception {
		List<Excerpt> result = new ArrayList<>();
		result.add(faulkner);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(ExcerptMapper.class))).thenReturn(result);
		assertEquals(result, excerptDAO.getByID(1));

	}

	@Test
	void shouldCountAllExcerpt() throws Exception {
		Mockito.when(mockJdbcTemplate.queryForObject("SELECT COUNT(*) FROM Excerpt", Integer.class)).thenReturn(4);
		assertEquals(4, excerptDAO.countAll());
	}

	@Test
	void shouldDeleteExcerpt() {

		excerptDAO.delete(1);
		verify(mockJdbcTemplate, times(1)).update(anyString(), eq(1));

	}

}
