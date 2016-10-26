package ru.pastie.repository;

import java.util.List;
import ru.pastie.om.Paste;


public interface PasteRepository {
    boolean exists(String id);
    Paste findOne(String id);
    Paste save(Paste paste);
    List<Paste> getLatest(int count);
}
