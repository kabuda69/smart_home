package com.example.smart_home.repository;

import com.example.smart_home.entity.SharedSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SharedSnapshotRepository extends JpaRepository<SharedSnapshot, Long> {
    Optional<SharedSnapshot> findByLinkUuid(String linkUuid);
    List<SharedSnapshot> findByUserId(Long userId);
    List<SharedSnapshot> findByExpireTimeBefore(LocalDateTime time);
    void deleteByDeviceId(Long deviceId);
}
