package com.excerpts.springboot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExcerptController {

	@Autowired
	private DAO<Excerpt> dao;

	@Autowired
	ExcerptValidator validator;

	/* display home page */
	@RequestMapping(value = "/")
	public String viewIndexPage(Model model) {
		Excerpt excerpt = new Excerpt();
		model.addAttribute("excerpt", excerpt);
		return "index";
	}

	/* display new excerpt form from index page */
	@RequestMapping(value = "/createExcerpt")
	public String displayExcerptForm(Model model) {
		model.addAttribute("excerpt", new Excerpt());
		return "newExcerptForm";
	}

	/* save an excerpt form */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveNewExcerpt(@RequestParam(name = "excerptID", defaultValue = "0") Integer excerptID,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		validator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "newExcerptForm";
		}

		String author = excerpt.getAuthor();
		String title = excerpt.getTitle();
		String text = excerpt.getText();
		String comments = excerpt.getComments();
		String tags = excerpt.getTags();

		if (excerptID > 0) {

			dao.edit(excerptID, author, title, text, comments, tags);

		} else {
			dao.save(author, title, text, comments, tags);
		}

		model.addAttribute("author", author);
		model.addAttribute("title", title);
		model.addAttribute("text", text);
		model.addAttribute("comments", comments);
		model.addAttribute("tags", tags);

		return "confirmExcerpt";

	}

	/* list excerpts by an author */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.GET,
			RequestMethod.POST }, params = "parameter=author")
	public String processAuthor(@RequestParam(name = "author") String author,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		validator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "newExcerptForm";
		}

		List<Excerpt> excerpts = new ArrayList<Excerpt>();
		excerpts = dao.getByAuthor(author);
		int counts = excerpts.size();

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("counts", counts);
		return "getByAuthor";

	}

	/* list excerpts from a book */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.GET,
			RequestMethod.POST }, params = "parameter=title")
	public String processTitle(@RequestParam(name = "title") String title, @ModelAttribute("excerpt") Excerpt excerpt,
			BindingResult result, Model model) {

		validator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "newExcerptForm";
		}

		List<Excerpt> excerpts = new ArrayList<Excerpt>();
		excerpts = dao.getByTitle(title);
		int counts = excerpts.size();

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("counts", counts);
		return "getByTitle";
	}

	/* list excerpts with a tag */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.GET,
			RequestMethod.POST }, params = "parameter=tag")
	public String processTag(@RequestParam(name = "tags") String tags, @ModelAttribute("excerpt") Excerpt excerpt,
			BindingResult result, Model model) {

		validator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "newExcerptForm";
		}

		List<Excerpt> excerpts = new ArrayList<Excerpt>();
		excerpts = dao.getByTag(tags);
		int counts = excerpts.size();

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("counts", counts);
		return "getByTag";
	}

	/* display separate comments page */
	@RequestMapping(value = "/displayComments/{comments}")
	public String getComments(@PathVariable("comments") String comments,
			Model model) {
		
		System.out.println(comments);

		model.addAttribute("comments", comments);
		return "displayComments";
	}

	/* delete an excerpt */
	@RequestMapping(value = "/delete/{excerptID}/{author}/{title}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("excerptID") int excerptID,
			@PathVariable("author") String author, @PathVariable("title") String title, Model model) {

		dao.delete(excerptID);

		if (title.equals("title")) {
			redirectAttributes.addAttribute("author", author);
			redirectAttributes.addAttribute("parameter", "author");
		} else {
			redirectAttributes.addAttribute("title", title);
			redirectAttributes.addAttribute("parameter", "title");
		}
		return "redirect:/getByParameter";
	}

	/* display excerpt page from edit button */
	@RequestMapping(value = "/edit/{excerptID}/{author}/{title}/{text}/{tags}")
	public String getEdit(@PathVariable("excerptID") int excerptID, @PathVariable("author") String author,
			@PathVariable("title") String title, @PathVariable("text") String text,
			@PathVariable("comments") String comments, @PathVariable("tags") String tags, Model model) {

		model.addAttribute("excerpt", new Excerpt(excerptID, author, title, text, comments, tags));
		return "editExcerptForm";
	}

	/* list all excerpts */
	@RequestMapping("/getAll")
	public String displayAllQuotes(Model model) {

		List<Excerpt> excerpts = dao.getAll();
		List<String> tags = new ArrayList<String>();

		int counts = excerpts.size();
		model.addAttribute("excerpts", excerpts);
		model.addAttribute("counts", counts);
		model.addAttribute("tags", tags);
		return "getAll";
	}

	/* delete all entries in the tables in the database and reset auto-increment */
	@RequestMapping(value = "/truncateTables")
	public void truncateTables() {
		dao.emptyExcerptsDb();
	}
}
