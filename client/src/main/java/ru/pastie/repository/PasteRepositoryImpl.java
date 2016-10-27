package ru.pastie.repository;

import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.pastie.om.Paste;

@Repository
public class PasteRepositoryImpl implements PasteRepository{
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int REGULAR_ID_LENGTH = 6;
    private static final int PRIVATE_ID_LENGTH = 12;
    private static final int MAX_NUM_REGENERATION_ATTEMPTS = 10;
    
    private Random rnd = new Random();
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    @Transactional
    public boolean exists(String id) {
        return findOne(id) == null;
    }
    
    @Override
    @Transactional
    public Paste findOne(String id) {
        return em.find(Paste.class, id);
    }

    @Override
    @Transactional
    public Paste save(Paste paste) {
        if(paste.getId() != null)
            return em.merge(paste);
        paste.setId(createId(paste.isPrivatePaste()));
        em.persist(paste);
        return paste;
    }
    
    private String createId(boolean priv) {
        for( int i = 0; i < MAX_NUM_REGENERATION_ATTEMPTS; ++i) {
            String id = createRandomString( priv ? PRIVATE_ID_LENGTH : REGULAR_ID_LENGTH);
            if(exists(id))
                return id;
        }
        throw new RuntimeException("Failed to generate new paste id: too much failed attemts to generate random id");
    }
    
    private String createRandomString(int length) {
        return createRandomString(ALPHABET, length);
    }
    
    private String createRandomString(String alphabet, int length) {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < length; ++i) {
            sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public List<Paste> getLatest(int count) {
        return em.createQuery(
                "SELECT p FROM Paste p WHERE p.privatePaste = FALSE AND (p.expirationDate IS NULL OR :now < p.expirationDate) ORDER BY p.creationDate DESC")
                .setParameter("now", new Date())
                .setMaxResults(count)
                .getResultList();
    }


    @Override
    @Transactional
    public List<Paste> getTop(int count) {
        return em.createQuery(
                "SELECT p FROM Paste p WHERE p.privatePaste = FALSE AND p.numViews > 0 AND (p.expirationDate IS NULL OR :now < p.expirationDate) ORDER BY p.numViews DESC")
                .setParameter("now", new Date())
                .setMaxResults(count)
                .getResultList();
    }
}