package info.deepakom.deepakom;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import info.deepakom.deepakom.model.Employee;
import info.deepakom.deepakom.model.EmployeeeFirebase;
import info.deepakom.deepakom.view.ProgressDialog;

public class UploadEmployeeData extends Activity implements View.OnClickListener {
    //Declaring views
    private Button buttonChoose;
    private Button buttonUpload, buttonSubmit;
    private ImageView imageView;
    private EditText edName, edPhone,edEmail,edDOB,edsalary;
    private AppCompatCheckBox chAndroid,chJava;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employeeform);
        //Initializing views
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonSubmit = (Button) findViewById(R.id.btsubmit);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.image_not_found);

        edName = (EditText)findViewById(R.id.fieldName);
        edDOB = (EditText)findViewById(R.id.fieldDOB);
        edDOB.setOnClickListener(this);
        edPhone = (EditText)findViewById(R.id.fieldPhoneNumber);
        edEmail = (EditText)findViewById(R.id.fieldEmail);
        edsalary = (EditText)findViewById(R.id.fieldSalary);
        chAndroid = (AppCompatCheckBox)findViewById(R.id.fieldAndroid);
        chJava = (AppCompatCheckBox)findViewById(R.id.fieldJAva);


        //Setting clicklistener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if (v == buttonUpload) {
            removeFile();
        }

        if(v == buttonSubmit){
            SubmitEmployeeData();
        }

        if(v == edDOB){
            selectData();
        }
    }

    private void removeFile() {
        imageView.setImageResource(R.mipmap.image_not_found);
    }

    private void selectData() {
        Log.d("Deepak", "Date =");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(this,
                myDateListener, year, month, day);
        datePicker.show();
    }

    private void SubmitEmployeeData() {
        EmployeeeFirebase employeeeFirebase = new EmployeeeFirebase();
        if(isEmpty(getEditTextData(edName))){
            Toast.makeText(this,"Employee Name is missing.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(isEmpty(getEditTextData(edEmail))){
            Toast.makeText(this,"Employee Email is missing.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(isEmpty(getEditTextData(edPhone))){
            Toast.makeText(this,"Employee Phone is missing.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(isEmpty(String.valueOf(getEditTextData(edsalary)))){
            Toast.makeText(this,"Employee Salary is missing.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(isEmpty(getEditTextData(edDOB))){
            Toast.makeText(this,"Employee DOB is missing.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!chJava.isChecked() && !chAndroid.isChecked()){
            Toast.makeText(this,"Employee Skills is missing. Select Atleast one.",Toast.LENGTH_SHORT).show();
            return;
        }

        employeeeFirebase.setDob(getEditTextData(edDOB));
        employeeeFirebase.setEmail(getEditTextData(edEmail));
        employeeeFirebase.setName(getEditTextData(edName));
        employeeeFirebase.setPhone(getEditTextData(edPhone));
        employeeeFirebase.setSalary(Long.parseLong(getEditTextData(edsalary)));
        String skills ="";
        if(chAndroid.isChecked()){
            skills=skills+"Android"+" ,";
        }
        if(chJava.isChecked()){
            skills=skills+"Java";
        }
        employeeeFirebase.setSkills(skills);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        progressDialog.show("Uploading Employee Data Please wait..");
        FirebaseDataBase.uploadData(employeeeFirebase,data,firebaseListenerData );

    }

    FirebaseListenerData firebaseListenerData = new FirebaseListenerData() {
        @Override
        public void onDataReceived(List<Employee> list) {
        }

        @Override
        public void onDataUploaded(boolean flag) {
            if(flag){
                progressDialog.dismiss();
                Intent intent = new Intent(UploadEmployeeData.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                progressDialog.changeToError("Employee Data Upload Failed");

            }

        }
    };
    private static boolean isEmpty(String str) {
        return str == null || str.equals("") || str.equals("null");

    }

    private String getEditTextData(EditText e){
        return String.valueOf(e.getText()) != null ? String.valueOf(e.getText()): "";
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    Log.d("Deepak", ""+arg1+" "+arg2);
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        edDOB.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

}
