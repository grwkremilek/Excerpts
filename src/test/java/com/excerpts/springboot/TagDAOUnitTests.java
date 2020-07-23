package com.excerpts.springboot;

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

import com.excerpts.springboot.dao.TagDAO;
import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.mappers.TagMapper;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ExcerptApplication.class)
class TagDAOUnitTests {
	
	Excerpt faulkner = new Excerpt(1, "William Faulkner", "Requiem for a Nun",
			"The past is never dead. It's not even past.", "comment no 1");
	Excerpt wilde1 = new Excerpt(2, "Oscar Wilde", "Lady Windermere's Fan",
			"We are all in the gutter, but some of us are looking at the stars.", "comment no 2");
	
	Tag history = new Tag(1, "history");
	Tag style = new Tag(2, "style");
	Tag irony = new Tag(3, "irony");
	
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
		Mockito.doAnswer(new Answer<Tag>() {
			public Tag answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return (Tag) args[0];
			}
		}).when(jdbcTemplate).update(anyString());

	}

	@Test
	void testgetAll() throws Exception {
		List<Tag> result = new ArrayList<>();
		result.add(history);
		result.add(style);
		result.add(irony);
		
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getAll());
	}

	@Test
	public void testgetByTitle() throws Exception {
		List<Tag> result = new ArrayList<>();
		result.add(irony);
		result.add(style);

		/*TO DO dependence on Excerpt*/
		ReflectionTestUtils.setField(tagDAO, "jdbcTemplate", jdbcTemplate);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getByTitle("Lady Windermere's Fan"));

	}

	@Test
	public void testgetByAuthor() throws Exception {
		List<Tag> result = new ArrayList<>();
		result.add(irony);
		result.add(style);

		/*TO DO dependence on Excerpt*/
		ReflectionTestUtils.setField(tagDAO, "jdbcTemplate", jdbcTemplate);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getByAuthor("Oscar Wilde"));

	}

	@Test
	public void testgetByTag() throws Exception {
		List<Tag> result = new ArrayList<>();
		result.add(history);
		result.add(style);

		/*TO DO dependence on Excerpt*/
		ReflectionTestUtils.setField(tagDAO, "jdbcTemplate", jdbcTemplate);
		Mockito.when(jdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(TagMapper.class))).thenReturn(result);
		assertEquals(result, tagDAO.getByTag("history"));

	}

	@Test
	public void testgetByID() throws Exception {

	}

	@Test
	void testCountAll() throws Exception {
		
		  Mockito.when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Tag",
		  Integer.class)).thenReturn(3); 
		  assertEquals(3, tagDAO.countAll());
		 
	}

	@Test
	public void testDelete() throws Exception {

	}
}
