package org.diplom.blog.repository;

import org.diplom.blog.model.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends CrudRepository<GlobalSetting, Long> {
    GlobalSetting findByCode(String code);
}
