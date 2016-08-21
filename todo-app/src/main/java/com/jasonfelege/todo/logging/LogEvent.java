package com.jasonfelege.todo.logging;

import java.util.Map;

public interface LogEvent {
	
	Map<String, Object> getFields();

	void addField(String key, Object value);
	
	void addField(Fields key, Object value);
	
	void addThrowableWithStacktrace(Throwable throwable);

	void addThrowableWithStacktrace(Throwable throwable, int stacktraceDepth);
	
	public static enum Fields { 
		REQUEST_ID("request_id"), MESSAGE("message"), USER("user"), USER_ID("user_id") ,
		PASSWORD("password"), ACTION("action"), HTTP_STATUS("http_status"),
		STATUS("status"), HTTP_METHOD("http_method"), HTTP_URI("http_uri"),
		TOKEN("token"), ID("id")
		;
		private final String text; private Fields(final String text) { this.text = text; } public String toString() { return text; } }
	
	// attributes
	void setRequestId(String requestId);
	void setId(String id);
	void setMessage(String message);
	void setUser(String user);
	void setUserId(String userId);
	void setPassword(String password);
	void setAction(String actionName);
	void setHttpStatus(String code);
	void setHttpMethod(String method);
	void setHttpUri(String uri);
	void setStatus(String name);
	void setToken(String token);
}
