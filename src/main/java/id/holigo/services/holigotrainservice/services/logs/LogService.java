package id.holigo.services.holigotrainservice.services.logs;


import id.holigo.services.common.model.SupplierLogDto;

public interface LogService {
    void sendSupplierLog(SupplierLogDto supplierLogDto);
}
