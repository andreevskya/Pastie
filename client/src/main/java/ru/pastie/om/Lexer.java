package ru.pastie.om;

public enum Lexer {
    PLAIN("Plain Text"),
    JAVA("Java"),
    JAVASCRIPT("Java Script"),
    JSP("JSP"),
    CSS("CSS"),
    HTML("HTML"),
    XML("XML"),
    PYTHON("Python");
    
    private final String name;
    
    Lexer(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
