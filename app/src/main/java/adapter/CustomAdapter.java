package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task1.MainActivity;
import com.example.task1.R;

import java.util.List;

import model.ListModel;

public class CustomAdapter extends ArrayAdapter {
    TextView policyTerm,tillYouTurn,download,cover,priceInc,claim,limitedPay,income,lumpsumAmt,preMedical;
    Button button1;
    ImageView imageView;
    Context context;
    int resource;
    List<ListModel> myList;
    ListModel product;


    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<ListModel> myList) {
        super(context, resource, myList);
        this.context=context;
        this.resource=resource;
        this.myList=myList;
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflator = LayoutInflater.from(context);
            View view = inflator.inflate(resource, null);
        try {
            policyTerm = view.findViewById(R.id.policyTerm);
            tillYouTurn = view.findViewById(R.id.tillYouTurn);
            download = view.findViewById(R.id.download);
            cover = view.findViewById(R.id.coverTxtView);
            claim = view.findViewById(R.id.claim);
            priceInc = view.findViewById(R.id.priceInc);
            limitedPay = view.findViewById(R.id.limitedPay);
            income = view.findViewById(R.id.income);
            lumpsumAmt = view.findViewById(R.id.lumpsumAmt);
            preMedical = view.findViewById(R.id.preMedical);
            button1 = view.findViewById(R.id.button1);
            imageView = view.findViewById(R.id.imageView);
        }catch (Exception e){
            e.printStackTrace();
        }

        product=myList.get(position);

        try {
            policyTerm.setText(product.getPolicyTerm());
            tillYouTurn.setText(product.getTillYouTurn());
            download.setText(product.getDownload());
            cover.setText(product.getCover());
            claim.setText(product.getClaimSettlement());
            priceInc.setText(product.getPriceInc());
            limitedPay.setText(product.getLimitedPay());
            income.setText(product.getMonthlyIncome());
            lumpsumAmt.setText(product.getLumpsum());
            preMedical.setText(product.getPremedical());
            button1.setText(product.getKshButtonTxt());
            imageView.setImageDrawable(getContext().getDrawable(product.getDrawableID()));
        }catch (Exception e){
            e.printStackTrace();
        }

        return  view;
    }
}
