package com.sd.rafael.sweetdreams.helper;

import android.graphics.Color;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cunoraz.tagview.TagView;
import com.sd.rafael.sweetdreams.activity.DreamsActivity;
import com.sd.rafael.sweetdreams.R;
import com.sd.rafael.sweetdreams.models.Dream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafae on 28/10/2016.
 */

public class DreamsHelper {
    private TextView datetime;
    private TextView title;
    private TextView description;
    private TagView tagGroup;
    private RatingBar ratingBar;

    private Dream dream;

    public DreamsHelper(DreamsActivity activity) {
        datetime = (TextView) activity.findViewById(R.id.dreams_datetime);
        title = (TextView) activity.findViewById(R.id.dreams_title);
        description = (TextView) activity.findViewById(R.id.dreams_description);
        tagGroup = (TagView) activity.findViewById(R.id.tag_group);
        ratingBar = (RatingBar) activity.findViewById(R.id.favorite_dreams);

        dream = new Dream();
    }

    public Dream getDream() {
        dream.setTitle(title.getText().toString());
        dream.setDescription(description.getText().toString());

        dream.setFavorite((ratingBar.getRating() == 1) ? true : false);

        String[] datetimeArr = datetime.getText().toString().split(" - ");

        String[] arDate = datetimeArr[0].toString().split("/");
        dream.setDay(Integer.parseInt(arDate[0]));
        dream.setMonth(Integer.parseInt(arDate[1]));
        dream.setYear(Integer.parseInt(arDate[2]));

        List<com.cunoraz.tagview.Tag> tags = tagGroup.getTags();
        String lstTags = "";

        for(com.cunoraz.tagview.Tag tag : tags) {
           lstTags += tag.text + ", ";
        }

        dream.setTags(lstTags);

        return dream;
    }

    public void makeDream(Dream dream) {
        title.setText(dream.getTitle());
        description.setText(dream.getDescription());
        datetime.setText(dream.getDay() + "/" + dream.getMonth() + "/" + dream.getYear());

        ratingBar.setRating((dream.getFavorite()) ? 1 : 0);

        List<com.cunoraz.tagview.Tag> tags = new ArrayList<>(dream.tagConvertStringToArray().length);
        String[] lstTags = dream.tagConvertStringToArray();

        for(String s : lstTags) {
            if(!s.equals("")) {
                com.cunoraz.tagview.Tag tag = new com.cunoraz.tagview.Tag(s);
                tag.radius = 10f;
                tag.layoutColor = Color.rgb(95, 170, 223);
                tags.add(tag);
            }
        }

        tagGroup.addTags(tags);

        this.dream = dream;
    }
}
