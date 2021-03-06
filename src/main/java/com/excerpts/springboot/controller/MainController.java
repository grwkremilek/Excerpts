package com.excerpts.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.excerpts.springboot.domain.Author;
import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.maintenance.DbMaintenance;

@Controller
public class MainController {

	@Autowired
	private DbMaintenance maintenance;

	// display the index page
	@RequestMapping(value = "/")
	public String viewIndexPage(Model model) {

		model.addAttribute("excerpt", new Excerpt());
		model.addAttribute("author", new Author());
		model.addAttribute("tag", new Tag());
		return "index";
	}

	// truncates all tables and resets auto-increment
	@RequestMapping(value = "/truncateTables")
	public String truncateTables() {

		maintenance.resetTagTables();
		maintenance.resetExcerptTables();
		maintenance.resetOutlineTables();

		return "redirect:/";
	}

}
