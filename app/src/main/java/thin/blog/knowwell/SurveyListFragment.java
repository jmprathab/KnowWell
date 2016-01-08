package thin.blog.knowwell;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import adapters.SurveyListAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import datasets.Survey;
import network.CustomRequest;
import network.VolleySingleton;

/*
* SurveyListFragment class is used to display a list of Surveys undertaken or yet to be taken by the user depending upon the Request Code value set
* RequestCode = 1 --> Lists all the surveys taken by the user
* RequestCode = 2 --> Lists all the surveys not taken by the user
* */

public class SurveyListFragment extends Fragment {
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.pull_to_refresh)
    TextView pullToRefreshMessage;
    String email;
    int userId;
    int serverSuccess;
    int requestCode;
    String serverMessage;
    LinkedList<Survey> data = new LinkedList<>();
    SurveyListAdapter surveyListAdapter;
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    private OnFragmentInteractionListener mListener;
    private Context activityContext;

    public static SurveyListFragment newInstance(int requestCode) {
        SurveyListFragment fragment = new SurveyListFragment();
        Bundle args = new Bundle();
        args.putInt("REQUEST_CODE", requestCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.requestCode = bundle.getInt("REQUEST_CODE", 1);
        } else {
            this.requestCode = 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Constants.USER_DATA_EMAIL, "email");
        userId = sharedPreferences.getInt(Constants.USER_DATA_USER_ID, 1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSureys();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        refreshSureys();
    }

    private void refreshSureys() {
        swipeRefreshLayout.setRefreshing(true);

        surveyListAdapter = new SurveyListAdapter(data);
        recyclerView.setAdapter(surveyListAdapter);

        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Map<String, String> formData = new HashMap<>();
        formData.put("user_id", String.valueOf(userId));
        formData.put("request_code", String.valueOf(requestCode));

        final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.SURVEY_LIST, formData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(recyclerView, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });
        request.setTag(Constants.SURVEY_LIST);
        requestQueue.add(request);
    }

    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("status");
            if (serverSuccess == 1) {
                JSONArray surveyArray = response.getJSONArray("survey");
                for (int i = 0; i < surveyArray.length(); i++) {
                    JSONObject object = surveyArray.getJSONObject(i);
                    int surveyId = Integer.parseInt(object.getString("id"));
                    String title = object.getString("title");
                    String organization = object.getString("organization");
                    Survey survey = new Survey(surveyId, title, organization);
                    if (!data.contains(survey)) {
                        data.add(0, survey);
                    }
                }
                surveyListAdapter.notifyDataSetChanged();
                notifyChangesToView();
            } else {
                serverMessage = response.getString("message");
                Snackbar.make(recyclerView, serverMessage, Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void notifyChangesToView() {
        swipeRefreshLayout.setRefreshing(false);
        if (data.size() == 0) {
            pullToRefreshMessage.setText("No Surveys To Display\nPull Down to Refresh");
        } else {
            pullToRefreshMessage.setText("Pull Down To Refresh");
        }

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
