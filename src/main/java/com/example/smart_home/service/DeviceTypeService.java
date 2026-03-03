package com.example.smart_home.service;

import com.example.smart_home.entity.DeviceType;
import com.example.smart_home.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DeviceTypeService {
    @Autowired
    private DeviceTypeRepository deviceTypeRepository;
    
    public List<DeviceType> getAllTypes() {
        return deviceTypeRepository.findAll();
    }
    
    public DeviceType getType(Long id) {
        return deviceTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("设备类型不存在"));
    }
    
    @Transactional
    public DeviceType createType(DeviceType type) {
        if (deviceTypeRepository.existsByName(type.getName())) {
            throw new RuntimeException("设备类型名称已存在");
        }
        return deviceTypeRepository.save(type);
    }
    //
    @Transactional
    public DeviceType updateType(Long id, DeviceType type) {
        DeviceType existing = getType(id);
        existing.setName(type.getName());
        existing.setParamTemplate(type.getParamTemplate());
        existing.setDescription(type.getDescription());
        existing.setIcon(type.getIcon());
        return deviceTypeRepository.save(existing);
    }
    
    @Transactional
    public void deleteType(Long id) {
        deviceTypeRepository.deleteById(id);
    }
}
