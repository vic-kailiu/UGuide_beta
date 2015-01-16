package com.kai.uGuide.ui.adapter;

import com.kai.uGuide.R;

public class Result {
    public static final String[] TITLES = {"Attractions", "Tours", "Food", "Hotels", "Events"};

    public static final ListItem[] Attractions = {
            new ListItem(R.drawable.t_sentosa,              R.drawable.sentosa,              "Sentosa Island"),
            new ListItem(R.drawable.t_universal_studios,    R.drawable.universal_studio,     "Universal Studios Singapore"),
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

    public static final String[] TMerlion = {
            "The Merlion is a traditional creature in western heraldry that depicts a monster with a lion head and a body of a fish.The Merlion is the national personification of Singapore. Its name combines mer meaning the sea and lion. The fish body represents Singapore's origin as a fishing village when it was called Temasek, which means sea town in Javanese. The lion head represents Singapore's original name Singapura meaning lion city or kota singa.",
            "The body symbolises Singapore's humble beginnings as a fishing village when it was called Temasek, meaning 'sea town' in Old Javanese. Its head represents Singapore’s original name, Singapura, or ‘lion city’ in Malay. Today, you can glimpse this legend at Merlion Park. Spouting water from its mouth, the Merlion statue stands tall at 8.6 metres and weighs 70 tonnes. This icon is a ‘must-see’ for tourists visiting Singapore, similar to other significant landmarks around the world. Built by local craftsman Lim Nang Seng, it was unveiled on 15 September 1972 by then Prime Minister Lee Kuan Yew at the mouth of the Singapore River, to welcome all visitors to Singapore."
    };

    public static final String[] TTMerlion = {
            "National Personification of Singapore", "Origin of Merlion"
    };

    public static final String[] TMarinaBaySands = {
            "Marina Bay Sands is a destination for those who appreciate luxury. An integrated resort notable for transforming Singapore’s city skyline, it comprises three 55-storey towers of extravagant hotel rooms and luxury suites equipped with personal butler service. In addition, its architecture is made complete with the Sands SkyPark which crowns the three towers.",
            "Marina Bay Sands is an integrated resort fronting Marina Bay in Singapore. Developed by Las Vegas Sands (LVS), it is the world's second most expensive building, at US$ 5.5 billion, including the cost of the prime land.Marina Bay Sands is situated on 15.5 hectares of land with the gross floor area of 581,000 square metres. The resort opened on 27 April 2010. The property has a hotel, convention and exhibition facilities, theatres, entertainment venues, retailers and restaurants."
    };

    public static final String[] TTMarinaBaySands = {
            "Destination for those who appreciate luxury",
            "World's second most expensive building"
    };

    public static final String[] TEsplanade = {
            "The Esplanade is a waterfront location just north of the mouth of the Singapore River in downtown Singapore. It is primarily occupied by the Esplanade Park, and was the venue where one of Singapore's largest congregation of satay outlets until their relocation to Clarke Quay as a result of the construction of a major performance arts venue, the Esplanade - Theatres on the Bay, which took its name from this location. There are few buildings in Singapore as eye-catching as Esplanade, a world-class performing arts centre made up of two rounded frames fitted with over 7,000 triangle glass sunshades. Locals have dubbed them the Durian, as the twin structures resemble the spiky tropical fruit that is unique to this part of the world. And as with the strong, some might say pungent, smelling national fruit of Singapore, every Singaporean has an opinion about the bold design of Esplanade. Love it or not, the space has become synonymous with the country, a funky complement to the symmetry of Marina Bay Sands and the Singapore Flyer that sit near by. The Esplanade is one of the busiest, and architecturally captivating, performing arts centres in the region. Designed by leading local firm DP Architects, the space features a 1,600-seat Concert Hall where the Singapore Symphony Orchestra and and other local, regional and international music acts regularly perform.",
            "Foo"
    };

    public static final String[] TTEsplanade = {
            "The Esplanade", "Foo"
    };
}
