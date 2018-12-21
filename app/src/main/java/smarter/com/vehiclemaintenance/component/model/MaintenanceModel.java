package smarter.com.vehiclemaintenance.component.model;

public class MaintenanceModel {

    String Code;
    String VehicleCode;
    String VehicleNumber;
    String Alias;
    String CreatedByEmployeeFullName;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getVehicleCode() {
        return VehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        VehicleCode = vehicleCode;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public String getCreatedByEmployeeFullName() {
        return CreatedByEmployeeFullName;
    }

    public void setCreatedByEmployeeFullName(String createdByEmployeeFullName) {
        CreatedByEmployeeFullName = createdByEmployeeFullName;
    }
}
