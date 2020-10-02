package com.excerpts.springboot.helperclass;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.excerpts.springboot.domain.Excerpt;
import com.excerpts.springboot.domain.Outline;
import com.excerpts.springboot.domain.Tag;

public class HelperClass {

	public static List<Excerpt> replaceEmptyCommentsExcerpts(List<Excerpt> excerpts) {

		for (Excerpt excerpt : excerpts) {
			String comments = excerpt.getComments();
			if (comments.isEmpty()) {
				excerpt.setComments("No comment yet");
			}
		}
		return excerpts;
	}
	
	public static List<Outline> replaceEmptyCommentsOutline(List<Outline> outlines) {

		for (Outline outline : outlines) {
			String comments = outline.getComments();
			if (comments.isEmpty()) {
				outline.setComments("No comment yet");
			}
		}
		return outlines;
	}
	
	

	// extract descriptions from tags and concatenate descriptions belonging to one
	// excerpt in a string
	public static List<String> concatenateTags(List<Tag> tags) {

		return new ArrayList<>(tags.stream().collect(Collectors.groupingBy(Tag::getExcerptID,
				Collectors.mapping(Tag::getDescription, Collectors.joining(";")))).values());
	}

	// extract the descriptions from the list of tags, remove duplicates and sort
	// alphabetically
	public static Set<String> organizeTags(List<Tag> rawTags) {

		return rawTags.stream().map(Tag::getDescription)
				.collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));
	}

	// create a map for tag description frequencies
	// create a list of maps required by Anychart to create a word cloud
	public static List<Map<String, Object>> createAnyChartData(List<Tag> rawTags) {

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

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {

			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("x", entry.getKey());
			mp.put("value", entry.getValue());
			data.add(mp);
		}

		return data;
	}

	// decode encoded comments and text before passing to editForm.html
	public static Excerpt decode(Excerpt excerpt) {

		try {

			String decodedComments = java.net.URLDecoder.decode(excerpt.getComments(), StandardCharsets.UTF_8.name());
			excerpt.setComments(decodedComments);

			String decodedText = java.net.URLDecoder.decode(excerpt.getText(), StandardCharsets.UTF_8.name());
			excerpt.setText(decodedText);

		} catch (UnsupportedEncodingException e) {
		}

		return excerpt;
	}

}
