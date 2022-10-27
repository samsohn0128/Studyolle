package com.studyolle.zone;

import com.studyolle.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String city, String province);
}
