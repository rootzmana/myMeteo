package iut.desvignes.mymeteo;

import android.arch.paging.PagedListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.concurrent.ExecutorService;

/**
 * Created by androidS4 on 19/03/18.
 */

public class MeteoAdapter extends PagedListAdapter<MeteoRoom, MeteoAdapter.TownViewHolder>{

    static DiffUtil.ItemCallback<MeteoRoom> diffCallback = new DiffUtil.ItemCallback<MeteoRoom>() {
        @Override public boolean areItemsTheSame(MeteoRoom oldItem, MeteoRoom newItem) {
            return oldItem.getId() == newItem.getId();
        }
        @Override public boolean areContentsTheSame(MeteoRoom oldItem, MeteoRoom newItem) {
            return oldItem.equals(newItem);
        }
    };

    MeteoPresenter presenter;
    ExecutorService service;

     public MeteoAdapter(ExecutorService service, MeteoPresenter presenter){
         super(diffCallback);
         this.service = service;
         this.presenter = presenter;
     }

    @Override
    public TownViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.items_list_layout, parent, false);
        return new TownViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TownViewHolder holder, int position) {
        MeteoRoom town = this.getItem(position);
        if(town != null) holder.displayTown(town);
    }

    public class TownViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        private TextView nameView, tempView;
        private ImageView imageView;

        public TownViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.itemTownNameView);
            tempView = itemView.findViewById(R.id.textViewTemp);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void displayTown(MeteoRoom town) {
            nameView.setText(town.getTownName());
            tempView.setText(Double.toString(town.getTemperature()) + "°C");
            imageView.setImageResource(presenter.getImageID(town));
        }

    @Override
    public boolean onLongClick(View v) {
        presenter.getPrefTown(MeteoAdapter.this.getItem(getAdapterPosition()));
        return true;
    }

        @Override
        public void onClick(View view) {
            service.submit(()->presenter.launchMap(MeteoAdapter.this.getItem(getAdapterPosition())));
        }
    }
}
