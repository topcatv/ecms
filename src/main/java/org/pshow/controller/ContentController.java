/**
 * 
 */
package org.pshow.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.UploadAdaptor;
import org.pshow.domain.File;
import org.pshow.domain.TreeItem;
import org.pshow.domain.Version;
import org.pshow.mvc.Result;
import org.pshow.mvc.SuccessResult;
import org.pshow.service.ContentService;

/**
 * @author topcat
 *
 */
@IocBean
@At("/content")
public class ContentController {
	@Inject
	private ContentService contentService;

	@At("/tree")
	public Result getChildrenContentForTree(String parent, HttpSession session)
			throws RepositoryException {
		ArrayList<TreeItem> treeItems = contentService.getChildrenForTree(
				parent, session);
		SuccessResult success = new SuccessResult();
		success.put("folders", treeItems);
		return success;
	}

	@At("/children")
	public Result getChildrenContent(String parent, HttpSession session)
			throws RepositoryException {
		ArrayList<File> items = contentService.getChildrenContent(parent,
				session, NodeType.NT_HIERARCHY_NODE);
		SuccessResult success = new SuccessResult();
		success.put("children", items);
		return success;
	}

	@At("/folder")
	@POST
	public Result createFolder(String parent, String name, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		contentService.createFolder(parent, name, session);
		return new SuccessResult();
	}

	@At("/folder")
	@PUT
	public Result updateFolder(String id, String name, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		contentService.updateFolder(id, name, session);
		return new SuccessResult();
	}

	@At("/file")
	@POST
	@AdaptBy(type = UploadAdaptor.class, args = { "c:/temp" })
	public Result createOrUpdateFile(String parent, String id, String fileName,
			@Param("file") java.io.File file, HttpSession session)
			throws ItemNotFoundException, RepositoryException, IOException {
		if (StringUtils.isNotBlank(id)) {
			contentService.updateFile(id, fileName, file, session);
		} else {
			contentService.createFile(parent, fileName, file, session);
		}
		return new SuccessResult();
	}

	@At("/delete")
	@PUT
	public Result deleteContent(String[] ids, HttpSession session)
			throws ItemNotFoundException, RepositoryException, IOException {
		contentService.deteleContent(ids, session);
		return new SuccessResult();
	}

	@At("/history")
	@GET
	public Result getHistory(String id, HttpSession session)
			throws ItemNotFoundException, RepositoryException, IOException {
		List<Version> history = contentService.getHistory(id, session);
		SuccessResult success = new SuccessResult();
		success.put("history", history);
		return success;
	}

	@At("/version")
	@GET
	public Result getVersion(String id, String versionName, HttpSession session)
			throws ItemNotFoundException, RepositoryException, IOException {
		File f = contentService.getVersion(id, versionName, session);
		SuccessResult success = new SuccessResult();
		success.put("file", f);
		return success;
	}

	@At("/full_text")
	@GET
	public Result fullText(String parent, String keywords, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		ArrayList<File> items = contentService.fullText(parent, keywords,
				session);
		SuccessResult success = new SuccessResult();
		success.put("children", items);
		return success;
	}

	@At("/search")
	@GET
	public Result search(@Param("..") SearchParameter param, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		ArrayList<File> items = contentService.search(param, session);
		SuccessResult success = new SuccessResult();
		success.put("children", items);
		return success;
	}

	@At("/restore")
	@POST
	public Result restore(String id, String versionName, HttpSession session)
			throws ItemNotFoundException, RepositoryException, IOException {
		contentService.restore(id, versionName, session);
		SuccessResult success = new SuccessResult();
		return success;
	}

	@At("/stream")
	@GET
	@Ok("raw:stream")
	public InputStream getStream(String id, HttpSession session,
			HttpServletResponse response) throws ItemNotFoundException,
			RepositoryException {
		File f = contentService.getFile(id, session);
		response.setContentType(f.getMimeType());
		response.setCharacterEncoding(f.getEncoding());
		return f.getStream();
	}

}
