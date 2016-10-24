package ru.pastie.om;

import java.io.Serializable;
import java.util.Date;
import org.hibernate.validator.constraints.NotBlank;

public class PasteForm implements Serializable {
    private Lexer syntax = Lexer.PLAIN;
    private Expiration expiration = Expiration.NEVER;
    private String name;
    @NotBlank
    private String paste;
    private boolean privatePaste = false;
    
    public PasteForm() {
        
    }

    public Lexer getSyntax() {
        return syntax;
    }

    public void setSyntax(Lexer syntax) {
        this.syntax = syntax;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public void setExpiration(Expiration expiration) {
        this.expiration = expiration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaste() {
        return paste;
    }

    public void setPaste(String paste) {
        this.paste = paste;
    }

    public boolean isPrivatePaste() {
        return privatePaste;
    }

    public void setPrivatePaste(boolean privatePaste) {
        this.privatePaste = privatePaste;
    }
    
    public Paste toPaste() {
        Paste p = new Paste();
        p.setCreationDate(new Date());
        p.setExpirationDate(this.expiration.getExpirationDate(p.getCreationDate()));
        p.setLexer(this.syntax);
        p.setName(this.name);
        p.setNumViews(0);
        p.setPrivatePaste(this.privatePaste);
        p.setPaste(this.paste);
        return p;
    }
}
