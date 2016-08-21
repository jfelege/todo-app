package com.jasonfelege.todo.logging;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class LogEventImpl implements LogEvent {
	// based on:
	// https://github.com/splunk/splunk-library-javalogging/blob/master/src/main/java/com/splunk/logging/SplunkCimLogEvent.java
	
    private static final String KVDELIM = "=";
    private static final String PAIRDELIM = " ";
    private static final char QUOTE = '"';

    private LinkedHashMap<String, Object> entries;
    
    public LogEventImpl(String eventName) {
    	entries = new LinkedHashMap<String, Object>();
    	addField("name", eventName);
    }
    
    public LogEventImpl(String eventName, String eventId) {
        this(eventName);
        addField("event_id", eventId);
    }
    
    public Map<String, Object> getFields() {
    	return this.entries;
    }
    
    public void addField(LogEvent.Fields field, Object value) {
    	addField(field.toString(), value);
    }

    public void addField(String key, Object value) {
    	if (entries.get(key) instanceof Collection) {
    		
    		@SuppressWarnings("unchecked")
			Collection<Object> c = (Collection<Object>)entries.get(key);    		
    		c.add(value);
    		
    	}
    	else {
    		entries.put(key, value);
    	}
    }

    public void addThrowableWithStacktrace(Throwable throwable) {
        addThrowableWithStacktrace(throwable, Integer.MAX_VALUE);
    }

    public void addThrowableWithStacktrace(Throwable throwable, int stacktraceDepth) {
        addField(THROWABLE_CLASS, throwable.getClass().getCanonicalName());
        addField(THROWABLE_MESSAGE, throwable.getMessage());

        StackTraceElement[] elements = throwable.getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int depth = 0; depth < elements.length && depth < stacktraceDepth; depth++) {
            if (depth > 0)
                sb.append(",");
            sb.append(elements[depth].toString());
        }

        addField(THROWABLE_STACKTRACE_ELEMENTS, sb.toString());
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        boolean first = true;
        for (String key : entries.keySet()) {
            if (!first) {
                output.append(PAIRDELIM);
            } else {
                first = false;
            }
            
            Object obj = entries.get(key);
            
            String value = (obj == null ? "null" : obj.toString());
            
            // Escape any " that appear in the key or value.
            key = key.replaceAll("\"", "\\\\\"");
            value = value.replaceAll("\"", "\\\\\"");

            output.append(key + KVDELIM + QUOTE + value + QUOTE);
        }

        return output.toString();
    }
    
    /**
     * Java Throwable type fields
     */
    private static final String THROWABLE_CLASS = "throwable_class";
    private static final String THROWABLE_MESSAGE = "throwable_message";
    private static final String THROWABLE_STACKTRACE_ELEMENTS = "stacktrace_elements";
    
    public void setRequestId(String requestId) {
    	addField(LogEvent.Fields.REQUEST_ID, requestId);
    }

    public void setMessage(String message) {
    	addField(LogEvent.Fields.MESSAGE, message);
    }

    public void setUser(String user) {
    	addField(LogEvent.Fields.USER, user);
    }
    
    public void setPassword(String password) {
    	addField(LogEvent.Fields.PASSWORD, password);
    }
    
    public void setAction(String actionName) {
    	addField(LogEvent.Fields.ACTION, actionName);
    }
    
    public void setHttpStatus(String code) {
    	addField(LogEvent.Fields.HTTP_STATUS, code);
    }
}
