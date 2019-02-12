package info.deepakom.deepakom;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import info.deepakom.deepakom.model.Employee;
import info.deepakom.deepakom.model.EmployeeeFirebase;

public class FirebaseDataBase {
    private static DatabaseReference mDatabaseReference;
    private static FirebaseStorage storage;
    private static long id;


    public static synchronized void init(FirebaseUser user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        if(mDatabaseReference == null) {
            mDatabaseReference = database.getReference().child("employee");
        }
    }

    public static FirebaseStorage getStorage(){
        return storage;
    }

    public static void getDataEmployee(final FirebaseListenerData firebaseListenerData){
        final List<Employee> employeeList = new ArrayList<>();
        mDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Log.d("Deepak",""+dataSnapshot.getKey());
                        Log.d("Deepak", "total count "+dataSnapshot.getChildrenCount());
                        final long[] count = {dataSnapshot.getChildrenCount()};
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            //Get user map
                            Log.d("Deepak", "" + String.valueOf(dataSnapshot1));
                            id = Long.parseLong(dataSnapshot1.getKey());
                            final EmployeeeFirebase employeeeFirebase = (EmployeeeFirebase) JsonUtil.objectify(dataSnapshot1,EmployeeeFirebase.class);
                            StorageReference islandRef = FirebaseDataBase.getStorage().getReferenceFromUrl(employeeeFirebase.getProfilepic());
                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Log.d("Deepak", "Download data"+bytes);
                                    final Employee employee = getEmployeeData(employeeeFirebase,id);
                                    employee.setProfile(bytes);
                                    employeeList.add(employee);
                                    count[0]--;
                                    if(count[0]<=0){
                                        firebaseListenerData.onDataReceived(employeeList);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                        Log.d("Deepak",databaseError.getMessage());
                    }
                });
        }

        ImageDataListener imageDataListener = new ImageDataListener() {
            @Override
            public void onImageData(byte[] data) {

            }
        };

    private static Employee getEmployeeData(EmployeeeFirebase employeeeFirebase,long id) {
        Employee employee = new Employee();
        employee.setEmployeeId(String.valueOf(id));
        employee.setName(employeeeFirebase.getName());
        employee.setPhone(employeeeFirebase.getPhone());
        employee.setEmail(employeeeFirebase.getEmail());
        employee.setDob(employeeeFirebase.getDob());
        employee.setSkills(JsonUtil.jsonify(employeeeFirebase.getSkills()));
        employee.setSalary(String.valueOf(employeeeFirebase.getSalary()));
        return employee;
    }

    public static void uploadData(final EmployeeeFirebase employeeeFirebase, byte[] data, final FirebaseListenerData firebaseListenerData) {
        final String url =String.valueOf(id+1);
        employeeeFirebase.setProfilepic("gs://omproject-35087.appspot.com/"+url);

        StorageReference mountainsRef = storage.getReference().child(url);
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                firebaseListenerData.onDataUploaded(false);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getMetadata().getPath();
                    Log.d("Deepak","Image url:"+url);
                    mDatabaseReference.child(url).setValue(employeeeFirebase);
                firebaseListenerData.onDataUploaded(true);
            }
        });

    }

    private void uploadImage(String path,String fbPath){
// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storage.getReference().child(fbPath);
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        UploadTask uploadTask = mountainsRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }

}
