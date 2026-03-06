package com.example.smart_home.repository;

import com.example.smart_home.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 设备仓库
 * 用于设备相关的数据库操作
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    /**
     * 根据用户ID查找设备列表
     * @param userId 用户ID
     * @return 设备列表
     */
    List<Device> findByUserId(Long userId);
    
    /**
     * 统计用户的设备数量
     * @param userId 用户ID
     * @return 设备数量
     */
    @Query("SELECT COUNT(d) FROM Device d WHERE d.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的在线设备数量
     * @param userId 用户ID
     * @return 在线设备数量
     */
    @Query("SELECT COUNT(d) FROM Device d WHERE d.user.id = :userId AND d.status = 'online'")
    long countOnlineByUserId(@Param("userId") Long userId);
    
    /**
     * 按设备类型统计用户的设备数量
     * @param userId 用户ID
     * @return 设备类型及数量列表
     */
    @Query("SELECT d.deviceType.name, COUNT(d) FROM Device d WHERE d.user.id = :userId GROUP BY d.deviceType.name")
    List<Object[]> countByTypeForUser(@Param("userId") Long userId);
}
