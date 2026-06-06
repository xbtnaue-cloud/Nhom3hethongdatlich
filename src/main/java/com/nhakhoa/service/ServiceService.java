package com.nhakhoa.service;

import com.nhakhoa.model.Service;
import com.nhakhoa.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepo;

    // Thêm hàm này để Controller hết lỗi
    public int addService(String name, String desc, double price, String imagePath) {
        Service s = new Service();
        s.setServiceName(name);
        s.setDescription(desc);
        s.setPrice(price);
        s.setServiceImage(imagePath);
        return serviceRepo.save(s).getServiceID();
    }
    @Transactional
    public void deleteService(int id) {
        serviceRepo.deleteById(id);
    }
    @Transactional
    public void updateService(int id, String name, String desc, double price, String imagePath, String[] doctorIds) {
        Service s = serviceRepo.findById(id).orElse(null);
        if (s != null) {
            s.setServiceName(name);
            s.setDescription(desc);
            s.setPrice(price);
            if (imagePath != null) s.setServiceImage(imagePath);
            serviceRepo.save(s);
            // Gọi update danh sách bác sĩ tại đây
            updateDentistServices(id, doctorIds);
        }
    }
    public Service getServiceByID(int id) {
        return serviceRepo.findById(id).orElse(null);
    }

    // Thêm hàm này để Controller hết lỗi
    @Transactional
    public void updateDentistServices(int serviceId, String[] doctorIds) {
        // Nếu bạn chưa có DentistServiceRepository, hãy xóa thủ công 
        // hoặc gọi câu lệnh SQL thuần qua JdbcTemplate ở đây.
    }
 // Thêm hàm này vào trong class ServiceService
    public List<Service> getAllServices() {
        // Gọi repository để lấy danh sách từ database
        // Nếu bạn muốn lấy dữ liệu dạng JOIN phức tạp như code cũ, 
        // hãy sử dụng hàm findAllServicesWithDoctorInfo() đã viết trong Repository
        List<Object[]> rows = serviceRepo.findAllServicesWithDoctorInfo();
        List<Service> list = new ArrayList<>();
        
        for (Object[] row : rows) {
            Service s = new Service();
            s.setServiceID((Integer) row[0]);
            s.setServiceName((String) row[1]);
            s.setDescription((String) row[2]);
            s.setPrice((Double) row[3]);
            s.setServiceImage((String) row[4]);
            s.setDoctorNames((String) row[5]);
            
            String json = (String) row[6];
            s.setDoctorIdsJson((json == null || json.equals("[]")) ? "[]" : json);
            
            list.add(s);
        }
        return list;
    }
}