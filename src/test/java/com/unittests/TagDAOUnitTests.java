package com.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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

import com.excerpts.springboot.ExcerptApplication;
import com.excerpts.springboot.dao.TagDAOImpl;
import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.mappers.TagMapper;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ExcerptApplication.class)
class TagDAOUnitTests {

	Tag history = new Tag(1, "history");
	Tag style = new Tag(2, "style");
	Tag irony = new Tag(3, "irony");

	@InjectMocks
	private TagDAOImpl tagDAO = new TagDAOImpl();

	@Mock
	private JdbcTemplate mockJdbcTemplate;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(tagDAO, "jdbcTemplate", mockJdbcTemplate);
	}

	@Test
	public void shouldSaveTags() throws Exception {

		Mockito.doAnswer(new Answer<Tag>() {
			public Tag answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return (Tag) args[0];
			}
		}).when(mockJdbcTemplate).update(anyString());
	}

	@Test
	public void shouldGetAllTags() throws Exception {

		List<Tag> result = new ArrayList<>();
		result.add(history);
		result.add(style);
		result.add(irony);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getAll());
	}

	@Test
	public void shouldGetTagsByTitle() throws Exception {

		List<Tag> result = new ArrayList<>();
		result.add(irony);
		result.add(style);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getByTitle("Lady Windermere's Fan"));
	}

	@Test
	public void shouldGetTagsByAuthor() throws Exception {

		List<Tag> result = new ArrayList<>();
		result.add(irony);
		result.add(style);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getByAuthor("Oscar Wilde"));
	}

	@Test
	public void shouldGetTagsByTag() throws Exception {

		List<Tag> result = new ArrayList<>();
		result.add(history);
		result.add(style);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getByTag("history"));
	}

	@Test
	public void shouldgetTagsByID() throws Exception {

		List<Tag> result = new ArrayList<>();
		result.add(history);
		result.add(style);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getByID(1));
	}

	@Test
	public void shouldCountAllTags() throws Exception {

		Mockito.when(mockJdbcTemplate.queryForObject("SELECT COUNT(*) FROM Tag", Integer.class)).thenReturn(3);
		assertEquals(3, tagDAO.countAll());
	}
}
