package com.kai.uGuide.ui.adapter;

import com.kai.uGuide.R;

public class Result {
    public static final String[] TITLES = {"Attractions1", "Tours1", "Food1", "Hotels1", "Events1"};

    public static final ListItem[] Attractions = {
            new ListItem(R.drawable.t_sentosa,              R.drawable.sentosa,              "111Sentosa Island"),
            new ListItem(R.drawable.t_universal_studios,    R.drawable.universal_studio,     "111Universal Studios Singapore"),
            new ListItem(R.drawable.t_merlion,              R.drawable.merlion,              "Merlion Park"),
            new ListItem(R.drawable.t_orchard,              R.drawable.orchard,              "Orchard Road"),
            new ListItem(R.drawable.t_marina_bay_sands,     R.drawable.marina_bay,           "Marina Bay"),
            new ListItem(R.drawable.t_gardens_by_the_bay,   R.drawable.gardens_by_the_bay,   "Gardens by the Bay")
    };

    public static final ListItem[] Tours = {
            new ListItem(R.drawable.t_book,    R.drawable.china_town,      "Heritage Trail"),
            new ListItem(R.drawable.t_food,    R.drawable.chilli_crab,     "Foodie's Route"),
            new ListItem(R.drawable.t_shop,    R.drawable.orchard,         "Shopping Lover"),
    };

    public static final ListItem[] Hotels = {
            new ListItem(R.drawable.t_sentosa,              R.drawable.sentosa,        "Sentosa Island"),
            new ListItem(R.drawable.t_marina_bay_sands,     R.drawable.marina_bay,     "Marina Bay"),
            new ListItem(R.drawable.t_chinatown,            R.drawable.china_town,     "Chinatown"),
            new ListItem(R.drawable.t_orchard,              R.drawable.orchard,        "Orchard Road"),
            new ListItem(R.drawable.t_budha,                R.drawable.little_india,   "Little India"),
    };

    public static final ListItem[] Foods = {
            new ListItem(R.drawable.t_crab,              R.drawable.chilli_crab,     "Chili Crab"),
            new ListItem(R.drawable.t_soup,    R.drawable.bak_kut_eh,      "Bak Kut Teh"),
            new ListItem(R.drawable.t_dish,              R.drawable.chicken_rice,    "Chicken Rice"),
            new ListItem(R.drawable.t_noodle,              R.drawable.laksa,           "Laksa"),
            new ListItem(R.drawable.t_meat,     R.drawable.satay,           "Satay"),
    };

    public static final ListItem[] Events = {
            new ListItem(R.drawable.t_cat,           R.drawable.cat,            "Cats"),
            new ListItem(R.drawable.t_ballet,        R.drawable.swan_lake,      "Swan Lake"),
            new ListItem(R.drawable.t_beauty_beast,  R.drawable.beauty_beast,   "Beauty and the Beast"),
            new ListItem(R.drawable.t_jay,           R.drawable.jay,            "OPUS 2 JAY"),
            new ListItem(R.drawable.t_mona_lisa,     R.drawable.mona_lisa,      "'Earlier Mona Lisa'"),
            new ListItem(R.drawable.t_peter_pan,     R.drawable.peter_pan,      "Peter Pan, the Never Ending Story")
    };
}
