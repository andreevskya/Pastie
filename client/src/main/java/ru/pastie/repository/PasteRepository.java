package ru.pastie.repository;

import ru.pastie.om.Paste;


public interface PasteRepository {
    boolean exists(String id);
    Paste findOne(String id);
    Paste save(Paste paste);
}
