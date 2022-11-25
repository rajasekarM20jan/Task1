package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.task1.R;

import java.util.List;

import model.ComparisonListModel;

public class CustomComparisonAdapter extends ArrayAdapter {
    ImageView companyLogo;
    TextView companyName;
    CheckBox checkBox;
    Context context;
    int count;
    int resource;
    List<ComparisonListModel> myList;
    ComparisonListModel product;
    public CustomComparisonAdapter(@NonNull Context context, int resource, @NonNull List<ComparisonListModel> myList) {
        super(context, resource, myList);
        this.context=context;
        this.resource=resource;
        this.myList=myList;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        count=0;
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(resource,null);
        try {
            companyLogo = view.findViewById(R.id.comparisonImageView);
            companyName = view.findViewById(R.id.comparisonTextView);
            checkBox = view.findViewById(R.id.comparisonCheckbox);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        product=myList.get(position);

        try {
            companyLogo.setImageDrawable(getContext().getDrawable(product.getCompanyLogo()));
            companyName.setText(product.getCompanyName());
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(count<2){
                        count +=1;
                    }
                }else {
                    if(count>0){
                        count -=1;
                    }
                }
                if(count==2){
                    Toast.makeText(context, "Redirects to the Comparison Activity", Toast.LENGTH_SHORT).show();

                }
            }
        });
        }catch (Exception e){
            e.printStackTrace();
        }


        return view;
    }
}
