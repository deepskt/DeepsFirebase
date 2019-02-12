package info.deepakom.deepakom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.deepakom.deepakom.model.Employee;
import info.deepakom.deepakom.model.EmployeeeFirebase;
import info.deepakom.deepakom.view.ProgressDialog;
import info.deepakom.deepakom.view.adapter.DataAdapter;


public class MainActivity extends Activity {
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private List<EmployeeeFirebase> mylist;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String name =getIntent().getStringExtra("userName");
        String phone = getIntent().getStringExtra("userPhone");
        Toolbar mToolBar = (Toolbar)findViewById(R.id.app_toolbar);
        TextView mtitle = (TextView) mToolBar.findViewById(R.id.tbTitle);
        mtitle.setText("Welcome "+name+"("+phone+")");
        progressDialog = new ProgressDialog(this);
        recyclerView = (RecyclerView)findViewById(R.id.lv_employee);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mylist = new ArrayList<>();
        setEmployeeList();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,UploadEmployeeData.class);
                startActivity(intent);
            }
        });
    }

    FirebaseListenerData firebaseListenerData = new FirebaseListenerData() {
        @Override
        public void onDataReceived(List<Employee> employeeData) {
            progressDialog.dismiss();
            List<Employee>mylist = new ArrayList<>();
            Log.d("Deepak","Employee data size: "+employeeData.size());
            dataAdapter = new DataAdapter();
            recyclerView.setAdapter(dataAdapter);
            dataAdapter.setDataMap(employeeData);
            dataAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDataUploaded(boolean flag) {

        }

    };

    private void setEmployeeList() {
        progressDialog.show("Checking Employee data...");
        FirebaseDataBase.getDataEmployee(firebaseListenerData);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
        super.onBackPressed();  // optional depending on your needs
    }

}
