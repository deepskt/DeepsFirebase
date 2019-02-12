package info.deepakom.deepakom.view.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import info.deepakom.deepakom.FirebaseDataBase;
import info.deepakom.deepakom.R;
import info.deepakom.deepakom.model.Employee;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataAdapterVH> {
    private List<Employee> mlist;
    private int currentPosition;
    public DataAdapter(){
    }

    public void setDataMap(List<Employee> eployeeList) {
        mlist = new ArrayList<>();
        mlist = eployeeList;
        notifyItemChanged(0, this.mlist.size());
        notifyDataSetChanged();
    }

    @Override
    public DataAdapterVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new DataAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(DataAdapterVH holder, int position) {
        currentPosition = position;
        Employee employee = mlist.get(position);
        if(getBitmap(employee.getProfile()) != null){
            holder.avPhoto.setImageBitmap(getBitmap(employee.getProfile()));
        }else{
            holder.avPhoto.setImageResource(R.mipmap.image_not_found);
        }
        holder.tvName.setText(employee.getName());
        holder.tvEmail.setText(employee.getEmail());
        holder.tvSkill.setText(employee.getSkills());
        holder.tvDob.setText(employee.getDob());
        holder.tvPhone.setText(employee.getPhone());
        holder.tvSalary.setText(employee.getSalary());
    }

    private Bitmap getBitmap(byte[] data){
        if(data != null) {
            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
            return image;
        }else{
            return null;
        }

    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    public class DataAdapterVH extends RecyclerView.ViewHolder {

        private ImageView avPhoto;
        private TextView tvName, tvEmail,tvPhone, tvSalary, tvDob, tvSkill;

        public DataAdapterVH(View view) {
            super(view);
            avPhoto = (ImageView) view.findViewById(R.id.ap_employee);
            tvName = (TextView)view.findViewById(R.id.tvName);
            tvEmail = (TextView)view.findViewById(R.id.tvEmail);
            tvPhone = (TextView)view.findViewById(R.id.tvPhone);
            tvDob = (TextView)view.findViewById(R.id.tvDOB);
            tvSkill = (TextView)view.findViewById(R.id.tvSkills);
            tvSalary = (TextView)view.findViewById(R.id.tvSalary);
        }
    }

}



