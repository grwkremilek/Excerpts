package com.excerpts.springboot.helperclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.excerpts.springboot.domain.Tag;

public class TagHelperClass {

	// extract descriptions from tags and concatenate descriptions that belong to
	// one excerpt in a string
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
}
