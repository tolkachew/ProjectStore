package org.store.service;

import java.util.Map;

public interface FirmService {
    Map<Long, String> getAll();

    String getFirmById(long id);

    boolean create(String name);

    boolean updateFirm(long id, String name);

    boolean deleteFirm(long id, String name);
}
