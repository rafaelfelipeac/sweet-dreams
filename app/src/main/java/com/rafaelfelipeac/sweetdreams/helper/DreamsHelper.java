package com.rafaelfelipeac.sweetdreams.helper;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.cunoraz.tagview.TagView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.rafaelfelipeac.sweetdreams.activities.DreamsActivity;
import com.rafaelfelipeac.sweetdreams.R;
import com.rafaelfelipeac.sweetdreams.models.Dream;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rafael Cordeiro on 28/10/2016.
 */

public class DreamsHelper implements OnLikeListener {
    @BindView(R.id.dreams_datetime)
    TextView datetime;
    @BindView(R.id.dreams_title)
    TextView title;
    @BindView(R.id.dreams_description)
    TextView description;
    @BindView(R.id.tag_group)
    TagView tagGroup;
    @BindView(R.id.favorite_dreams)
    LikeButton likeButton;
    @BindView(R.id.dreams_activity_tag_title)
    TextView tagTitle;

    private Dream dream;

    public DreamsHelper(DreamsActivity activity)  {
        ButterKnife.bind(this, activity);
        dream = new Dream();
    }

    public Dream getDream() {
        dream.setTitle(title.getText().toString());
        dream.setDescription(description.getText().toString());

        String[] datetimeArr = datetime.getText().toString().split(" - ");

        String[] arDate = datetimeArr[0].toString().split("/");
        dream.setDay(Integer.parseInt(arDate[0]));
        dream.setMonth(Integer.parseInt(arDate[1]));
        dream.setYear(Integer.parseInt(arDate[2]));

        List<com.cunoraz.tagview.Tag> tags = tagGroup.getTags();
        String lstTags = "";

        for(com.cunoraz.tagview.Tag tag : tags) {
           lstTags += tag.text + ",";
        }

        dream.setTags(lstTags);

        return dream;
    }

    public void makeDream(Dream dream) {
        title.setText(dream.getTitle());
        description.setText(dream.getDescription());
        datetime.setText(String.format("%02d", dream.getDay()) + "/" + String.format("%02d", dream.getMonth()) + "/" + dream.getYear());

        likeButton.setLiked((dream.getFavorite()));

        List<com.cunoraz.tagview.Tag> tags = new ArrayList<>(dream.tagConvertStringToArray().length);
        String[] lstTags = dream.tagConvertStringToArray();

        for(String s : lstTags) {
            if(!s.equals("")) {
                tagTitle.setVisibility(View.VISIBLE);
                com.cunoraz.tagview.Tag tag = new com.cunoraz.tagview.Tag(s);
                tag.radius = 10f;
                tag.layoutColor = Color.rgb(95, 170, 223);
                tags.add(tag);
            }
        }

        tagGroup.addTags(tags);

        this.dream = dream;
    }

    @Override
    public void liked(LikeButton likeButton) {
        dream.setFavorite(true);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        dream.setFavorite(false);
    }
}
