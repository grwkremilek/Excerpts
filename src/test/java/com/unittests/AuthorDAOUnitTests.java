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
import com.excerpts.springboot.dao.AuthorDAOImpl;
import com.excerpts.springboot.domain.Author;
import com.excerpts.springboot.mappers.AuthorMapper;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ExcerptApplication.class)
class AuthorDAOUnitTests {
	
	Author james = new Author(1, "Henry James");
	Author eliot = new Author(2, "George Eliot");
	Author anonymous = new Author(3, "anonymous");
		
	@InjectMocks
	private AuthorDAOImpl authorDAO = new AuthorDAOImpl();

	@Mock
	private JdbcTemplate mockJdbcTemplate;

	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(authorDAO, "jdbcTemplate", mockJdbcTemplate);
		}

	@Test
	public void shouldSaveAuthor() throws Exception {
		
		Mockito.doAnswer(new Answer<Author>() {
			public Author answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return (Author) args[0];
			}
		}).when(mockJdbcTemplate).update(anyString());
	}
	
	@Test
	void shouldGetAllAuthors() throws Exception {
		
		List<Author> result = new ArrayList<>();
		result.add(james);
		result.add(eliot);
		result.add(anonymous);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.any(AuthorMapper.class))).thenReturn(result);
		assertEquals(result, authorDAO.getAll());
	}
	
	@Test
	public void shouldGetAuthorByTitle() throws Exception {
		
		List<Author> result = new ArrayList<>();
		result.add(eliot);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(AuthorMapper.class))).thenReturn(result);
		assertEquals(result, authorDAO.getByTitle("The Mill on the Floss"));
	}

	@Test
	public void shouldGetAuthorsByTag() throws Exception {
		
		List<Author> result = new ArrayList<>();
		result.add(james);
		
		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(AuthorMapper.class))).thenReturn(result);
		assertEquals(result, authorDAO.getByTag("style"));
	}

	@Test
	public void shouldGetAuthorsByID() throws Exception {

		List<Author> result = new ArrayList<>();
		result.add(james);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(AuthorMapper.class))).thenReturn(result);
		assertEquals(result, authorDAO.getByID(1));
	}

	@Test
	void shouldCountAllAuthors() throws Exception {

		Mockito.when(mockJdbcTemplate.queryForObject("SELECT COUNT(*) FROM Author", Integer.class)).thenReturn(3);
		assertEquals(3, authorDAO.countAll());
	}
}
