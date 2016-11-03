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
    public List<Paste> getTop() {
        return repo.getTop(config.getMaxTopPasties());
    }

    @Override
    public int getMaxLatestPastes() {
        return config.getMaxLatestPasties();
    }

    @Override
    public int countPublicAndNotExpiredPages() {
        return 1 + repo.countPublicAndNotExpired() / config.getPageSize();
    }

    @Override
    public List<Paste> listPublicAndNotExpired(int page) {
        if(page <= 0)
            page = 1;
        int offset = (page - 1) * config.getPageSize();
        return repo.listPublicAndNotExpired(offset, config.getPageSize());
    }
}
