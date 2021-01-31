package org.dice.ida.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dice.ida.constant.IDAConst;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Util class containing a Map unique to the current HTTP Session
 *
 * @author Nikit
 */
@SessionScope
@Component
public class SessionUtil {
	/**
	 * Method to return a session scoped Map
	 *
	 * @return - Map
	 */
	private Map<String, Object> sessionMap;

	public SessionUtil() {
		sessionMap = new HashMap<>();
	}

	public Map<String, Object> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public void resetSessionId() {
		if (sessionMap.containsKey(IDAConst.DF_SESSION_ID)) {
			sessionMap.remove(IDAConst.DF_SESSION_ID);
		}
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}
}
