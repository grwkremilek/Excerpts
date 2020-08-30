package com.excerpts.springboot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

import com.excerpts.springboot.dao.DAO;
import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.validators.ExcerptValidator;
import com.excerpts.springboot.validators.TagValidator;

@Controller
public class ExcerptController {

	@Autowired
	private DAO<Excerpt> exerptDAO;
	@Autowired
	private DAO<Tag> tagDAO;

	@Autowired
	ExcerptValidator excerptValidator;
	@Autowired
	TagValidator tagValidator;

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/")
	public String viewIndexPage(Model model) {
		model.addAttribute("excerpt", new Excerpt());
		model.addAttribute("tag", new Tag());
		return "index";
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/createExcerpt")
	public String displayExcerptForm(Model model) {
		model.addAttribute("excerpt", new Excerpt());
		model.addAttribute("tag", new Tag());
		return "newExcerptForm";
	}

	/**
	 * 
	 * @param excerptID
	 * @param excerpt
	 * @param excerptResult
	 * @param tag
	 * @param tagResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveExcerpt(@RequestParam(name = "excerptID", defaultValue = "0") Integer excerptID,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult excerptResult, @ModelAttribute("tag") Tag tag,
			BindingResult tagResult, Model model) {

		excerptValidator.validate(excerpt, excerptResult);
		tagValidator.validate(tag, tagResult);

		if (excerptResult.hasErrors() || tagResult.hasErrors()) {
			return "newExcerptForm";
		}

		String author = excerpt.getAuthor();
		String title = excerpt.getTitle();
		String text = excerpt.getText();
		String comments = excerpt.getComments();
		String description = tag.getDescription();

		exerptDAO.save(excerptID, author, title, text, comments);
		tagDAO.save(excerptID, description);

		model.addAttribute("author", author);
		model.addAttribute("title", title);
		model.addAttribute("text", text);
		model.addAttribute("comments", comments);
		model.addAttribute("description", description);
		return "confirmExcerpt";
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/getAll")
	public String getAll(Model model) {

		List<Excerpt> excerpts = exerptDAO.getAll();
		List<Tag> tags = tagDAO.getAll();
		int count = excerpts.size();

		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "getAll";
	}

	/**
	 * 
	 * @param title
	 * @param tag
	 * @param excerpt
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=title")
	public String processTitle(@RequestParam(name = "title") String title, @ModelAttribute("tag") Tag tag,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByTitle(title);
		List<Tag> tags = tagDAO.getByTitle(title);
		int count = excerpts.size();

		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);
		return "getByTitle";
	}

	/**
	 * 
	 * @param author
	 * @param tag
	 * @param excerpt
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=author")
	public String processAuthor(@RequestParam(name = "author") String author, @ModelAttribute("tag") Tag tag,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByAuthor(author);
		List<Tag> tags = tagDAO.getByAuthor(author);
		int count = excerpts.size();

		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);
		return "getByAuthor";
	}

	/**
	 * 
	 * @param description
	 * @param excerpt
	 * @param tag
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=tag")
	public String processTag(@RequestParam(name = "description") String description,
			@ModelAttribute("excerpt") Excerpt excerpt, @ModelAttribute("tag") Tag tag, BindingResult result,
			Model model) {

		tagValidator.validate(tag, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByTag(description);
		List<Tag> tags = tagDAO.getByTag(description);
		int count = excerpts.size();

		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);
		return "getByTag";
	}

	/**
	 * 
	 * @param excerptID
	 * @param tag
	 * @param excerpt
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=excerptID")
	public String processExcerptID(@RequestParam(name = "excerptID") Integer excerptID, @ModelAttribute("tag") Tag tag,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByID(excerptID);
		List<Tag> tags = tagDAO.getByID(excerptID);

		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		return "getByExcerptID";
	}

	/**
	 * 
	 * @param comments
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/displayComments/{comments}")
	public String getComments(@PathVariable("comments") String comments, Model model) {

		model.addAttribute("comments", comments);
		return "displayComments";
	}

	/**
	 * 
	 * @param redirectAttributes
	 * @param excerptID
	 * @param author
	 * @param title
	 * @param tag
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/delete/{excerptID}/{author}/{title}/{tag}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("excerptID") int excerptID,
			@PathVariable("author") String author, @PathVariable("title") String title, @PathVariable("tag") String tag,
			Model model) {

		exerptDAO.delete(excerptID);
		tagDAO.delete(excerptID);

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

		return "redirect:/getByParameter";
	}

	/**
	 * 
	 * @param excerpt
	 * @param tag
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/edit/{excerptID}/{author}/{title}/{text}/{comments}/{description}")
	public String displayEdit(Excerpt excerpt, Tag tag, Model model) {

		model.addAttribute("excerpt", excerpt);
		model.addAttribute("tag", tag);
		return "editExcerptForm";
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/truncateTables")
	public String truncateTables() {

		tagDAO.resetTables();
		exerptDAO.resetTables();
		return "redirect:/";
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getAllTags", method = RequestMethod.POST)
	public String getAllTags(Model model) {

		List<Tag> rawTags = tagDAO.getAll();
		int count = tagDAO.countAll();

		Set<String> tags = rawTags.stream().map(Tag::getDescription)
				.collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));
		model.addAttribute("tags", tags);
		model.addAttribute("count", count);
		return "tags";
	}
}
