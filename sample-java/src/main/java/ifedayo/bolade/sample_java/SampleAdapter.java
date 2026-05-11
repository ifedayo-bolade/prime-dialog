package ifedayo.bolade.sample_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleViewHolder> {

    MainActivity mainActivity;
    private final ArrayList<String> list;

    public SampleAdapter(MainActivity mainActivity, ArrayList<String> list){
        this.mainActivity = mainActivity;
        this.list = list;
    }

    @NonNull
    @Override
    public SampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sample_adapter_layout, null, false);
        return new SampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SampleViewHolder extends RecyclerView.ViewHolder {

        TextView textView = itemView.findViewById(R.id.textView);

        public SampleViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                switch (position){
                    case 0: mainActivity.demoSimpleDialog();break;
                    case 1: mainActivity.demoAccentColoredDialog();break;
                    case 2: mainActivity.demoMultiColoredDialog();break;
                    case 3: mainActivity.demoDimensionDialog();break;
                    case 4: mainActivity.demoHeaderDialog();break;
                    case 5: mainActivity.demoAnimatedDialog();break;
                    case 6: mainActivity.demoListenerDialog();break;
                }
            });
        }
    }
}