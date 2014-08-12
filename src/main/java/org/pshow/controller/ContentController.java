/**
 * 
 */
package org.pshow.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pshow.domain.File;
import org.pshow.domain.TreeItem;
import org.pshow.domain.Version;
import org.pshow.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author topcat
 *
 */
@Controller
@RequestMapping("/content")
public class ContentController {
	@Autowired
	private ContentService contentService;

	@RequestMapping("/tree")
	public ModelMap getChildrenContentForTree(String parent)
			throws RepositoryException {
		ArrayList<TreeItem> treeItems = contentService
				.getChildrenForTree(parent);
		return new ModelMap("folders", treeItems);
	}

	@RequestMapping("/children")
	public ModelMap getChildrenContent(String parent)
			throws RepositoryException {
		ArrayList<File> items = contentService.getChildrenContent(parent,
				NodeType.NT_HIERARCHY_NODE);
		return new ModelMap("children", items);
	}

	@RequestMapping(value = "/folder", method = RequestMethod.POST)
	public ModelMap createFolder(String parent, String name)
			throws ItemNotFoundException, RepositoryException {
		contentService.createFolder(parent, name);
		return new ModelMap("success", true);
	}

	@RequestMapping(value = "/folder", method = RequestMethod.PUT)
	public ModelMap updateFolder(String id, String name)
			throws ItemNotFoundException, RepositoryException {
		contentService.updateFolder(id, name);
		return new ModelMap("success", true);
	}

	@RequestMapping(value = "/file", method = RequestMethod.POST)
	public ModelMap createOrUpdateFile(String parent, String id,
			String fileName, @RequestParam MultipartFile file, HttpSession session)
			throws ItemNotFoundException, RepositoryException, IOException {
		java.io.File temp = null;
		if (!file.isEmpty()) {
			temp = java.io.File.createTempFile("ecm_upload", "."
					+ FilenameUtils.getExtension(file.getOriginalFilename()));
			file.transferTo(temp);
		}
		if (StringUtils.isNotBlank(id)) {
			contentService.updateFile(id, fileName, temp, session);
		} else {
			contentService.createFile(parent, fileName, temp, session);
		}
		return new ModelMap("success", true);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.PUT)
	public void deleteContent(String[] ids) throws ItemNotFoundException,
			RepositoryException, IOException {
		contentService.deteleContent(ids);
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public Map<String, List<Version>> getHistory(String id)
			throws ItemNotFoundException, RepositoryException, IOException {
		List<Version> history = contentService.getHistory(id);
		Map<String, List<Version>> resutl = new HashMap<String, List<Version>>();
		resutl.put("history", history);
		return resutl;
	}

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public Map<String, File> getVersion(String id, String versionName)
			throws ItemNotFoundException, RepositoryException, IOException {
		File f = contentService.getVersion(id, versionName);
		Map<String, File> resutl = new HashMap<String, File>();
		resutl.put("file", f);
		return resutl;
	}

	@RequestMapping(value = "/full_text", method = RequestMethod.GET)
	public Map<String, List<File>> fullText(String parent, String keywords)
			throws ItemNotFoundException, RepositoryException {
		ArrayList<File> items = contentService.fullText(parent, keywords);
		Map<String, List<File>> resutl = new HashMap<String, List<File>>();
		resutl.put("children", items);
		return resutl;
	}

	@RequestMapping(value = "/search")
	public ModelMap search(SearchParameter param) throws ItemNotFoundException,
			RepositoryException {
		ArrayList<File> items = contentService.search(param);
		return new ModelMap("children", items);
	}

	@RequestMapping(value = "/restore", method = RequestMethod.POST)
	public void restore(String id, String versionName)
			throws ItemNotFoundException, RepositoryException, IOException {
		contentService.restore(id, versionName);
	}

	@RequestMapping(value = "/stream")
	public void getStream(String id, boolean isCopy,
			HttpServletResponse response) throws ItemNotFoundException,
			RepositoryException, IOException {
		if (isCopy) {
			InputStream stream = contentService.getCopy(id);
			response.setContentType("application/pdf");
			response.setCharacterEncoding("utf8");
			ServletOutputStream outputStream = response.getOutputStream();
			IOUtils.copy(stream, outputStream);
		} else {
			File f = contentService.getFile(id);
			response.setContentType(f.getMimeType());
			response.setCharacterEncoding(f.getEncoding());
			String filename = f.getName()+"."+f.getSuffix();
			response.setHeader("Content-disposition", "attachment; filename="+URLEncoder.encode(filename,"utf-8"));
			ServletOutputStream outputStream = response.getOutputStream();
			IOUtils.copy(f.getStream(), outputStream);
		}
	}

}
