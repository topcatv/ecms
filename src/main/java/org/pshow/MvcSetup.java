package org.pshow;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.jcr.AccessDeniedException;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NodeTypeExistsException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.pshow.common.JackrabbitUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class MvcSetup implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			if (event.getApplicationContext().getParent() == null) {
				Session manageSession = JackrabbitUtils.getManageSession();
				NamespaceRegistry namespaceRegistry = manageSession
						.getWorkspace().getNamespaceRegistry();
				try {
					namespaceRegistry.getURI("ps");
				} catch (NamespaceException e) {
					namespaceRegistry.registerNamespace("ps",
							"http://www.pshow.org/");
				}
				// TODO 需要定义常量
				if (!manageSession.getWorkspace().getNodeTypeManager()
						.hasNodeType("ps:file")) {
					ClassPathResource resource = new ClassPathResource(
							"pshow.cnd");
					CndImporter.registerNodeTypes(new InputStreamReader(
							resource.getInputStream()), manageSession);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidNodeTypeDefinitionException e) {
			e.printStackTrace();
		} catch (NodeTypeExistsException e) {
			e.printStackTrace();
		} catch (UnsupportedRepositoryOperationException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
