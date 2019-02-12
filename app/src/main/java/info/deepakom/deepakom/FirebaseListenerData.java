package info.deepakom.deepakom;

import java.util.List;

import info.deepakom.deepakom.model.Employee;
import info.deepakom.deepakom.model.EmployeeeFirebase;

public interface FirebaseListenerData {
    void onDataReceived(List<Employee> list);
    void onDataUploaded(boolean flag);
}
