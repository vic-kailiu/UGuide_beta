package com.kai.uGuide.utils;

import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    private static final String ns = null;

    // We don't use namespaces

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;
        double lati = 0;
        double longi = 0;
        List<LatLng> place = new ArrayList<LatLng>();
        List<String> path = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("summary")) {
                summary = readSummary(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else if (name.equals("lati")) {
                lati = readLati(parser);
            } else if (name.equals("longi")) {
                longi =  readLongi(parser);
            } else if (name.equals("path")) {
                path =  readPath(parser);
//            }  else if (name.equals("route")) {
//                parser.require(XmlPullParser.START_TAG, ns, "route");
//                while (parser.next() != XmlPullParser.END_TAG) {
//                    if (parser.getEventType() != XmlPullParser.START_TAG) {
//                        continue;
//                    }
//                    String sub_name = parser.getName();
//                    //place = readPlace(parser);
//                    if (sub_name.equals("place")) {
//                        place = readPlace(parser);
//                    } else if (sub_name.equals("path")) {
//                        path = readPath(parser);
//                    } else {
//                        skip(parser);
//                    }
//                }
            } else {
                skip(parser);
            }
        }
        return new Entry(title, summary, link, lati, longi, place, path);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private double readLati(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lati");
        double lati = Double.parseDouble( readText(parser) );
        parser.require(XmlPullParser.END_TAG, ns, "lati");
        return lati;
    }

    private double readLongi(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "longi");
        double longi = Double.parseDouble( readText(parser) );
        parser.require(XmlPullParser.END_TAG, ns, "longi");
        return longi;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    private List<LatLng> readPlace(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "place");
        List<LatLng> spot = new ArrayList<LatLng>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            double lati, longi;
            parser.require(XmlPullParser.START_TAG, ns, "spot");

            parser.require(XmlPullParser.START_TAG, ns, "lati");
            lati = Double.parseDouble(readText(parser));
            parser.require(XmlPullParser.END_TAG, ns, "lati");

            parser.require(XmlPullParser.START_TAG, ns, "longi");
            longi = Double.parseDouble(readText(parser));
            parser.require(XmlPullParser.END_TAG, ns, "longi");

            LatLng sp = new LatLng(lati, longi);
            parser.require(XmlPullParser.END_TAG, ns, "spot");
            spot.add(sp);
        }

        parser.require(XmlPullParser.END_TAG, ns, "place");
        return spot;
    }

    private List<String> readPath(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "path");
        List<String> path = new ArrayList<String>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            parser.require(XmlPullParser.START_TAG, ns, "poly");
            String poly = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "poly");
            path.add(poly);
        }

        parser.require(XmlPullParser.END_TAG, ns, "path");
        return path;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;
        public final Double lati;
        public final Double longti;
        public final List<LatLng> place;
        public final List<String> path;

        private Entry(String title, String summary, String link, Double Lati, Double Longti, List<LatLng> place,  List<String> path) {
            this.title = title;
            this.summary = summary;
            this.link = link;
            this.lati = Lati;
            this.longti = Longti;
            this.place = place;
            this.path = path;
        }
    }
}

