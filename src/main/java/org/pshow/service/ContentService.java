package org.pshow.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.value.BinaryImpl;
import org.nutz.ioc.loader.annotation.IocBean;
import org.pshow.common.JackrabbitUtils;
import org.pshow.domain.File;
import org.pshow.domain.TreeItem;

import sun.net.www.MimeTable;

@IocBean
public class ContentService {

	public void createFolder(String parent, String name, HttpSession session)
			throws ItemNotFoundException, RepositoryException,
			ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, AccessDeniedException,
			ReferentialIntegrityException, InvalidItemStateException {
		Session jcrSession = getJcrSession(session);
		Node parentNode = getNode(parent, jcrSession);

		parentNode.addNode(name, NodeType.NT_FOLDER);
		jcrSession.save();
	}

	public Node getNode(String identifier, Session jcrSession)
			throws RepositoryException, ItemNotFoundException {
		if ("root".equals(identifier)) {
			return jcrSession.getRootNode();
		} else {
			return jcrSession.getNodeByIdentifier(identifier);
		}
	}

	public ArrayList<File> getChildrenContent(String parent, HttpSession session, String nodeType)
			throws RepositoryException, ItemNotFoundException {
		Session jcrSession = getJcrSession(session);
		ArrayList<File> items = new ArrayList<File>();
		String sql = "";
		if ("root".equals(parent)) {
			sql = "select * from [%s] as c where ischildnode(c, ['/'])";
		} else {
			sql = "select * from [%s] as c where ischildnode(c, ['"
					+ jcrSession.getNodeByIdentifier(parent).getPath() + "'])";
		}
		if("*".equals(nodeType)){
			nodeType = NodeType.NT_BASE;
		}
		QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(String.format(sql, nodeType), Query.JCR_SQL2);
		QueryResult result = query.execute();
		NodeIterator nodes = result.getNodes();
		while (nodes.hasNext()) {
			Node nextNode = nodes.nextNode();
			File item = new File();
			item.setId(nextNode.getIdentifier());
			item.setName(nextNode.getName());
			boolean isFolder = nextNode.isNodeType(NodeType.NT_FOLDER);
			item.setFolder(isFolder);
			items.add(item);
		}
		return items;
	}

	public ArrayList<TreeItem> getChildrenForTree(String parent,
			HttpSession session) throws RepositoryException,
			ItemNotFoundException {
		ArrayList<File> items = getChildrenContent(parent, session, NodeType.NT_FOLDER);
		ArrayList<TreeItem> treeItems = new ArrayList<TreeItem>();
		for (File file : items) {
			TreeItem treeItem = new TreeItem();
			treeItem.setId(file.getId());
			treeItem.setText(file.getName());
			treeItem.setLeaf(!file.isFolder());
			treeItems.add(treeItem);
		}
		return treeItems;
	}

	private Session getJcrSession(HttpSession session) {
		return JackrabbitUtils.getJcrSessionFromHttpSession(session);
	}

	public void createFile(String parent, String fileName, java.io.File file,
			HttpSession session) throws ItemNotFoundException,
			RepositoryException, IOException {
		System.out.println("parent: " + parent);
		System.out.println("fileName: " + fileName);
		System.out.println(file.getName());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		MimeTable mt = MimeTable.getDefaultTable();
		String mimeType = mt.getContentTypeFor(file.getName());
		if (mimeType == null)
			mimeType = "application/octet-stream";

		Session jcrSession = getJcrSession(session);
		Node pNode = getNode(parent, jcrSession);
		Node fileNode = pNode.addNode(fileName, "ps:file");
		
		// TODO 需要定义常量
		fileNode.setProperty("ps:creator", jcrSession.getUserID());
		fileNode.setProperty("ps:lastModifier", jcrSession.getUserID());
		fileNode.setProperty("ps:size", file.length());
		
		Node resNode = fileNode.addNode("ps:content", NodeType.NT_RESOURCE);  
        resNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);  
        resNode.setProperty(JcrConstants.JCR_ENCODING, "");
        resNode.setProperty(JcrConstants.JCR_DATA, new BinaryImpl(new FileInputStream(file)));  
        resNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
        
        jcrSession.save();
	}

}
