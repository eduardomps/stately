/**
 * Copyright 2016 Lloyd Torres
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lloydtorres.stately.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lloydtorres.stately.R;
import com.lloydtorres.stately.dto.Issue;
import com.lloydtorres.stately.dto.IssueOption;
import com.lloydtorres.stately.helpers.RaraHelper;
import com.lloydtorres.stately.helpers.SparkleHelper;
import com.lloydtorres.stately.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lloyd on 2016-01-29.
 * A RecyclerView used to display the contents of IssueDecisionActivity.
 */
public class IssueDecisionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // constants for the different types of cards
    private static final int INFO_CARD = 0;
    private static final int OPTION_CARD = 1;
    private static final int DISMISS_CARD = 2;

    private static final int ISSUE_PIRATE_NO = 201;

    private List<Object> cards;
    private Context context;
    private boolean pirateMode;

    public IssueDecisionRecyclerAdapter(Context c, Issue issue) {
        context = c;
        setIssue(issue);
    }

    public void setIssue(Issue issue) {
        cards = new ArrayList<Object>();
        cards.add(issue);
        cards.addAll(issue.options);

        if (issue.id == ISSUE_PIRATE_NO) {
            pirateMode = true;
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case INFO_CARD:
                View infoCard = inflater.inflate(R.layout.card_issue_info, parent, false);
                viewHolder = new IssueInfoCard(context, infoCard);
                break;
            default:
                View optionCard = inflater.inflate(R.layout.card_issue_option, parent, false);
                viewHolder = new IssueOptionCard(context, optionCard);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case INFO_CARD:
                IssueInfoCard infoCard = (IssueInfoCard) holder;
                infoCard.init((Issue) cards.get(position));
                break;
            default:
                IssueOptionCard optionCard = (IssueOptionCard) holder;
                optionCard.init((IssueOption) cards.get(position), holder.getItemViewType());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (cards.get(position) instanceof Issue) {
            return INFO_CARD;
        }
        else if (cards.get(position) instanceof IssueOption) {
            IssueOption option = (IssueOption) cards.get(position);
            if (option.index == -1) {
                return DISMISS_CARD;
            }
            return OPTION_CARD;
        }
        return -1;
    }

    // Card view holders

    // Card for info on issue
    public class IssueInfoCard extends RecyclerView.ViewHolder {
        private Context context;
        private TextView title;
        private TextView issueNo;
        private TextView content;

        public IssueInfoCard(Context c, View v) {
            super(v);
            context = c;
            title = (TextView) v.findViewById(R.id.card_issue_info_title);
            issueNo = (TextView) v.findViewById(R.id.card_issue_info_number);
            content = (TextView) v.findViewById(R.id.card_issue_option_content);
        }

        public void init(Issue issue) {
            // Forces card to span across columns
            RaraHelper.setViewHolderFullSpan(itemView);

            title.setText(SparkleHelper.getHtmlFormatting(issue.title).toString());
            if (issue.chain != null) {
                issueNo.setText(String.format(Locale.US, context.getString(R.string.issue_chain_and_number), issue.id, issue.chain));
            }
            else {
                issueNo.setText(String.format(Locale.US, context.getString(R.string.issue_number), issue.id));
            }
            content.setText(SparkleHelper.getHtmlFormatting(issue.content).toString());
        }
    }

    // Card for options
    public class IssueOptionCard extends RecyclerView.ViewHolder {
        private Context context;
        private IssueOption option;
        private TextView content;
        private LinearLayout contentHolder;
        private LinearLayout selectButton;
        private ImageView selectIcon;
        private TextView selectContent;
        private View divider;

        public IssueOptionCard(Context c, View v) {
            super(v);

            context = c;
            content = (TextView) v.findViewById(R.id.card_issue_option_content);
            contentHolder = (LinearLayout) v.findViewById(R.id.card_issue_option_content_holder);
            selectButton = (LinearLayout) v.findViewById(R.id.card_issue_option_select);
            selectIcon = (ImageView) v.findViewById(R.id.card_issue_option_select_icon);
            selectContent = (TextView) v.findViewById(R.id.card_issue_option_select_text);
            divider = v.findViewById(R.id.view_divider);
        }

        public void init(IssueOption op, int mode) {
            option = op;

            // Resets some values to default
            RaraHelper.setViewHolderFullSpan(itemView, false);
            contentHolder.setVisibility(View.VISIBLE);
            switch (SettingsActivity.getTheme(context)) {
                case SettingsActivity.THEME_VERT:
                    selectIcon.setImageResource(R.drawable.ic_check_green);
                    break;
                case SettingsActivity.THEME_NOIR:
                    selectIcon.setImageResource(R.drawable.ic_check_white);
                    break;
                case SettingsActivity.THEME_BLEU:
                    selectIcon.setImageResource(R.drawable.ic_check_blue);
                    break;
                case SettingsActivity.THEME_ROUGE:
                    selectIcon.setImageResource(R.drawable.ic_check_red);
                    break;
                case SettingsActivity.THEME_VIOLET:
                    selectIcon.setImageResource(R.drawable.ic_check_violet);
                    break;
            }
            divider.setVisibility(View.VISIBLE);

            content.setText(SparkleHelper.getHtmlFormatting(option.content).toString());
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((IssueDecisionActivity) context).setAdoptPosition(option);
                }
            });
            if (pirateMode) {
                selectContent.setText(context.getString(R.string.issue_select_option_pirate));
            }

            if (mode == DISMISS_CARD) {
                // Forces card to span across columns
                RaraHelper.setViewHolderFullSpan(itemView);

                contentHolder.setVisibility(View.GONE);
                selectContent.setText(context.getString(pirateMode ? R.string.issue_dismiss_issue_pirate : R.string.issue_dismiss_issue));
                switch (SettingsActivity.getTheme(context)) {
                    case SettingsActivity.THEME_VERT:
                        selectIcon.setImageResource(R.drawable.ic_dismiss_green);
                        break;
                    case SettingsActivity.THEME_NOIR:
                        selectIcon.setImageResource(R.drawable.ic_dismiss_white);
                        break;
                    case SettingsActivity.THEME_BLEU:
                        selectIcon.setImageResource(R.drawable.ic_dismiss_blue);
                        break;
                    case SettingsActivity.THEME_ROUGE:
                        selectIcon.setImageResource(R.drawable.ic_dismiss_red);
                        break;
                    case SettingsActivity.THEME_VIOLET:
                        selectIcon.setImageResource(R.drawable.ic_dismiss_violet);
                        break;
                }
                divider.setVisibility(View.GONE);
            }
        }
    }
}
