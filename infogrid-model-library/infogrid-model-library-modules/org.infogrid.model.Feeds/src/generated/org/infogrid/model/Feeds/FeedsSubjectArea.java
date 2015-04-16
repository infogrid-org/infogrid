//
// This file is part of InfoGrid(tm). You may not use this file except in
// compliance with the InfoGrid license. The InfoGrid license and important
// disclaimers are contained in the file LICENSE.InfoGrid.txt that you should
// have received with InfoGrid. If you have not received LICENSE.InfoGrid.txt
// or you do not consent to all aspects of the license and the disclaimers,
// no license is granted; do not use this file.
// 
// For more information about InfoGrid go to http://infogrid.org/
//
// Copyright 1998-2013 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

//
// This file has been generated AUTOMATICALLY. DO NOT MODIFY.
// on Thu, 2015-04-16 22:41:00 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Feeds;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Collects the MeshTypes in this Subject Area for simpler access.</p>
  */
public abstract class FeedsSubjectArea
{
    /**
      * The SubjectArea itself.
      */
    public static final SubjectArea _SA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Feeds" );

    /**
      * A web feed such as an RSS or Atom feed.
      */
    public static final EntityType FEED = org.infogrid.model.Feeds.Feed._TYPE;

    /**
      * The feed\'s title
      */
    public static final PropertyType FEED_TITLE = org.infogrid.model.Feeds.Feed.TITLE;
    public static final org.infogrid.model.primitives.BlobDataType FEED_TITLE_type = (org.infogrid.model.primitives.BlobDataType) FEED_TITLE.getDataType();

    /**
      * The feed\'s description
      */
    public static final PropertyType FEED_DESCRIPTION = org.infogrid.model.Feeds.Feed.DESCRIPTION;
    public static final org.infogrid.model.primitives.BlobDataType FEED_DESCRIPTION_type = (org.infogrid.model.primitives.BlobDataType) FEED_DESCRIPTION.getDataType();

    /**
      * A web feed in Atom format.
      */
    public static final EntityType ATOMFEED = org.infogrid.model.Feeds.AtomFeed._TYPE;

    /**
      * The feed\'s subtitle
      */
    public static final PropertyType ATOMFEED_SUBTITLE = org.infogrid.model.Feeds.AtomFeed.SUBTITLE;
    public static final org.infogrid.model.primitives.BlobDataType ATOMFEED_SUBTITLE_type = (org.infogrid.model.primitives.BlobDataType) ATOMFEED_SUBTITLE.getDataType();

    /**
      * A web feed in RSS format.
      */
    public static final EntityType RSSFEED = org.infogrid.model.Feeds.RssFeed._TYPE;

    /**
      * An entry, or item, in a web feed.
      */
    public static final EntityType FEEDITEM = org.infogrid.model.Feeds.FeedItem._TYPE;

    /**
      * The feed item\'s title
      */
    public static final PropertyType FEEDITEM_TITLE = org.infogrid.model.Feeds.FeedItem.TITLE;
    public static final org.infogrid.model.primitives.BlobDataType FEEDITEM_TITLE_type = (org.infogrid.model.primitives.BlobDataType) FEEDITEM_TITLE.getDataType();

    /**
      * The feed item\'s content.
      */
    public static final PropertyType FEEDITEM_CONTENT = org.infogrid.model.Feeds.FeedItem.CONTENT;
    public static final org.infogrid.model.primitives.BlobDataType FEEDITEM_CONTENT_type = (org.infogrid.model.primitives.BlobDataType) FEEDITEM_CONTENT.getDataType();

    /**
      * An entry in a web feed in Atom format.
      */
    public static final EntityType ATOMFEEDITEM = org.infogrid.model.Feeds.AtomFeedItem._TYPE;

    /**
      * The feed item\'s summary
      */
    public static final PropertyType ATOMFEEDITEM_SUMMARY = org.infogrid.model.Feeds.AtomFeedItem.SUMMARY;
    public static final org.infogrid.model.primitives.BlobDataType ATOMFEEDITEM_SUMMARY_type = (org.infogrid.model.primitives.BlobDataType) ATOMFEEDITEM_SUMMARY.getDataType();

    /**
      * An entry in a web feed in RSS format.
      */
    public static final EntityType RSSFEEDITEM = org.infogrid.model.Feeds.RssFeedItem._TYPE;

    /**
      * Relates a Web Feed to the items it contains.
      */
    public static final RelationshipType FEED_CONTAINS_FEEDITEM = ModelBaseSingleton.findRelationshipType( "org.infogrid.model.Feeds/Feed_Contains_FeedItem" );

}
