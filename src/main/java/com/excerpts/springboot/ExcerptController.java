package com.excerpts.springboot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

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

	// display the index page
	@RequestMapping(value = "/")
	public String viewIndexPage(Model model) {
		model.addAttribute("excerpt", new Excerpt());
		model.addAttribute("tag", new Tag());
		return "index";
	}

	// display a form creating a new excerpt
	@RequestMapping(value = "/createExcerpt")
	public String displayExcerptForm(Model model) {
		model.addAttribute("excerpt", new Excerpt());
		model.addAttribute("tag", new Tag());
		return "newExcerptForm";
	}

	// save a new or edited excerpt
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

		return "excerptConfirmation";
	}

	// display all excerpts in the database
	@RequestMapping("/getAll")
	public String getAll(Model model) {

		List<Excerpt> excerpts = exerptDAO.getAll();
		List<Tag> tags = tagDAO.getAll();
		int count = excerpts.size();

		// extract descriptions from tags and concatenate descriptions belonging to one
		// excerpt in a string
		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "allExcerpts";
	}

	// retrieve all excerpts from the specified book
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=title")
	public String processTitle(@RequestParam(name = "title") String title, @ModelAttribute("tag") Tag tag,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> rawExcerpts = exerptDAO.getByTitle(title);
		List<Tag> tags = tagDAO.getByTitle(title);
		int count = rawExcerpts.size();

		// extract descriptions from tags and concatenate descriptions belonging to one
		// excerpt in a string
		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		// replace empty comments with a message
		List<Excerpt> excerpts = replaceEmptyComments(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);
		return "excerptsByTitle";
	}

	// retrieve all excerpts by the specified author
	@RequestMapping(value = "/getByParameter", method = { RequestMethod.POST,
			RequestMethod.GET }, params = "parameter=author")
	public String processAuthor(@RequestParam(name = "author") String author, @ModelAttribute("tag") Tag tag,
			@ModelAttribute("excerpt") Excerpt excerpt, BindingResult result, Model model) {

		excerptValidator.validate(excerpt, result);

		if (result.hasErrors()) {
			return "index";
		}

		List<Excerpt> rawExcerpts = exerptDAO.getByAuthor(author);
		List<Tag> tags = tagDAO.getByAuthor(author);
		int count = rawExcerpts.size();

		// extract descriptions from tags and concatenate descriptions belonging to one
		// excerpt in a string
		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		// replace empty comments with a message
		List<Excerpt> excerpts = replaceEmptyComments(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "excerptsByAuthor";
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

		List<Excerpt> rawExcerpts = exerptDAO.getByTag(description);
		List<Tag> tags = tagDAO.getByTag(description);
		int count = rawExcerpts.size();

		// extract descriptions from tags and concatenate descriptions belonging to one
		// excerpt in a string
		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		// replace empty comments with a message
		List<Excerpt> excerpts = replaceEmptyComments(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);
		model.addAttribute("count", count);

		return "excerptsByTag";
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

		List<Excerpt> rawExcerpts = exerptDAO.getByID(excerptID);
		List<Tag> tags = tagDAO.getByID(excerptID);

		// extract descriptions from tags and concatenate descriptions belonging to one
		// excerpt in a string
		List<String> descriptions = new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());

		// replace empty comments with a message
		List<Excerpt> excerpts = replaceEmptyComments(rawExcerpts);

		model.addAttribute("excerpts", excerpts);
		model.addAttribute("descriptions", descriptions);

		return "excerptByID";
	}

	// display a comment on a separate page
	@RequestMapping(value = "/displayComments/{comments}")
	public String getComments(@PathVariable("comments") String comments, Model model) {

		model.addAttribute("comments", comments);
		return "comment";
	}

	// delete a specified excerpt
	@RequestMapping(value = "/delete/{excerptID}/{author}/{title}/{tag}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("excerptID") int excerptID,
			@PathVariable("author") String author, @PathVariable("title") String title, @PathVariable("tag") String tag,
			Model model) {

		exerptDAO.delete(excerptID);
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

		return "redirect:/getByParameter";
	}

	// displays a form for existing tag edits
	@RequestMapping(value = "/edit/{excerptID}/{author}/{title}/{text}/{comments}/{description}")
	public String displayEdit(Excerpt excerpt, Tag tag, Model model) {

		model.addAttribute("excerpt", excerpt);
		model.addAttribute("tag", tag);
		return "editForm";
	}

	// truncates all tables and resets auto-increment
	@RequestMapping(value = "/truncateTables")
	public String truncateTables() {

		tagDAO.resetTables();
		exerptDAO.resetTables();
		return "redirect:/";
	}

	// displays all tags present in the database
	@RequestMapping(value = "/getAllTags", method = RequestMethod.POST)
	public String getAllTags(Model model) throws JsonGenerationException, JsonMappingException, IOException {

		List<Tag> rawTags = tagDAO.getAll();
		int count = tagDAO.countAll();
		
		//create a map for tag description frequencies
		int frequency = 1;
		List<String> tagDescriptions = rawTags.stream().map(Tag::getDescription).collect(Collectors.toList());
		Map<String, Integer> frequencyMap = new HashMap<>();
		Set<String> set1 = new LinkedHashSet<>();
		for (String s : tagDescriptions) {
		    if (!set1.add(s)) {
		    	frequency = frequencyMap.get(s) + 1;
		    }
		    frequencyMap.put(s, frequency);
		    frequency = 1;
		}
		
		//create a list of maps required by anychart to create a word cloud
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
        for (Map.Entry<String,Integer> entry : frequencyMap.entrySet()) {
        	
        	Map<String, Object> mp=new HashMap<String, Object>();
        	mp.put("x", entry.getKey());
        	mp.put("value", entry.getValue());    	
        	data.add(mp);
        }

		// extract the descriptions from the list of tags, remove duplicates and sort
		// alphabetically
		Set<String> tags = rawTags.stream().map(Tag::getDescription)
				.collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

		model.addAttribute("data", data);
		model.addAttribute("tags", tags);
		model.addAttribute("count", count);

		return "tags";
	}

	public List<Excerpt> replaceEmptyComments(List<Excerpt> excerpts) {

		for (Excerpt excerpt : excerpts) {
			String comments = excerpt.getComments();
			if (comments.isEmpty()) {
				excerpt.setComments("No comment yet");
			}
		}
		return excerpts;
	}
}
