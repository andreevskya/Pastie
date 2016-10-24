package ru.pastie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pastie.om.Paste;
import ru.pastie.repository.PasteRepository;

@Service
public class PasteServiceImpl implements PasteService {
    
    @Autowired
    PasteRepository repo;
    
    @Override
    public Paste save(Paste paste) {
        return repo.save(paste);
    }

    @Override
    public Paste findOne(String id) {
        return repo.findOne(id);
    }
}
