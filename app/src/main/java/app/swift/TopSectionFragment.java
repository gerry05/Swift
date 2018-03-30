package app.swift;

/**
 * Created by Admiral on 22/01/2018.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TopSectionFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_section_fragment, container,false);

        Button menuBtn = view.findViewById(R.id.client_home_drawer_btn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),User_NavMenu.class);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
            }
        });

        return view;
    }

}