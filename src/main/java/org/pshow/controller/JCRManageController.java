package org.pshow.controller;

import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NodeTypeExistsException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;

import org.pshow.common.JackrabbitUtils;
import org.pshow.domain.Namespace;
import org.pshow.domain.PropertyDef;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jcrmanage")
public class JCRManageController {

	@SuppressWarnings("unchecked")
	@RequestMapping("/registNodeType")
	public void registNodeType(String nodeTypeName, List<PropertyDef> properties) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException{
		Session manageSession = JackrabbitUtils.getManageSession();
		
		/* Retrieve node type manager from the session */
		NodeTypeManager nodeTypeManager = manageSession.getWorkspace().getNodeTypeManager();

		/* Create node type */
		NodeTypeTemplate nodeType = nodeTypeManager.createNodeTypeTemplate();
		nodeType.setName(nodeTypeName);
		nodeType.setMixin(false);
		nodeType.setQueryable(true);
		nodeType.setOrderableChildNodes(true);
		for (PropertyDef propertyDef : properties) {
			PropertyDefinitionTemplate pdt = nodeTypeManager.createPropertyDefinitionTemplate();
			pdt.setName(propertyDef.getName());
			pdt.setFullTextSearchable(propertyDef.isFullTextSearchable());
			pdt.setMandatory(propertyDef.isMandatory());
			pdt.setMultiple(propertyDef.isMultiple());
			pdt.setRequiredType(PropertyType.valueFromName(propertyDef.getType()));
			nodeType.getPropertyDefinitionTemplates().add(pdt);
		}

		/* Register node type */
		nodeTypeManager.registerNodeType(nodeType, false);
		manageSession.save();
	}
	
	@RequestMapping("/registNamespace")
	public void registNamespace(Namespace namespace) throws AccessDeniedException, NamespaceException, UnsupportedRepositoryOperationException, RepositoryException{
		Session manageSession = JackrabbitUtils.getManageSession();
		NamespaceRegistry namespaceRegistry = manageSession.getWorkspace().getNamespaceRegistry();
		namespaceRegistry.registerNamespace(namespace.getPrefix(), namespace.getUri());
	}
}
