package com.excerpts.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.excerpts.springboot.dao.DAO;
import com.excerpts.springboot.domain.Outline;
import com.excerpts.springboot.domain.Tag;
import com.excerpts.springboot.helperclass.OutlineHelperClass;
import com.excerpts.springboot.helperclass.TagHelperClass;
import com.excerpts.springboot.validators.OutlineValidator;
import com.excerpts.springboot.validators.TagValidator;

@Controller
public class OutlineController {
	
	@Autowired
	@Qualifier("outlineDAO")
	private DAO<Outline> outlineDAO;
	
	@Autowired
	@Qualifier("tagDAO")
	private DAO<Tag> tagDAO;

	@Autowired
	private OutlineValidator outlineValidator;
	
	@Autowired
	private TagValidator tagValidator;
	
	
	// display a form creating a new outline
		@RequestMapping(value = "/createOutline")
		public String displayOutlineForm(Model model) {

			model.addAttribute("outline", new Outline());
			model.addAttribute("tag", new Tag());

			return "outline/newOutlineForm";
		}
		
		// save a new or edited outline
		@RequestMapping(value = "/saveOutline", method = RequestMethod.POST)
		public String saveExcerpt(@RequestParam(name = "outlineID", defaultValue = "0") Integer outlineID,
				@ModelAttribute("outline") Outline outline, BindingResult outlineResult, @ModelAttribute("tag") Tag tag,
				BindingResult tagResult, Model model) {

			outlineValidator.validate(outline, outlineResult);
			tagValidator.validate(tag, tagResult);

			if (outlineResult.hasErrors() || tagResult.hasErrors()) {
				return "outline/newOutlineForm";
			}

			String plot = outline.getPlot();
			String comments = outline.getComments();
			String description = tag.getDescription();

			outlineDAO.save(outlineID, plot, comments);
			tagDAO.save(outlineID, description);

			model.addAttribute("plot", plot);
			model.addAttribute("comments", comments);
			model.addAttribute("description", description);

			return "outline/outlineConfirmation";
		}
		
		// display all outlines
		@RequestMapping("/getAllOutlines")
		public String getAllOutlines(Model model) {

			List<Outline> outlines = outlineDAO.getAll();
			List<Tag> tags = tagDAO.getAll();
			int count = outlines.size();

			List<String> descriptions = TagHelperClass.concatenateTags(tags);

			model.addAttribute("outlines", outlines);
			model.addAttribute("descriptions", descriptions);
			model.addAttribute("count", count);

			return "outline/allOutlines";
		}
		
		// retrieve all outlines with the specified tag
		@RequestMapping(value = "/getByTag", method = { RequestMethod.POST,
				RequestMethod.GET })
		public String processTag(@RequestParam(name = "description") String description,
				@ModelAttribute("outline") Outline outline, @ModelAttribute("tag") Tag tag, BindingResult result,
				Model model) {

			tagValidator.validate(tag, result);

			if (result.hasErrors()) {
				return "index";
			}

			List<Outline> rawOutlines = outlineDAO.getByTag(description);
			List<Tag> tags = tagDAO.getByTag(description);
			int count = rawOutlines.size();

			List<String> descriptions = TagHelperClass.concatenateTags(tags);

			// replace empty comments with a message
			List<Outline> outlines = OutlineHelperClass.replaceEmptyCommentsOutline(rawOutlines);

			model.addAttribute("outlines", outlines);
			model.addAttribute("descriptions", descriptions);
			model.addAttribute("count", count);

			return "outlines/outlinesByTag";
		}


}
