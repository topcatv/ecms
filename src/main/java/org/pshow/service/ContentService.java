package org.pshow.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
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
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.value.BinaryImpl;
import org.nutz.ioc.loader.annotation.IocBean;
import org.pshow.common.JackrabbitUtils;
import org.pshow.common.SimpleCharsetDetector;
import org.pshow.domain.File;
import org.pshow.domain.TreeItem;

import sun.net.www.MimeTable;

@IocBean
public class ContentService {

	private static final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

	public void createFolder(String parent, String name, HttpSession session)
			throws ItemNotFoundException, RepositoryException,
			ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, AccessDeniedException,
			ReferentialIntegrityException, InvalidItemStateException {
		Session jcrSession = getJcrSession(session);
		Node parentNode = getNode(parent, jcrSession);

		Node addNode = parentNode.addNode(name, "ps:folder");
		addNode.setProperty("ps:name", name);
		jcrSession.save();
	}

	private Node getNode(String identifier, Session jcrSession)
			throws RepositoryException, ItemNotFoundException {
		if ("root".equals(identifier)) {
			return jcrSession.getRootNode();
		} else {
			return jcrSession.getNodeByIdentifier(identifier);
		}
	}

	public ArrayList<File> getChildrenContent(String parent,
			HttpSession session, String nodeType) throws RepositoryException,
			ItemNotFoundException {
		Session jcrSession = getJcrSession(session);
		ArrayList<File> items = new ArrayList<File>();
		String sql = "";
		if ("root".equals(parent) || StringUtils.isBlank(parent)) {
			sql = "select * from [%s] as c where ischildnode(c, ['/'])";
		} else {
			sql = "select * from [%s] as c where ischildnode(c, ['"
					+ jcrSession.getNodeByIdentifier(parent).getPath() + "'])";
		}
		if ("*".equals(nodeType)) {
			nodeType = NodeType.NT_BASE;
		}
		QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(String.format(sql, nodeType),
				Query.JCR_SQL2);
		QueryResult result = query.execute();
		NodeIterator nodes = result.getNodes();
		while (nodes.hasNext()) {
			Node nextNode = nodes.nextNode();
			items.add(convertToFile(nextNode));
		}
		return items;
	}

	private File convertToFile(Node nextNode) throws RepositoryException,
			ValueFormatException, PathNotFoundException {
		File item = new File();
		item.setId(nextNode.getIdentifier());
		item.setName(nextNode.getProperty("ps:name").getString());
		boolean isFolder = nextNode.isNodeType("ps:folder");
		item.setFolder(isFolder);
		if (!isFolder) {
			item.setSize(nextNode.getProperty("ps:size").getLong());
			item.setMimeType(nextNode.getProperty(
					"ps:content/" + JcrConstants.JCR_MIMETYPE).getString());
			item.setStream(nextNode.getNode("ps:content")
					.getProperty(JcrConstants.JCR_DATA).getBinary().getStream());
			item.setEncoding(nextNode.getProperty("ps:encoding").getString());
		}
		item.setCreated(nextNode.getProperty(JcrConstants.JCR_CREATED)
				.getDate().getTime());
		item.setLastModified(nextNode
				.getProperty(JcrConstants.JCR_LASTMODIFIED).getDate()
				.getTime());
		item.setCreator(nextNode.getProperty("jcr:createdBy").getString());
		item.setLastModifiedBy(nextNode.getProperty("jcr:lastModifiedBy")
				.getString());
		return item;
	}

	public ArrayList<TreeItem> getChildrenForTree(String parent,
			HttpSession session) throws RepositoryException,
			ItemNotFoundException {
		ArrayList<File> items = getChildrenContent(parent, session, "ps:folder");
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
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		Node pNode = getNode(parent, jcrSession);
		Node fileNode = pNode.addNode(fileName, "ps:file");
		if (!fileNode.isNodeType(NodeType.MIX_VERSIONABLE)) {
			fileNode.addMixin(NodeType.MIX_VERSIONABLE);
		}

		// TODO 需要定义常量
		fileNode.setProperty("ps:size", file.length());
		fileNode.setProperty("ps:name", fileName);
		fileNode.setProperty("ps:mimeType", mimeType);

		Node resNode = fileNode.addNode("ps:content", NodeType.NT_RESOURCE);
		resNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
		if (mimeType.contains("text")) {
			String charset = SimpleCharsetDetector.detectCharset(new FileInputStream(file));
			fileNode.setProperty("ps:encoding", charset);
			resNode.setProperty(JcrConstants.JCR_ENCODING, charset);
		} else {
			fileNode.setProperty("ps:encoding", "UTF-8");
			resNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
		}
		resNode.setProperty(JcrConstants.JCR_DATA, new BinaryImpl(new FileInputStream(file)));

		jcrSession.save();
		versionManager.checkpoint(fileNode.getPath());
	}

	public void deteleContent(String[] ids, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		Session jcrSession = getJcrSession(session);
		if (ids != null && ids.length > 0) {
			for (String id : ids) {
				Node node = jcrSession.getNodeByIdentifier(id);
				node.remove();
			}
			jcrSession.save();
		}
	}

