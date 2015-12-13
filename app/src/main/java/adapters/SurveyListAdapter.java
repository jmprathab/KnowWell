package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import datasets.Survey;
import thin.blog.knowwell.R;

/**
 * Created by jmprathab on 11/12/15.
 */
public class SurveyListAdapter extends RecyclerView.Adapter<SurveyListAdapter.ViewHolder> {
    LinkedList<Survey> data = new LinkedList<>();


    public SurveyListAdapter(LinkedList<Survey> data) {
        this.data = data;
    }


    @Override
    public SurveyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_list_single_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SurveyListAdapter.ViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.organization.setText(data.get(position).getOrganization());
        holder.imageView.setImageDrawable(data.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, organization;
        ImageView imageView;
        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.survey_title);
            organization = (TextView) itemView.findViewById(R.id.organization_name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            context = itemView.getContext();

        }
    }
}
