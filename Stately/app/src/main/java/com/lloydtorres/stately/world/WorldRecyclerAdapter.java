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

package com.lloydtorres.stately.world;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lloydtorres.stately.R;
import com.lloydtorres.stately.census.TrendsActivity;
import com.lloydtorres.stately.dto.BaseRegion;
import com.lloydtorres.stately.dto.CensusDetailedRank;
import com.lloydtorres.stately.dto.DataIntPair;
import com.lloydtorres.stately.dto.Event;
import com.lloydtorres.stately.dto.Nation;
import com.lloydtorres.stately.dto.World;
import com.lloydtorres.stately.explore.ExploreActivity;
import com.lloydtorres.stately.helpers.RaraHelper;
import com.lloydtorres.stately.helpers.SparkleHelper;
import com.lloydtorres.stately.helpers.StatsCard;
import com.lloydtorres.stately.helpers.network.DashHelper;
import com.lloydtorres.stately.region.RegionOverviewRecyclerAdapter;

import org.atteo.evo.inflector.English;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lloyd on 2016-09-12.
 * A recycler adapter for the World fragment.
 */
public class WorldRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // consts for card types
    private static final int WORLD_COUNT = 0;
    private static final int WORLD_FEATURED_REGION = 1;
    private static final int WORLD_BREAKING_NEWS = 2;
    private static final int WORLD_FEATURED_CENSUS = 3;
    private static final int WORLD_CENSUS_DATA = 4;

    private List<Object> cards;
    private Context context;
    private FragmentManager fragmentManager;
    private String[] WORLD_CENSUS_ITEMS;

    public WorldRecyclerAdapter(Context c, FragmentManager fm, World w, BaseRegion fr) {
        context = c;
        fragmentManager = fm;
        WORLD_CENSUS_ITEMS = context.getResources().getStringArray(R.array.census);
        setContent(w, fr);
    }

    public void setContent(World w, BaseRegion fr) {
        cards = new ArrayList<Object>();
        // Add the number of nations and regions
        cards.add(new DataIntPair(w.numNations, w.numRegions));
        // Add featured region if available
        if (fr != null) {
            cards.add(fr);
        }
        // I know, this is the entire World model, but we only want the happenings from it
        // since we're showing it in one card
        cards.add(w);
        // Try grabbing the featured census, if it's within range
        if (w.featuredCensus < w.census.size()) {
            CensusDetailedRank featuredCensus = w.census.get(w.featuredCensus);
            featuredCensus.isFeatured = true;
            w.census.remove(w.featuredCensus);
            cards.add(featuredCensus);
        }
        // Add the rest of the census data
        cards.addAll(w.census);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case WORLD_COUNT:
                View statsCard = inflater.inflate(R.layout.card_wa_members, parent, false);
                viewHolder = new StatsCard(statsCard);
                break;
            case WORLD_FEATURED_REGION:
                View featuredRegionCard = inflater.inflate(R.layout.card_world_featured_region, parent, false);
                viewHolder = new FeaturedRegionCard(featuredRegionCard);
                break;
            case WORLD_BREAKING_NEWS:
                View breakingNewsCard = inflater.inflate(R.layout.card_world_breaking_news, parent, false);
                viewHolder = new BreakingNewsCard(breakingNewsCard);
                break;
            case WORLD_FEATURED_CENSUS:
                View featuredCensusCard = inflater.inflate(R.layout.card_world_featured_census, parent, false);
                viewHolder = new FeaturedCensusCard(featuredCensusCard);
                break;
            case WORLD_CENSUS_DATA:
                View worldCensusCard = inflater.inflate(R.layout.card_census_delta, parent, false);
                viewHolder = new WorldCensusCard(worldCensusCard);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case WORLD_COUNT:
                StatsCard statsCard = (StatsCard) holder;
                statsCard.init((DataIntPair) cards.get(position),
                        context.getString(R.string.world_nations),
                        context.getString(R.string.world_regions));
                break;
            case WORLD_FEATURED_REGION:
                FeaturedRegionCard featuredRegionCard = (FeaturedRegionCard) holder;
                featuredRegionCard.init((BaseRegion) cards.get(position));
                break;
            case WORLD_BREAKING_NEWS:
                BreakingNewsCard breakingNewsCard = (BreakingNewsCard) holder;
                breakingNewsCard.init((World) cards.get(position));
                break;
            case WORLD_FEATURED_CENSUS:
                FeaturedCensusCard featuredCensusCard = (FeaturedCensusCard) holder;
                featuredCensusCard.init((CensusDetailedRank) cards.get(position));
                break;
            case WORLD_CENSUS_DATA:
                WorldCensusCard worldCensusCard = (WorldCensusCard) holder;
                worldCensusCard.init((CensusDetailedRank) cards.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (cards.get(position) instanceof DataIntPair) {
            return WORLD_COUNT;
        }
        else if (cards.get(position) instanceof BaseRegion) {
            return WORLD_FEATURED_REGION;
        }
        else if (cards.get(position) instanceof World) {
            return WORLD_BREAKING_NEWS;
        }
        else if (cards.get(position) instanceof CensusDetailedRank) {
            CensusDetailedRank censusRank = (CensusDetailedRank) cards.get(position);
            return censusRank.isFeatured ? WORLD_FEATURED_CENSUS : WORLD_CENSUS_DATA;
        }
        return -1;
    }

    // Card viewholders
    // Featured region
    public class FeaturedRegionCard extends RecyclerView.ViewHolder {

        private BaseRegion regionData;

        private RelativeLayout header;
        private TextView regionName;
        private TextView nationCount;
        private ImageView flag;

        private TextView waDelegate;
        private TextView founder;

        private LinearLayout factbookHolder;
        private HtmlTextView factbook;

        private TextView tags;

        private LinearLayout visitButton;
        private TextView visitText;

        private View.OnClickListener regionOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regionData != null) {
                    SparkleHelper.startExploring(context,
                            SparkleHelper.getIdFromName(regionData.name),
                            ExploreActivity.EXPLORE_REGION);
                }
            }
        };

        public FeaturedRegionCard(View itemView) {
            super(itemView);
            header = (RelativeLayout) itemView.findViewById(R.id.card_world_featured_header_container);
            regionName = (TextView) itemView.findViewById(R.id.card_world_featured_region_name);
            nationCount = (TextView) itemView.findViewById(R.id.card_world_featured_nation_count);
            flag = (ImageView) itemView.findViewById(R.id.card_world_featured_flag);

            waDelegate = (TextView) itemView.findViewById(R.id.card_world_featured_wa);
            founder = (TextView) itemView.findViewById(R.id.card_world_featured_founder);

            factbookHolder = (LinearLayout) itemView.findViewById(R.id.card_world_featured_factbook_holder);
            factbook = (HtmlTextView) itemView.findViewById(R.id.card_world_featured_factbook);

            tags = (TextView) itemView.findViewById(R.id.card_world_featured_tags);

            visitButton = (LinearLayout) itemView.findViewById(R.id.card_world_featured_region_button_holder);
            visitText = (TextView) itemView.findViewById(R.id.card_world_featured_region_button_name);
        }

        public void init(BaseRegion r) {
            regionData = r;
            header.setOnClickListener(regionOnClick);
            regionName.setText(regionData.name);
            nationCount.setText(String.format(Locale.US,
                    context.getString(R.string.val_currency),
                    SparkleHelper.getPrettifiedNumber(regionData.numNations),
                    English.plural(context.getString(R.string.region_pop), regionData.numNations)));
            if (regionData.flagURL != null) {
                flag.setVisibility(View.VISIBLE);
                DashHelper.getInstance(context).loadImage(regionData.flagURL, flag, false);
            } else {
                flag.setVisibility(View.GONE);
            }

            RegionOverviewRecyclerAdapter.initWaDelegate(context, waDelegate, regionData.delegate, regionData.delegateVotes);
            RegionOverviewRecyclerAdapter.initFounder(context, founder, regionData.founder, regionData.founded);

            if (regionData.factbook != null) {
                SparkleHelper.setBbCodeFormatting(context, factbook, regionData.factbook, fragmentManager);
                factbookHolder.setVisibility(View.VISIBLE);
            }
            else {
                factbookHolder.setVisibility(View.GONE);
            }

            String tagCombine = SparkleHelper.joinStringList(regionData.tags, ", ");
            tags.setText(tagCombine);

            visitButton.setOnClickListener(regionOnClick);
            visitText.setText(String.format(Locale.US, context.getString(R.string.telegrams_region_explore), regionData.name));
        }
    }

    // Breaking news
    public class BreakingNewsCard extends RecyclerView.ViewHolder {

        private List<Event> newsItems;

        private LinearLayout newsHolder;
        private LayoutInflater inflater;

        public BreakingNewsCard(View itemView) {
            super(itemView);
            newsHolder = (LinearLayout) itemView.findViewById(R.id.card_world_breaking_news_holder);
        }

        public void init(World w) {
            newsItems = w.happenings;

            inflater = LayoutInflater.from(context);
            newsHolder.removeAllViews();
            int index = 0;
            for (Event e : newsItems) {
                View newsItemView = inflater.inflate(R.layout.view_world_breaking_news_entry, null);
                HtmlTextView newsContent = (HtmlTextView) newsItemView.findViewById(R.id.card_world_breaking_news_content);
                SparkleHelper.setHappeningsFormatting(context, newsContent, e.content);

                if (++index >= newsItems.size()) {
                    newsItemView.findViewById(R.id.view_divider).setVisibility(View.GONE);
                }

                newsHolder.addView(newsItemView);
            }
        }
    }

    // Featured census
    public class FeaturedCensusCard extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CensusDetailedRank featuredCensus;

        private TextView censusTitle;
        private TextView censusUnit;
        private TextView censusScore;
        private ImageView censusBg;

        public FeaturedCensusCard(View itemView) {
            super(itemView);
            censusTitle = (TextView) itemView.findViewById(R.id.card_world_featured_census);
            censusUnit = (TextView) itemView.findViewById(R.id.card_world_featured_census_unit);
            censusScore = (TextView) itemView.findViewById(R.id.card_world_featured_census_score);
            censusBg = (ImageView) itemView.findViewById(R.id.card_world_census_background);
            itemView.setOnClickListener(this);
        }

        public void init(CensusDetailedRank census) {
            // Forces card to span across columns
            RaraHelper.setViewHolderFullSpan(itemView);

            featuredCensus = census;

            String[] censusType = SparkleHelper.getCensusScale(WORLD_CENSUS_ITEMS, featuredCensus.id);

            censusTitle.setText(censusType[0]);
            censusUnit.setText(censusType[1]);
            censusScore.setText(String.format(Locale.US,
                    context.getString(R.string.world_census_score),
                    SparkleHelper.getPrettifiedShortSuffixedNumber(context, featuredCensus.score)));

            String bgUrl = Nation.getBannerURL(censusType[2]);
            DashHelper.getInstance(context).loadImage(bgUrl, censusBg, false);
        }

        @Override
        public void onClick(View view) {
            SparkleHelper.startTrends(context, null, TrendsActivity.TREND_WORLD, featuredCensus.id);
        }
    }

    // Regular world census data
    public class WorldCensusCard extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CensusDetailedRank censusData;

        private CardView censusCard;
        private TextView scale;
        private TextView unit;
        private TextView score;

        public WorldCensusCard(View itemView) {
            super(itemView);
            censusCard = (CardView) itemView.findViewById(R.id.card_census_delta_main);
            scale = (TextView) itemView.findViewById(R.id.card_delta_name);
            unit = (TextView) itemView.findViewById(R.id.card_delta_unit);
            score = (TextView) itemView.findViewById(R.id.card_delta_value);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            SparkleHelper.startTrends(context, null, TrendsActivity.TREND_WORLD, censusData.id);
        }

        public void init(CensusDetailedRank census) {
            censusData = census;

            censusCard.setCardBackgroundColor(RaraHelper.getThemeCardColour(context));

            String[] censusType = SparkleHelper.getCensusScale(WORLD_CENSUS_ITEMS, censusData.id);

            scale.setText(censusType[0]);
            unit.setText(censusType[1]);
            score.setText(SparkleHelper.getPrettifiedShortSuffixedNumber(context, censusData.score));

            scale.setTextColor(RaraHelper.getThemeLinkColour(context));
            unit.setTextColor(RaraHelper.getThemeLinkColour(context));
            score.setTextColor(RaraHelper.getThemeLinkColour(context));
        }
    }
}
