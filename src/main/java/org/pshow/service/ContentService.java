package org.pshow.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.jcr.security.Privilege;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.value.BinaryImpl;
import org.pshow.common.CopyCallback;
import org.pshow.common.CopyProcesser;
import org.pshow.common.JackrabbitUtils;
import org.pshow.common.SimpleCharsetDetector;
import org.pshow.controller.SearchParameter;
import org.pshow.domain.File;
import org.pshow.domain.TreeItem;
import org.pshow.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

	private static final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
	@Autowired
	private PermissionService permissionService;

	public void createFolder(String parent, String name)
			throws ItemNotFoundException, RepositoryException,
			ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, AccessDeniedException,
			ReferentialIntegrityException, InvalidItemStateException {
		Session jcrSession = getJcrSession();
		Node parentNode = getNode(parent, jcrSession);

		Node addNode = parentNode.addNode(name, "ps:folder");
		addNode.setProperty("ps:name", name);
		jcrSession.save();
		permissionService.authorize(addNode.getIdentifier(),
				jcrSession.getUserID(), true, Privilege.JCR_ALL);
	}

	private Node getNode(String identifier, Session jcrSession)
			throws RepositoryException, ItemNotFoundException {
		if ("root".equals(identifier)) {
			return jcrSession.getRootNode();
		} else {
			return jcrSession.getNodeByIdentifier(identifier);
		}
	}

	public ArrayList<File> getChildrenContent(String parent, String nodeType)
			throws RepositoryException, ItemNotFoundException {
		Session jcrSession = getJcrSession();
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
				.getProperty(JcrConstants.JCR_LASTMODIFIED).getDate().getTime());
		item.setCreator(nextNode.getProperty("jcr:createdBy").getString());
		item.setLastModifiedBy(nextNode.getProperty("jcr:lastModifiedBy")
				.getString());
		return item;
	}

	public ArrayList<TreeItem> getChildrenForTree(String parent)
			throws RepositoryException, ItemNotFoundException {
		ArrayList<File> items = getChildrenContent(parent, "ps:folder");
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

	private Session getJcrSession() {
		return JackrabbitUtils.getJcrSessionFromHttpSession();
	}

	public void createFile(String parent, String fileName, java.io.File file,
			HttpSession session) throws ItemNotFoundException,
			RepositoryException, IOException {
		System.out.println("parent: " + parent);
		System.out.println("fileName: " + fileName);
		System.out.println(file.getName());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		Session jcrSession = getJcrSession();
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		Node pNode = getNode(parent, jcrSession);
		Node fileNode = pNode.addNode(fileName, "ps:file");
		if (!fileNode.isNodeType(NodeType.MIX_VERSIONABLE)) {
			fileNode.addMixin(NodeType.MIX_VERSIONABLE);
		}

		String mimeType = getMimetype(file);
		fillMainNode(fileName, file, fileNode, mimeType);

		Node resNode = fileNode.addNode("ps:content", NodeType.NT_RESOURCE);
		fillResourceNode(file, fileNode, mimeType, resNode);

		jcrSession.save();
		versionManager.checkpoint(fileNode.getPath());
		if (mimeType.indexOf("ms") != -1) {
			final User user = (User) session.getAttribute("d_user");
			CopyCallback callback = new CopyCallback() {

				@Override
				public void execute(String cid, String output) {
					Session jcrSession = null;
					try {
						jcrSession = loginToJcr(user);
						Node fileNode = jcrSession.getNodeByIdentifier(cid);
						Node resNode = fileNode.addNode("ps:copy",
								NodeType.NT_RESOURCE);
						fillResourceNode(new java.io.File(output), null,
								"application/pdf", resNode);
						jcrSession.save();
						VersionManager versionManager = jcrSession
								.getWorkspace().getVersionManager();
						versionManager.checkpoint(fileNode.getPath());
					} catch (ItemNotFoundException e) {
						e.printStackTrace();
					} catch (RepositoryException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if(jcrSession != null){
							jcrSession.logout();
						}
					}

				}

				private Session loginToJcr(User user) throws LoginException,
						RepositoryException {
					Repository repository = JcrUtils.getRepository();
					Session session = repository.login(new SimpleCredentials(
							user.getName(),
							"admin".equals(user.getName()) ? "admin"
									.toCharArray() : user.getPassword()
									.toCharArray()));
					return session;
				}
			};
			new Thread(new CopyProcesser(callback, file,
					fileNode.getIdentifier())).start();
		}
		permissionService.authorize(fileNode.getIdentifier(),
				jcrSession.getUserID(), true, Privilege.JCR_ALL);
		permissionService.authorize(fileNode.getIdentifier(), "everyone",
				false, Privilege.JCR_READ);
	}

	private String getMimetype(java.io.File file) {
		String mimeType = mimetypesFileTypeMap.getContentType(file);
		if (mimeType == null)
			mimeType = "application/octet-stream";
		return mimeType;
	}

	public void deteleContent(String[] ids) throws ItemNotFoundException,
			RepositoryException {
		Session jcrSession = getJcrSession();
		if (ids != null && ids.length > 0) {
			for (String id : ids) {
				Node node = jcrSession.getNodeByIdentifier(id);
				node.remove();
			}
			jcrSession.save();
		}
	}

	public List<org.pshow.domain.Version> getHistory(String id)
			throws ItemNotFoundException, RepositoryException {
		Session jcrSession = getJcrSession();
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
		Session jcrSession = getJcrSession();
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
		String mimeType = "";
		if (file != null && file.exists()) {
			mimeType = getMimetype(file);

			fillMainNode(fileName, file, fileNode, mimeType);

			Node resNode = fileNode.getNode("ps:content");
			fillResourceNode(file, fileNode, mimeType, resNode);
		}
		jcrSession.save();
		versionManager.checkin(fileNode.getPath());
		if (file != null && file.exists() && mimeType.indexOf("ms") != -1) {
			final User user = (User) session.getAttribute("d_user");
			CopyCallback callback = new CopyCallback() {

				@Override
				public void execute(String cid, String output) {
					Session jcrSession = null;
					try {
						jcrSession = loginToJcr(user);
						Node fileNode = jcrSession.getNodeByIdentifier(cid);
						Node resNode = null;
						VersionManager versionManager = jcrSession
								.getWorkspace().getVersionManager();
						versionManager.checkout(fileNode.getPath());
						if (fileNode.hasNode("ps:copy")) {
							resNode = fileNode.getNode("ps:copy");
						} else {
							resNode = fileNode.addNode("ps:copy", NodeType.NT_RESOURCE);
						}
						fillResourceNode(new java.io.File(output), null,
								"application/pdf", resNode);
						jcrSession.save();
						versionManager.checkin(fileNode.getPath());
					} catch (ItemNotFoundException e) {
						e.printStackTrace();
					} catch (RepositoryException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if(jcrSession != null){
							jcrSession.logout();
						}
					}
				}

				private Session loginToJcr(User user) throws LoginException,
						RepositoryException {
					Repository repository = JcrUtils.getRepository();
					Session session = repository.login(new SimpleCredentials(
							user.getName(),
							"admin".equals(user.getName()) ? "admin"
									.toCharArray() : user.getPassword()
									.toCharArray()));
					return session;
				}
			};
			new Thread(new CopyProcesser(callback, file,
					fileNode.getIdentifier())).start();
		}
	}

	private void fillResourceNode(java.io.File file, Node fileNode,
			String mimeType, Node resNode) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException, IOException, FileNotFoundException {
		resNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
		if (mimeType.contains("text")) {
			SimpleCharsetDetector simpleCharsetDetector = new SimpleCharsetDetector();
			String charset = simpleCharsetDetector.guessFileEncoding(file);
			if (fileNode != null) {
				fileNode.setProperty("ps:encoding", charset);
			}
			resNode.setProperty(JcrConstants.JCR_ENCODING, charset);
		} else {
			if (fileNode != null) {
				fileNode.setProperty("ps:encoding", "UTF-8");
			}
			resNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
		}
		resNode.setProperty(JcrConstants.JCR_DATA, new BinaryImpl(
				new FileInputStream(file)));
	}

	private void fillMainNode(String fileName, java.io.File file,
			Node fileNode, String mimeType) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO 需要定义常量
		fileNode.setProperty("ps:size", file.length());
		fileNode.setProperty("ps:name", fileName);
		fileNode.setProperty("ps:mimeType", mimeType);
		fileNode.setProperty("ps:suffix", FilenameUtils.getExtension(file.getName()));
	}

	public void updateFolder(String id, String name)
			throws ItemNotFoundException, RepositoryException {
		Session jcrSession = getJcrSession();
		Node folder = jcrSession.getNodeByIdentifier(id);
		folder.setProperty("ps:name", name);
		String destAbsPath = folder.getParent().getPath() + "/" + name;
		jcrSession.move(folder.getPath(), destAbsPath.replaceAll("//", "/"));
		jcrSession.save();
	}

	public ArrayList<File> fullText(String parent, String keywords)
			throws ItemNotFoundException, RepositoryException {
		Session jcrSession = getJcrSession();
		ArrayList<File> items = new ArrayList<File>();
		String sql = "SELECT t.* FROM [nt:hierarchyNode] as t INNER JOIN [nt:resource] AS c ON ISCHILDNODE(c, t) WHERE CONTAINS(t.*, '%s') OR CONTAINS(c.*, '%s')";
		QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(
				String.format(sql, keywords, keywords), Query.JCR_SQL2);
		QueryResult result = query.execute();
		RowIterator rows = result.getRows();
		while (rows.hasNext()) {
			Node nextNode = rows.nextRow().getNode("t");
			items.add(convertToFile(nextNode));
		}
		return items;
	}

	public void restore(String id, String versionName)
			throws UnsupportedRepositoryOperationException, RepositoryException {
		Session jcrSession = getJcrSession();
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		Node node = jcrSession.getNodeByIdentifier(id);
		versionManager.restore(node.getPath(), versionName, true);
	}

	public InputStream getStream(String id) throws ItemNotFoundException,
			RepositoryException {
		Node node = getNode(id, getJcrSession());
		Node resource = node.getNode("ps:content");
		return resource.getProperty(JcrConstants.JCR_DATA).getBinary()
				.getStream();
	}

	public File getFile(String id) throws ItemNotFoundException,
			RepositoryException {
		Node node = getNode(id, getJcrSession());
		return convertToFile(node);
	}

	public File getVersion(String id, String name)
			throws UnsupportedRepositoryOperationException, RepositoryException {
		Session jcrSession = getJcrSession();
		VersionManager versionManager = jcrSession.getWorkspace()
				.getVersionManager();
		Node node = jcrSession.getNodeByIdentifier(id);
		VersionHistory versionHistory = versionManager.getVersionHistory(node
				.getPath());
		Version version = versionHistory.getVersion(name);
		return convertToFile(version.getFrozenNode());
	}

	public ArrayList<File> search(SearchParameter search)
			throws RepositoryException {
		Session jcrSession = getJcrSession();
		ArrayList<File> items = new ArrayList<File>();
		String sql = getQueryString(search);
		System.out.println(sql);
		QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(sql, Query.JCR_SQL2);
		QueryResult result = query.execute();
		NodeIterator nodes = result.getNodes();
		while (nodes.hasNext()) {
			Node nextNode = nodes.nextNode();
			items.add(convertToFile(nextNode));
		}
		return items;
	}

	private String getQueryString(SearchParameter search) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		StringBuffer sb = new StringBuffer("SELECT t.* FROM [");
		sb.append(search.isFolder() ? "ps:folder" : NodeType.NT_HIERARCHY_NODE)
				.append("] as t WHERE");
		if (StringUtils.isNoneBlank(search.getName())) {
			sb.append(" and t.[ps:name] like '%").append(search.getName())
					.append("%'");
		}
		if (StringUtils.isNoneBlank(search.getCreator())) {
			sb.append(" and t.[jcr:createdBy] like '%")
					.append(search.getCreator()).append("%'");
		}
		if (StringUtils.isNoneBlank(search.getLastModifiedBy())) {
			sb.append(" and t.[jcr:lastModifiedBy] like '%")
					.append(search.getLastModifiedBy()).append("%'");
		}
		if (null != search.getCreated_start()) {
			sb.append(" and t.[jcr:created] >= cast('")
					.append(format.format(search.getCreated_start()))
					.append("' as date)");
		}
		if (null != search.getCreated_end()) {
			sb.append(" and t.[jcr:created] <= cast('")
					.append(format.format(search.getCreated_end()))
					.append("' as date)");
		}
		if (null != search.getLastModified_start()) {
			sb.append(" and t.[jcr:lastModified] >= cast('")
					.append(format.format(search.getLastModified_start()))
					.append("' as date)");
		}
		if (null != search.getLastModified_end()) {
			sb.append(" and t.[jcr:lastModified] <= cast('")
					.append(format.format(search.getLastModified_end()))
					.append("' as date)");
		}

		String sql = sb.toString();
		if (sql.endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		if (sql.contains("WHERE and")) {
			sql = sql.replace("WHERE and", "WHERE");
		}
		return sql;
	}

	public InputStream getCopy(String id) throws ItemNotFoundException,
			RepositoryException {
		Node node = getNode(id, getJcrSession());
		Node resNode = node.getNode("ps:copy");
		return resNode.getProperty(JcrConstants.JCR_DATA).getBinary()
				.getStream();
	}

}
