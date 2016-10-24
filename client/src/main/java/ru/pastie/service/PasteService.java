package ru.pastie.service;

import ru.pastie.om.Paste;

public interface PasteService {
    
    Paste save(Paste paste);
    Paste findOne(String id);
}
