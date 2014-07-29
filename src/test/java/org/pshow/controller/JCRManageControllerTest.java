package org.pshow.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.LoginException;
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
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
//		jcrManageController = this.getBean(JCRManageController.class);
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
				e.printStackTrace();
			}
			try {
				jcrManageController.registNodeType(nodeTypeName, properties);
			} catch (NodeTypeExistsException e) {
				e.printStackTrace();
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
	
	@Test
	public void testACL() {
		
		try {
			Session manageSession = JackrabbitUtils.getManageSession();
			AccessControlManager acm = manageSession.getAccessControlManager();
//			AccessControlPolicyIterator applicablePolicies = acm.getApplicablePolicies("/a");
//			while (applicablePolicies.hasNext()) {
//				AccessControlPolicy acp = applicablePolicies.nextAccessControlPolicy();
//				JackrabbitAccessControlList jacl = (JackrabbitAccessControlList) acp;
//				AccessControlEntry[] aces = jacl.getAccessControlEntries();
//				for (AccessControlEntry ace : aces) {
//					System.out.println(ace.getPrincipal().getName());
//					Privilege[] privileges = ace.getPrivileges();
//					for (Privilege pri : privileges) {
//						System.out.println(pri.getName());
//					}
//				}
//			}
//			Node node = manageSession.getNodeByIdentifier("01a95b0d-1d6d-403b-8030-95d62c9bcc0d");
//			String path = node.getPath();
//			System.out.println(path);
			AccessControlPolicy[] policies = acm.getPolicies("/git");
//			AccessControlPolicyIterator applicablePolicies = acm.getApplicablePolicies(path);
			AccessControlPolicy acp = policies[0];
			JackrabbitAccessControlList jacl = (JackrabbitAccessControlList) acp;
//			PrincipalManager pMgr = ((JackrabbitSession) manageSession)
//					.getPrincipalManager();
//			Principal principal = pMgr.getPrincipal("roy");
//			Privilege privilege = acm.privilegeFromName(Privilege.JCR_ALL);
//			Privilege privilege2 = acm.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT);
//			Privilege privilege3 = acm.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES);
//			Privilege privilege4 = acm.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT);
//			Privilege privilege5 = acm.privilegeFromName(Privilege.JCR_REMOVE_CHILD_NODES);
//			jacl.addEntry(principal, new Privilege[]{privilege}, true);
//			acm.setPolicy("/", jacl);
//			manageSession.save();
			AccessControlEntry[] aces = jacl.getAccessControlEntries();
			for (AccessControlEntry ace : aces) {
				JackrabbitAccessControlEntry jace = (JackrabbitAccessControlEntry) ace;
				System.out.println(jace.getPrincipal().getName()+" --- "+jace.isAllow());
				Privilege[] privileges = jace.getPrivileges();
				for (Privilege pri : privileges) {
					System.out.println(pri.getName());
				}
				System.out.println("**************************************************");
			}
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
