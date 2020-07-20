package com.excerpts.springboot;

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
	 * display the home page
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
	 * display a new excerpt form
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
	 * save a new or edited excerpt
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
		tagValidator.validate(excerpt, tagResult);

		if (excerptResult.hasErrors() || tagResult.hasErrors()) {
			return "newExcerptForm";
		}

		String author = excerpt.getAuthor();
		String title = excerpt.getTitle();
		String text = excerpt.getText();
		String comments = excerpt.getComments();
		String description = tag.getDescription();

		int createdExcerptID = exerptDAO.save(excerptID, author, title, text, comments);
		tagDAO.save(createdExcerptID, description);

		model.addAttribute("author", author);
		model.addAttribute("title", title);
		model.addAttribute("text", text);
		model.addAttribute("comments", comments);
		model.addAttribute("description", description);
		return "confirmExcerpt";
	}

	/**
	 * display all excerpts (with their tags)
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/getAll")
	public String getAll(Model model) {

		List<Excerpt> excerpts = exerptDAO.getAll();
		List<Tag> tags = tagDAO.getAll();

		int count = excerpts.size();
		model.addAttribute("excerpts", excerpts);
		model.addAttribute("tags", tags);
		model.addAttribute("count", count);

		return "getAll";
	}

	/**
	 * display excerpts from a book with title
	 * 
	 * @param title
	 * @param excerpt
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.GET,
			RequestMethod.POST }, params = "parameter=title")
	public String processTitle(@RequestParam(name = "title") String title, @ModelAttribute("excerpt") Excerpt excerpt,
			BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByTitle(title);
		List<Tag> tags = tagDAO.getByTitle(title);
		int count = excerpts.size();

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("tags", tags);
		model.addAttribute("count", count);
		return "getByTitle";
	}

	/**
	 * display excerpts by an author
	 * 
	 * @param author
	 * @param excerpt
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.GET,
			RequestMethod.POST }, params = "parameter=author")
	public String processAuthor(@RequestParam(name = "author") String author,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByAuthor(author);
		List<Tag> tags = tagDAO.getByAuthor(author);
		int count = excerpts.size();

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("tags", tags);
		model.addAttribute("count", count);
		return "getByAuthor";
	}

	/**
	 * display excerpts with a tag
	 * 
	 * @param description
	 * @param tag
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.GET,
			RequestMethod.POST }, params = "parameter=tag")
	public String processTag(@RequestParam(name = "description") String description, @ModelAttribute("tag") Tag tag,
			BindingResult result, Model model) {

		tagValidator.validate(tag, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByTag(description);
		List<Tag> tags = tagDAO.getByTag(description);
		int count = excerpts.size();

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("tags", tags);
		model.addAttribute("count", count);
		return "getByTag";
	}

	/**
	 * get an excerpt by its excerptID
	 * 
	 * @param excerptID
	 * @param excerpt
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.GET,
			RequestMethod.POST }, params = "parameter=excerptID")
	public String processExcerptID(@RequestParam(name = "excerptID") Integer excerptID,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> excerpts = exerptDAO.getByID(excerptID);
		List<Tag> tags = tagDAO.getByID(excerptID);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("tags", tags);
		return "getByExcerptID";
	}

	/**
	 * display comments on a separate page
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
	 * delete an excerpt
	 * 
	 * @param redirectAttributes
	 * @param excerptID
	 * @param author
	 * @param title
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/delete/{excerptID}/{author}/{title}/{tag}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("excerptID") int excerptID,
			@PathVariable("author") String author, @PathVariable("title") String title, @PathVariable("tag") String tag,
			Model model) {

		int tagID = exerptDAO.delete(excerptID);
		tagDAO.delete(tagID);

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
	 * display edit form
	 * 
	 * @param excerptID
	 * @param author
	 * @param title
	 * @param text
	 * @param comments
	 * @param tags
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
	 * delete all entries in the tables in the database and reset auto-increment
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
	 * show all tags
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getAllTags", method = RequestMethod.POST)
	public String getAllTags(Model model) {
		
		List<Tag> tags = tagDAO.getAll();
		int count = tagDAO.countAll();
		model.addAttribute("tags", tags);
		model.addAttribute("count", count);
		return "tags";
	}
}
