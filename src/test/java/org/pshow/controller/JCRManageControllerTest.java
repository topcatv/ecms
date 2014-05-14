package org.pshow.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.NamespaceException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeExistsException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.mvc.Mvcs;
import org.pshow.common.JackrabbitUtils;
import org.pshow.domain.Namespace;
import org.pshow.domain.PropertyDef;

public class JCRManageControllerTest extends BaseTest {
	private JCRManageController jcrManageController;
	private String nodeTypeName;
	private List<PropertyDef> properties;
	private Namespace namespace;
	private PropertyDef pd;

	@Before
	public void setUp() throws Exception {
		jcrManageController = Mvcs.getIoc().get(JCRManageController.class);
		nodeTypeName = "app:test";
		properties = new ArrayList<PropertyDef>();
		pd = new PropertyDef();
		pd.setName("app:name");
		pd.setFullTextSearchable(true);
		pd.setMandatory(true);
		pd.setType("String");
		properties.add(pd);
		namespace = new Namespace();
		namespace.setPrefix("app");
		namespace.setUri("http://www.pshow.org/app");
		System.setProperty("disableCheckForReferencesInContentException", "true");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRegistNodeType() {
		
		try {
			try {
				jcrManageController.registNamespace(namespace);
			} catch (NamespaceException e) {
			}
			try {
				jcrManageController.registNodeType(nodeTypeName, properties);
			} catch (NodeTypeExistsException e) {
			}
			Session manageSession = JackrabbitUtils.getManageSession();
			NodeTypeManager nodeTypeManager = manageSession.getWorkspace().getNodeTypeManager();
			NodeType nodeType = nodeTypeManager.getNodeType(nodeTypeName);
			assertNotNull(nodeType);
			assertEquals(nodeTypeName, nodeType.getName());
			PropertyDefinition pd = nodeType.getPropertyDefinitions()[0];
			assertEquals(this.pd.getName(), pd.getName());
			assertEquals(this.pd.getType(), PropertyType.nameFromValue(pd.getRequiredType()));
			
			QueryManager queryManager = manageSession.getWorkspace().getQueryManager();
			Query query = queryManager.createQuery("select * from [app:test]", Query.JCR_SQL2);
			QueryResult result = query.execute();
			if(!result.getRows().hasNext())
				nodeTypeManager.unregisterNodeType(nodeTypeName);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
