package com.utilFrame;

import jakarta.servlet.http.HttpSession;

public class MySession {
    private HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
    }

    public void add(String key, Object object) {
        session.setAttribute(key, object);
    }

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public void delete(String key) {
        session.removeAttribute(key);
    }

    public MySession getSession() {
        return new MySession(session);
    }

    public void invalidate() {
        session.invalidate();
    }
}
