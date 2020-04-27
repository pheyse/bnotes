package de.brightside.bnotes.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import de.brightside.bnotes.dto.ChapterDto;
import de.brightside.bnotes.model.Chapter;
import de.brightside.bnotes.model.Document;

public class HtmlExportService {
	private static final String TEMPLATE_PATH = "classpath:html-export-template.html";
	private static final String PLACEHOLDER_TITLE = "${title}";
	private static final String PLACEHOLDER_DOCUMENT_TITLE = "${documentTitle}";
	private static final String PLACEHOLDER_EXPORT_INFO = "${exportInfo}";
	private static final String PLACEHOLDER_TOC = "${toc}";
	private static final String PLACEHOLDER_CHAPTERS = "${chapters}";
	
	public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8")) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
	
	public String createHtml(Document document, List<Chapter> chapters, List<ChapterDto> chapterDtos) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(TEMPLATE_PATH);
		
		String result = asString(resource);

		result = result.replace(PLACEHOLDER_TITLE, createDocumentTitle(document, true));
		result = result.replace(PLACEHOLDER_DOCUMENT_TITLE, createDocumentTitle(document, false));
		result = result.replace(PLACEHOLDER_EXPORT_INFO, createDateInfo());
		result = result.replace(PLACEHOLDER_TOC, createToc(chapterDtos));
		result = result.replace(PLACEHOLDER_CHAPTERS, createChapters(chapterDtos));
		
		return result;
	}

	private String createChapters(List<ChapterDto> chapters) {
		StringBuilder result = new StringBuilder();
		int index = 0;
		for (ChapterDto i: chapters) {
			String headingTag = "h" + Math.min(i.getLevel(), 6);
			result.append("<" + headingTag + " id = 'cap" + index + "'>" + escapeHtml(i.getIndexLabel() + " " + i.getTitle()) + "</" + headingTag + ">\n");
			result.append("<p class=\"chapter-view\">" + i.getBodyHtml() + "</p>\n");
			result.append("</br>\n");
			index ++;
		}
		
		return result.toString();
	}

	private String createToc(List<ChapterDto> chapters) {
		StringBuilder result = new StringBuilder();
		
		int index = 0;
		for (ChapterDto i: chapters) {
			int padding = 2 * i.getLevel();
//			result.append("<span style = 'padding-left: " + padding + "mm'>");
			result.append("<span>");
			result.append(repeat("&nbsp;", padding));
			result.append("</span>");
			
			result.append("<a href='#cap" + index + "'>");
//			result.append("&nbsp;".repeat(padding));
			result.append(escapeHtml(i.getIndexLabel()) + " " + escapeHtml(i.getTitle()));
			result.append("</a>");
//			result.append("</span>");
			result.append("<br>\n");
			index ++;
		}
		
		
		return result.toString();
	}
	
	private String repeat(String string, int amount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amount; i++) {
			sb.append(string);
		}
		return sb.toString();
	}

	public static String escapeHtml(String string) {
		return StringEscapeUtils.escapeHtml4(string);
	}

	private String createDocumentTitle(Document document, boolean includeAppName) {
		String appName = "";
		if (includeAppName) {
			appName = "Bnotes - ";
		}
		return appName + escapeHtml(document.getTitle());
	}

	private String createDateInfo() {
		return "Exported: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
}
