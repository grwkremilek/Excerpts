package com.unittests;

import static org.junit.jupiter.api.Assertions.*;
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
import com.excerpts.springboot.dao.OutlineDAOImpl;
import com.excerpts.springboot.domain.Outline;
import com.excerpts.springboot.mappers.OutlineMapper;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ExcerptApplication.class)
class OutlineDAOUnitTests {

	Outline outline1 = new Outline(1, "plot1", "comments1");
	Outline outline2 = new Outline(2, "plot2", "comments2");
	Outline outline3 = new Outline(3, "plot3", "comments3");

	@InjectMocks
	private OutlineDAOImpl outlineDAO = new OutlineDAOImpl();

	@Mock
	private JdbcTemplate mockJdbcTemplate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(outlineDAO, "jdbcTemplate", mockJdbcTemplate);
	}

	@Test
	public void shouldSaveOutline() throws Exception {

		Mockito.doAnswer(new Answer<Outline>() {
			public Outline answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return (Outline) args[0];
			}
		}).when(mockJdbcTemplate).update(anyString(), anyString());
	}

	@Test
	public void shouldGetAllOutlines() throws Exception {

		List<Outline> result = new ArrayList<>();
		result.add(outline1);
		result.add(outline2);
		result.add(outline3);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.any(OutlineMapper.class))).thenReturn(result);
		assertEquals(result, outlineDAO.getAll());
	}

	@Test
	public void shouldGetOutlinessByTitle() throws Exception {

		// TODO
	}

	@Test
	public void shouldGetOutlinesByTag() throws Exception {

		// TODO
	}

	@Test
	public void shouldGetOutlineByID() throws Exception {

		List<Outline> result = new ArrayList<>();
		result.add(outline1);

		Mockito.when(mockJdbcTemplate.query(anyString(), ArgumentMatchers.<Object[]>any(),
				ArgumentMatchers.any(OutlineMapper.class))).thenReturn(result);
		assertEquals(result, outlineDAO.getByID(1));
	}

	@Test
	public void shouldCountAllOutline() throws Exception {

		Mockito.when(mockJdbcTemplate.queryForObject("SELECT COUNT(*) FROM Outline", Integer.class)).thenReturn(3);
		assertEquals(3, outlineDAO.countAll());
	}
}
