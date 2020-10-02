package com.excerpts.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.excerpts.springboot.dao.excerpt.ExcerptDAOInterface;
import com.excerpts.springboot.dao.tag.TagDAOInterface;
import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.domain.Tag;

@Controller
public class MainController {

	@Autowired
	private ExcerptDAOInterface<Excerpt> exerptDAO;
	@Autowired
	private TagDAOInterface tagDAO;

	// display the index page
	@RequestMapping(value = "/")
	public String viewIndexPage(Model model) {
		model.addAttribute("excerpt", new Excerpt());
		model.addAttribute("tag", new Tag());
		return "index";
	}

	// truncates all tables and resets auto-increment
	@RequestMapping(value = "/truncateTables")
	public String truncateTables() {

		tagDAO.resetTables();
		exerptDAO.resetTables();

		return "redirect:/";
	}

}
