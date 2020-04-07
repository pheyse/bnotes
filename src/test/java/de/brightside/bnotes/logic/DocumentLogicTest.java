package de.brightside.bnotes.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import de.brightside.bnotes.model.Chapter;

public class DocumentLogicTest {
	@Test
	public void nextIndex_start() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		
		assertEquals(1, indexPerChapter.size());
		assertEquals(1, indexPerChapter.get(1));
	}

	@Test
	public void nextIndex_startLevel2() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 2);
		
		assertEquals(1, indexPerChapter.size());
		assertEquals(1, indexPerChapter.get(2));
	}
	
	@Test
	public void nextIndex_threeLevelsOne() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 3);
		
		assertEquals(3, indexPerChapter.size());
		assertEquals(1, indexPerChapter.get(1));
		assertEquals(1, indexPerChapter.get(2));
		assertEquals(1, indexPerChapter.get(3));
	}

	@Test
	public void nextIndex_addLevelTwo() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 3);
		logic.nextIndex(indexPerChapter, 2);
		
		assertEquals(2, indexPerChapter.size());
		assertEquals(1, indexPerChapter.get(1));
		assertEquals(2, indexPerChapter.get(2));
	}

	@Test
	public void nextIndex_level2WithReset() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 1);
		
		assertEquals(1, indexPerChapter.size());
		assertEquals(2, indexPerChapter.get(1));
	}

	@Test
	public void nextIndex_level2AfterReset() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		
		assertEquals(2, indexPerChapter.size());
		assertEquals(2, indexPerChapter.get(1));
		assertEquals(1, indexPerChapter.get(2));
	}
	
	
	@Test
	public void getIndexLabel_empty() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		String result = logic.getIndexLabel(indexPerChapter, 1);
	
		assertEquals("1.", result);
	}

	@Test
	public void getIndexLabel_secondIndexInLevel2() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 3);
		logic.nextIndex(indexPerChapter, 2);

		String result = logic.getIndexLabel(indexPerChapter, 2);
		
		assertEquals("1.2", result);
	}

	@Test
	public void getIndexLabel_forLevel3SecondIndexInLevel2() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 3);
		logic.nextIndex(indexPerChapter, 2);
		
		String result = logic.getIndexLabel(indexPerChapter, 3);
		
		assertEquals("1.2.1", result);
	}

	@Test
	public void getIndexLabel_forLevel2AfterReset() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		
		String result = logic.getIndexLabel(indexPerChapter, 2);
		
		assertEquals("2.1", result);
	}
	
	@Test
	public void getIndexLabel_forLevel1SecondIndexInLevel2() {
		DocumentLogic logic = new DocumentLogic();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		logic.nextIndex(indexPerChapter, 1);
		logic.nextIndex(indexPerChapter, 2);
		logic.nextIndex(indexPerChapter, 3);
		logic.nextIndex(indexPerChapter, 2);
		
		String result = logic.getIndexLabel(indexPerChapter, 1);
		
		assertEquals("1.", result);
	}

	private List<Chapter> createDummyChapters() {
		List<Chapter> result = new ArrayList<Chapter>();
		
		result.add(createDummyChapter(1, 1, "One", 1));
		result.add(createDummyChapter(2, 2, "One-a", 2));
		result.add(createDummyChapter(3, 3, "One-b", 2));
		result.add(createDummyChapter(4, 4, "One-c", 2));
		result.add(createDummyChapter(5, 5, "Two", 1));
		result.add(createDummyChapter(6, 6, "Two-a", 2));
		result.add(createDummyChapter(7, 7, "Two-b", 2));
		result.add(createDummyChapter(8, 8, "Three", 1));
		
		return result;
	}

	private Chapter createDummyChapter(long id, long orderSequence, String title, int level) {
		Chapter result = new Chapter();
		result.setActive(1);
		result.setTitle(title);
		result.setChapterId(id);
		result.setDocumentId(1);
		result.setOrderSequence(orderSequence);
		result.setLevel(level);
		result.setBody("...");
		return result;
	}

	@Test
	public void getIdOfNextChapter_normalCase() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		long result = logic.getIdOfNextChapter(chapters, 4, 2);
		
		assertEquals(6, result);
	}

	@Test
	public void getIdOfNextChapter_missing() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		Long result = logic.getIdOfNextChapter(chapters, 99, 2);
		
		assertEquals(null, result);
	}
	
	@Test
	public void getIdOfNextChapter_beginning() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		long result = logic.getIdOfNextChapter(chapters, 2, 2);
		
		assertEquals(3, result);
	}
	
	@Test
	public void getIdOfNextChapter_ending() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		Long result = logic.getIdOfNextChapter(chapters, 7, 2);
		
		assertEquals(null, result);
	}
	
	@Test
	public void getIdOfNextChapter_empty() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = new ArrayList<Chapter>();
		Long result = logic.getIdOfNextChapter(chapters, 7, 2);
		
		assertEquals(null, result);
	}
	
	@Test
	public void getIdOfPrevChapter_normalCase() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		long result = logic.getIdOfPrevChapter(chapters, 4, 2);
		
		assertEquals(3, result);
	}

	@Test
	public void getIdOfPrevChapter_missing() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		Long result = logic.getIdOfPrevChapter(chapters, 99, 2);
		
		assertEquals(null, result);
	}
	
	@Test
	public void getIdOfPrevChapter_beginning() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		Long result = logic.getIdOfPrevChapter(chapters, 2, 2);
		
		assertEquals(null, result);
	}
	
	@Test
	public void getIdOfPrevChapter_ending() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = createDummyChapters();
		long result = logic.getIdOfPrevChapter(chapters, 7, 2);
		
		assertEquals(6, result);
	}
	
	@Test
	public void getIdOfPrevChapter_empty() {
		DocumentLogic logic = new DocumentLogic();
		
		List<Chapter> chapters = new ArrayList<Chapter>();
		Long result = logic.getIdOfPrevChapter(chapters, 7, 2);
		
		assertEquals(null, result);
	}

	
}
