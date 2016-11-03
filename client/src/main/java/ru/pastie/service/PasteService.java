package ru.pastie.service;

import java.util.List;
import ru.pastie.om.Paste;

public interface PasteService {
    
    Paste save(Paste paste);
    Paste findOne(String id);
    int getMaxLatestPastes();
    List<Paste> getLatest();
    List<Paste> getTop();
    int countPublicAndNotExpiredPages();
    List<Paste> listPublicAndNotExpired(int page);
}
