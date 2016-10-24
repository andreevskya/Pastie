package ru.pastie.om;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;

@Entity
public class Paste implements Serializable {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private Lexer lexer;
    private String name;
    @Lob
    private String paste;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date expirationDate;
    private boolean privatePaste = false;
    private String userIp;
    private String userAgent;
    private int numViews = 0;
    
    public Paste() {
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Lexer getLexer() {
        return lexer;
    }

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isPrivatePaste() {
        return privatePaste;
    }

    public void setPrivatePaste(boolean privatePaste) {
        this.privatePaste = privatePaste;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getNumViews() {
        return numViews;
    }

    public void setNumViews(int numViews) {
        this.numViews = numViews;
    }
    
    public boolean isExpired() {
        if( this.expirationDate == null)
            return false;
        return this.expirationDate.before(new Date());
    }
}
