/**
 * 
 */
package org.pshow.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.UploadAdaptor;
import org.pshow.domain.File;
import org.pshow.domain.TreeItem;
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

	@At("/file")
	@POST
	@AdaptBy(type = UploadAdaptor.class, args = { "c:/temp" })
	public Result createFile(String parent, String fileName, @Param("file") java.io.File file,
			HttpSession session) throws ItemNotFoundException, RepositoryException, IOException {
		contentService.createFile(parent, fileName, file, session);
		return new SuccessResult();
	}

}
