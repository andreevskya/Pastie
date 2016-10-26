package ru.pastie.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pastie.configuration.ConfigurationRoutine;
import ru.pastie.om.Paste;
import ru.pastie.repository.PasteRepository;

@Service
public class PasteServiceImpl implements PasteService {

    private ConfigurationRoutine config = new ConfigurationRoutine();

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

    @Override
    public List<Paste> getLatest() {
        return repo.getLatest(this.getMaxLatestPastes());
    }

    @Override
    public int getMaxLatestPastes() {
        return config.getMaxLatestPasties();
    }
}
