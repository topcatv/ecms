/**
 * 
 */
package org.pshow.common;

import javax.servlet.http.HttpSession;

import org.pshow.common.constants.SessionConstants;
import org.pshow.domain.User;

/**
 * @author Sin
 *
 */
public class SessionUtils {
	public static Long getUserId(HttpSession session) {
		return ((User)session.getAttribute(SessionConstants.USER)).getId();
	}
}
