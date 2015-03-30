package com.application.timmy.ui;

/**
 * Created by mtudora on 27/03/15.
 */
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.timmy.model.ChangeModel;

import timmy.application.com.landoftimmy.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ChangeModel> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, ChangeModel> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final ChangeModel childChange = (ChangeModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView childMorale = (TextView) convertView.findViewById(R.id.personDetailsMorale);
        TextView childHumour = (TextView) convertView.findViewById(R.id.personDetailsHumour);
        TextView childSkill = (TextView) convertView.findViewById(R.id.personDetailsSkill);
        ImageView childMoraleImg = (ImageView) convertView.findViewById(R.id.emoMorale);
        ImageView childHumourImg = (ImageView) convertView.findViewById(R.id.emoHumour);
        ImageView childSkillImg = (ImageView) convertView.findViewById(R.id.emoSkill);


        if (childChange != null) {
            childMorale.setText(String.valueOf(childChange.getMorale()));
            childHumour.setText(String.valueOf(childChange.getHumour()));
            childSkill.setText(String.valueOf(childChange.getSkill()));

            if (childChange.getMorale() <= 0)
                childMoraleImg.setImageResource(R.drawable.ic_action_emo_cry);
            else
            if (childChange.getMorale() <= 3)
                childMoraleImg.setImageResource(R.drawable.ic_action_emo_shame);
            else
            if (childChange.getMorale() <= 5)
                childMoraleImg.setImageResource(R.drawable.ic_action_emo_basic);
            else
                childMoraleImg.setImageResource(R.drawable.ic_action_emo_cool);

            if (childChange.getHumour() <= 0)
                childHumourImg.setImageResource(R.drawable.ic_action_emo_cry);
            else
            if (childChange.getHumour() <= 3)
                childHumourImg.setImageResource(R.drawable.ic_action_emo_shame);
            else
            if (childChange.getHumour() <= 5)
                childHumourImg.setImageResource(R.drawable.ic_action_emo_basic);
            else
                childHumourImg.setImageResource(R.drawable.ic_action_emo_cool);

            if (childChange.getSkill() <= 0)
                childSkillImg.setImageResource(R.drawable.ic_action_emo_cry);
            else
            if (childChange.getSkill() <= 3)
                childSkillImg.setImageResource(R.drawable.ic_action_emo_shame);
            else
            if (childChange.getSkill() <= 5)
                childSkillImg.setImageResource(R.drawable.ic_action_emo_basic);
            else
                childSkillImg.setImageResource(R.drawable.ic_action_emo_cool);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
       // return this._listDataChild.get(this._listDataHeader.get(groupPosition))
         //       .size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}



