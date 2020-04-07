package de.brightside.bnotes.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.brightside.bnotes.model.Chapter;

public class DocumentLogic {
	
	public void nextIndex(Map<Integer, Integer> indexPerChapter, int level) {
		Integer index = indexPerChapter.get(level);
		if (index == null) {
			index = 0;
		}
		index ++;
		indexPerChapter.put(level, index);
		
		Set<Integer> keys = new TreeSet<>(indexPerChapter.keySet());
		for (Integer i: keys) {
			if (i > level) {
				indexPerChapter.remove(i);
			}
		}
		
	}

	public String getIndexLabel(Map<Integer, Integer> indexPerChapter, int level) {
		String result = "";
		for (int i = 1; i <= level; i++) {
			Integer index = indexPerChapter.get(i);
			if (index == null) {
				index = 1;
			}
			if (!result.isEmpty()) {
				result += ".";
			}
			result += "" + index;
		}
		
		if (level == 1) {
			result += ".";
		}
		
		return result;
	}

	
	private Integer findIndex(List<Chapter> chapters, long chapterId) {
		int index = 0;
		for (Chapter i: chapters) {
			if (i.getChapterId() == chapterId) {
				return index;
			}
			index ++;
		}
		return null;
	}

	public Long getIdOfNextChapter(List<Chapter> chapters, long chapterId, int level) {
		Integer index = findIndex(chapters, chapterId);
		if (index == null) {
			return null;
		}
		
		for (int i = index + 1; i < chapters.size(); i++) {
			if (chapters.get(i).getLevel() == level) {
				return chapters.get(i).getChapterId();
			}
		}
		
		return null;
	}

	public Long getIdOfPrevChapter(List<Chapter> chapters, long chapterId, int level) {
		Integer index = findIndex(chapters, chapterId);
		if (index == null) {
			return null;
		}
		
		for (int i = index - 1; i >= 0; i--) {
			if (chapters.get(i).getLevel() == level) {
				return chapters.get(i).getChapterId();
			}
		}
		
		return null;
	}
	
	
}
