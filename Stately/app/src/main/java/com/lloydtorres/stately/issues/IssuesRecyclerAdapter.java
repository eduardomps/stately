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
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lloydtorres.stately.R;
import com.lloydtorres.stately.dto.Issue;
import com.lloydtorres.stately.dto.Nation;
import com.lloydtorres.stately.helpers.RaraHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by Lloyd on 2016-01-28.
 * An adapter for the IssuesFragment recycler.
 */
public class IssuesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ISSUE_CARD = 0;
    private static final int NEXT_CARD = 1;

    private Context context;
    private List<Object> issues;
    private Nation mNation;

    public IssuesRecyclerAdapter(Context c, List<Object> i, Nation n) {
        context = c;
        mNation = n;
        setIssueCards(i);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ISSUE_CARD:
                View issueCard = inflater.inflate(R.layout.card_issue_main, parent, false);
                viewHolder = new IssueCard(context, issueCard);
                break;
            default:
                View nextCard = inflater.inflate(R.layout.card_generic, parent, false);
                viewHolder = new NextCard(nextCard);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ISSUE_CARD:
                IssueCard issueCard = (IssueCard) holder;
                issueCard.init((Issue) issues.get(position));
                break;
            default:
                NextCard nextCard = (NextCard) holder;
                nextCard.init((String) issues.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (issues.get(position) instanceof Issue) {
            return ISSUE_CARD;
        }
        else if (issues.get(position) instanceof String) {
            return NEXT_CARD;
        }
        return -1;
    }

    public void setIssueCards(List<Object> cards) {
        issues = cards;
        notifyDataSetChanged();
    }

    // Card viewholders
    public class IssueCard extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context context;
        private TextView title;
        private TextView id;
        private Issue issue;

        public IssueCard(Context c, View v) {
            super(v);
            context = c;
            title = (TextView) v.findViewById(R.id.card_issue_main_title);
            id = (TextView) v.findViewById(R.id.card_issue_main_number);

            v.setOnClickListener(this);
        }

        public void init(Issue i) {
            issue = i;

            title.setText(issue.title);
            if (issue.chain != null) {
                id.setText(String.format(Locale.US, context.getString(R.string.issue_chain_and_number), issue.id, issue.chain));
            }
            else {
                id.setText(String.format(Locale.US, context.getString(R.string.issue_number), issue.id));
            }
        }

        @Override
        public void onClick(View v) {
            if (issue != null) {
                Intent decisionActivityLaunch = new Intent(context, IssueDecisionActivity.class);
                decisionActivityLaunch.putExtra(IssueDecisionActivity.ISSUE_DATA, issue);
                decisionActivityLaunch.putExtra(IssueDecisionActivity.NATION_DATA, mNation);
                context.startActivity(decisionActivityLaunch);
            }
        }
    }

    public class NextCard extends RecyclerView.ViewHolder {

        private TextView nextUpdate;

        public NextCard(View v) {
            super(v);
            v.findViewById(R.id.card_generic_title).setVisibility(View.GONE);
            nextUpdate = (TextView) v.findViewById(R.id.card_generic_content);
        }

        public void init(String m) {
            RaraHelper.setViewHolderFullSpan(itemView);

            nextUpdate.setText(m);
            nextUpdate.setTypeface(nextUpdate.getTypeface(), Typeface.ITALIC);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 0);
            nextUpdate.setLayoutParams(params);
        }
    }
}
