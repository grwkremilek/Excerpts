package com.excerpts.springboot.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.excerpts.springboot.dao.DAO;
import com.excerpts.springboot.dao.ParameterDAO;
import com.excerpts.springboot.domain.Author;
import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.helperclass.ExcerptHelperClass;
import com.excerpts.springboot.helperclass.TagHelperClass;
import com.excerpts.springboot.validators.AuthorValidator;
import com.excerpts.springboot.validators.ExcerptValidator;
import com.excerpts.springboot.validators.TagValidator;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class ExcerptController {

	@Autowired
	@Qualifier("excerptDAO")
	private ParameterDAO<Excerpt> excerptDAO;
	
	@Autowired
	@Qualifier("tagDAO")
	private ParameterDAO<Tag> tagDAO;

	@Autowired
	@Qualifier("authorDAO")
	private DAO<Author> authorDAO;

	@Autowired
	private ExcerptValidator excerptValidator;

	@Autowired
	private AuthorValidator authorValidator;

	@Autowired
	private TagValidator tagValidator;

	// display a form creating a new excerpt
	@RequestMapping(value = "/createExcerpt")
	public String displayExcerptForm(Model model) {

		model.addAttribute("excerpt", new Excerpt());
		model.addAttribute("author", new Author());
		model.addAttribute("tag", new Tag());

		return "excerpt/newExcerptForm";
	}

	// save a new or edited excerpt
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveExcerpt(@RequestParam(name = "excerptID", defaultValue = "0") Integer excerptID,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult excerptResult,
			@ModelAttribute("author") Author author, BindingResult authorResult, @ModelAttribute("tag") Tag tag,
			BindingResult tagResult, Model model) {

		excerptValidator.validate(excerpt, excerptResult);
		authorValidator.validate(author, authorResult);
		tagValidator.validate(tag, tagResult);

		if (excerptResult.hasErrors() || authorResult.hasErrors() || tagResult.hasErrors()) {
			return "excerpt/newExcerptForm";
		}

		String name = author.getName();
		String title = excerpt.getTitle();
		String text = excerpt.getText();
		String comments = excerpt.getComments();
		String description = tag.getDescription();

		excerptDAO.save(excerptID, title, text, comments);
		authorDAO.save(excerptID, name);
		tagDAO.save(excerptID, description);

		model.addAttribute("name", name);
		model.addAttribute("title", title);
		model.addAttribute("text", text);
		model.addAttribute("comments", comments);
		model.addAttribute("description", description);

		return "excerpt/excerptConfirmation";
	}

	// display all excerpts
	@RequestMapping("/getAllExcerpts")
	public String getAllExcerpts(Model model) {

		List<Excerpt> excerpts = excerptDAO.getAll();
		List<Author> authors = authorDAO.getAll();
		List<Tag> tags = tagDAO.getAll();
		int count = excerpts.size();

		List<String> descriptions = TagHelperClass.concatenateTags(tags);
		List<String> names = ExcerptHelperClass.extractNames(authors);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("authors", names);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "excerpt/allExcerpts";
	}

	// retrieve all excerpts from the specified book
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=title")
	public String processTitle(@RequestParam(name = "title") String title, @ModelAttribute("excerpt") Excerpt excerpt,
			BindingResult result, @ModelAttribute("author") Author author, BindingResult authorResult,
			@ModelAttribute("tag") Tag tag, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> rawExcerpts = excerptDAO.getByTitle(title);
		List<Author> names = authorDAO.getByTitle(title);
		List<Tag> tags = tagDAO.getByTitle(title);
		int count = rawExcerpts.size();

		List<String> descriptions = TagHelperClass.concatenateTags(tags);

		// replace empty comments with a message
		List<Excerpt> excerpts = ExcerptHelperClass.replaceEmptyCommentsExcerpts(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("authors", names);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "excerpt/excerptsByTitle";
	}

	// retrieve all excerpts by the specified author
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=name")
	public String processAuthor(@RequestParam(name = "name") String name,
			@ModelAttribute("author") Author author, BindingResult result, @ModelAttribute("tag") Tag tag,
			Model model) {

		authorValidator.validate(author, result);

		if (result.hasErrors()) {
			return "index";
		}

		// get excerpts of the authors
		List<Excerpt> rawExcerpts = excerptDAO.getByAuthor(name);
		List<Tag> tags = tagDAO.getByAuthor(name);
		int count = rawExcerpts.size();

		List<String> descriptions = TagHelperClass.concatenateTags(tags);

		// replace empty comments with a message
		List<Excerpt> excerpts = ExcerptHelperClass.replaceEmptyCommentsExcerpts(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "excerpt/excerptsByAuthor";
	}

	// retrieve all excerpts with the specified tag
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=tag")
	public String processTag(@RequestParam(name = "description") String description,
			@ModelAttribute("excerpt") Excerpt excerpt, @ModelAttribute("tag") Tag tag, BindingResult result,
			Model model) {

		tagValidator.validate(tag, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> rawExcerpts = excerptDAO.getByTag(description);
		List<Tag> tags = tagDAO.getByTag(description);
		int count = rawExcerpts.size();

		List<String> descriptions = TagHelperClass.concatenateTags(tags);

		// replace empty comments with a message
		List<Excerpt> excerpts = ExcerptHelperClass.replaceEmptyCommentsExcerpts(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "excerpt/excerptsByTag";
	}

	// retrieve an excerpt with the specified excerpt ID
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=excerptID")
	public String processExcerptID(@RequestParam(name = "excerptID") Integer excerptID, @ModelAttribute("tag") Tag tag,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> rawExcerpts = excerptDAO.getByID(excerptID);
		List<Tag> tags = tagDAO.getByID(excerptID);

		List<String> descriptions = TagHelperClass.concatenateTags(tags);

		// replace empty comments with a message
		List<Excerpt> excerpts = ExcerptHelperClass.replaceEmptyCommentsExcerpts(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);

		return "excerpt/excerptByID";
	}

	// display a comment on a separate page
	@RequestMapping(value = "/displayComments/{comments}")
	public String getComments(@PathVariable("comments") String comments, Model model) {

		model.addAttribute("comments", comments);
		return "excerpt/comment";
	}

	// delete a specified excerpt
	@RequestMapping(value = "/delete/{excerptID}/{author}/{title}/{tag}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("excerptID") int excerptID,
			@PathVariable("author") String author, @PathVariable("title") String title, @PathVariable("tag") String tag,
			Model model) {

		excerptDAO.delete(excerptID);
		tagDAO.delete(excerptID);

		// disambiguation to redirect to an excerpt with the given parameter
		if (!author.equals("author") && title.equals("title") && tag.equals("tag")) {

			redirectAttributes.addAttribute("author", author);
			redirectAttributes.addAttribute("parameter", "author");

		} else if (!title.equals("title") && author.equals("author") && tag.equals("tag")) {

			redirectAttributes.addAttribute("title", title);
			redirectAttributes.addAttribute("parameter", "title");

		} else if (!tag.equals("tag") && author.equals("author") && title.equals("title")) {

			redirectAttributes.addAttribute("description", tag);
			redirectAttributes.addAttribute("parameter", "tag");

		} else {

			redirectAttributes.addAttribute("excerptID", excerptID);
			redirectAttributes.addAttribute("parameter", "excerptID");
		}

		return "redirect:/excerpt/getByParameter";
	}

	// displays a form for existing tag edits
	@RequestMapping(value = "/edit/{excerptID}/{author}/{title}/{text}/{comments}/{description}")
	public String displayEdit(Excerpt excerpt, Tag tag, Model model) {

		Excerpt decodedExcerpt = ExcerptHelperClass.decode(excerpt);

		model.addAttribute("excerpt", decodedExcerpt);
		model.addAttribute("tag", tag);

		return "excerpt/editExcerptForm";
	}

	// displays all tags present in the database
	@RequestMapping(value = "/getAllTags", method = RequestMethod.POST)
	public String getAllTags(Model model) throws JsonGenerationException, JsonMappingException, IOException {

		List<Tag> rawTags = tagDAO.getAll();
		int count = tagDAO.countAll();

		List<Map<String, Object>> data = TagHelperClass.createAnyChartData(rawTags);
		Set<String> tags = TagHelperClass.organizeTags(rawTags);

		model.addAttribute("data", data);
		model.addAttribute("count", count);
		model.addAttribute("tags", tags);

		return "tags";
	}
}