	public List<org.pshow.domain.Version> getHistory(String id,
			HttpSession session) throws ItemNotFoundException,
			RepositoryException {
		Session jcrSession = getJcrSession(session);
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		Node node = jcrSession.getNodeByIdentifier(id);
		VersionHistory versionHistory = versionManager.getVersionHistory(node
				.getPath());
		ArrayList<org.pshow.domain.Version> history = new ArrayList<org.pshow.domain.Version>();

		Version rootVersion = versionHistory.getRootVersion();
		getSuccessorVersions(versionHistory, history, rootVersion);

		return history;
	}

	private void getSuccessorVersions(VersionHistory versionHistory,
			ArrayList<org.pshow.domain.Version> history, Version rootVersion)
			throws RepositoryException, VersionException {
		Version[] successors = rootVersion.getSuccessors();
		for (Version version : successors) {
			String[] versionLabels = versionHistory.getVersionLabels(version);
			org.pshow.domain.Version v = new org.pshow.domain.Version();
			v.setName(version.getName());
			if (ArrayUtils.isNotEmpty(versionLabels)) {
				v.setLabel(versionLabels[0]);
			}
			v.setCreated(version.getCreated().getTime());
			history.add(v);
			getSuccessorVersions(versionHistory, history, version);
		}
	}

	public void updateFile(String id, String fileName, java.io.File file,
			HttpSession session) throws ItemNotFoundException,
			RepositoryException, FileNotFoundException, IOException {
		Session jcrSession = getJcrSession(session);
		Node fileNode = getNode(id, jcrSession);
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		if (!fileNode.isNodeType(NodeType.MIX_VERSIONABLE)) {
			fileNode.addMixin(NodeType.MIX_VERSIONABLE);
			jcrSession.save();
		}
		versionManager.checkout(fileNode.getPath());
		if (!fileNode.getName().equals(fileName)) {
			fileNode.setProperty("ps:name", fileName);
			String destAbsPath = fileNode.getParent().getPath() + "/"
					+ fileName;
			jcrSession.move(fileNode.getPath(),
					destAbsPath.replaceAll("//", "/"));
		}
		if (file != null && file.exists()) {
			String mimeType = mimetypesFileTypeMap.getContentType(file);
			if (mimeType == null)
				mimeType = "application/octet-stream";

			// TODO 需要定义常量
			fileNode.setProperty("ps:size", file.length());
			fileNode.setProperty("ps:mimeType", mimeType);

			Node resNode = fileNode.getNode("ps:content");
			resNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
			if (mimeType.contains("text")) {
				String charset = SimpleCharsetDetector.detectCharset(new FileInputStream(file));
				fileNode.setProperty("ps:encoding", charset);
				resNode.setProperty(JcrConstants.JCR_ENCODING, charset);
			} else {
				fileNode.setProperty("ps:encoding", "UTF-8");
				resNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
			}
			resNode.setProperty(JcrConstants.JCR_DATA, new BinaryImpl(new FileInputStream(file)));
		}
		jcrSession.save();
		versionManager.checkin(fileNode.getPath());
	}

	public void updateFolder(String id, String name, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		Session jcrSession = getJcrSession(session);
		Node folder = jcrSession.getNodeByIdentifier(id);
		folder.setProperty("ps:name", name);
		String destAbsPath = folder.getParent().getPath() + "/" + name;
		jcrSession.move(folder.getPath(), destAbsPath.replaceAll("//", "/"));
		jcrSession.save();
	}

	public ArrayList<File> fullText(String parent, String keywords,
			HttpSession session) throws ItemNotFoundException,
			RepositoryException {
		Session jcrSession = getJcrSession(session);
		ArrayList<File> items = new ArrayList<File>();
		String sql = "SELECT * FROM [nt:hierarchyNode] as t WHERE CONTAINS(t.*, '%s')";
		QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(String.format(sql, keywords),
				Query.JCR_SQL2);
		QueryResult result = query.execute();
		NodeIterator nodes = result.getNodes();
		while (nodes.hasNext()) {
			Node nextNode = nodes.nextNode();
			items.add(convertToFile(nextNode));
		}
		return items;
	}

	public void restore(String id, String versionName, HttpSession session)
			throws UnsupportedRepositoryOperationException, RepositoryException {
		Session jcrSession = getJcrSession(session);
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		Node node = jcrSession.getNodeByIdentifier(id);
		versionManager.restore(node.getPath(), versionName, true);
	}

	public InputStream getStream(String id, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		Node node = getNode(id, getJcrSession(session));
		Node resource = node.getNode("ps:content");
		return resource.getProperty(JcrConstants.JCR_DATA).getBinary()
				.getStream();
	}

	public File getFile(String id, HttpSession session)
			throws ItemNotFoundException, RepositoryException {
		Node node = getNode(id, getJcrSession(session));
		return convertToFile(node);
	}

	public File getVersion(String id, String name, HttpSession session) throws UnsupportedRepositoryOperationException, RepositoryException {
		Session jcrSession = getJcrSession(session);
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		Node node = jcrSession.getNodeByIdentifier(id);
		VersionHistory versionHistory = versionManager.getVersionHistory(node
				.getPath());
		Version version = versionHistory.getVersion(name);
		return convertToFile(version.getFrozenNode());
	}

}
