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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util.text;

import java.util.HashMap;
import org.infogrid.util.Identifier;

/**
 * Helps find a StringRepresentationDirectory. The found StringRepresentationDirectory
 * is automatically initialized with a minimum set of entries. Applications may
 * set a different StringRepresentationDirectory if they like.
 */
public class StringRepresentationDirectorySingleton
        extends
            SimpleStringRepresentationDirectory
{
    /**
     * Private constructor, use static singleton methods.
     */
    protected StringRepresentationDirectorySingleton()
    {
        super();
    }

    /**
     * Obtain the singleton StringRepresentationDirectory.
     *
     * @return the StringRepresentationDirectory
     */
    public static StringRepresentationDirectorySingleton getSingleton()
    {
        if( theSingleton == null ) {
            // never mind threads
            instantiateDefaultSingleton();
        }
        return theSingleton;
    }

    /**
     * Set a different StringRepresentationDirectory as the singleton.
     *
     * @param newValue the new value for the singleton
     */
    public static void setSingleton(
            StringRepresentationDirectorySingleton newValue )
    {
        theSingleton = newValue;
    }

    /**
     * Create the default singleton.
     *
     * @return the created default singleton
     */
    protected static StringRepresentationDirectory instantiateDefaultSingleton()
    {
        HashMap<String,Stringifier<? extends Object>> plainMap = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> htmlMap  = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> urlMap   = new HashMap<String,Stringifier<? extends Object>>();

        plainMap.put(   "int",            LongStringifier.create() );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "int2",           LongStringifier.create( 2 ) );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "int3",           LongStringifier.create( 3 ) );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "int4",           LongStringifier.create( 4 ) );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "hex",            LongStringifier.create( -1, 16 ) );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "hex2",           LongStringifier.create( 2, 16 ) );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "hex4",           LongStringifier.create( 4, 16 ) );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "float",          FloatStringifier.create() );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "double",         DoubleStringifier.create() );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "string",         StringStringifier.create() );
        htmlMap.put(    "string",         HtmlifyingDelegatingStringifier.create( StringStringifier.create() ));
        // url:  same as plain

        plainMap.put(   "verbatim",       StringStringifier.create() );
        htmlMap.put(    "verbatim",       StringStringifier.create() ); // don't Htmlify
        // url:  same as plain

        plainMap.put(   "stacktrace",     StacktraceStringifier.create() );
        htmlMap.put(    "stacktrace",     HtmlifyingDelegatingStringifier.create( StacktraceStringifier.create() ));
        // url:  same as plain

        plainMap.put(   "urlappend",      AppendToUrlStringifier.create() );
        htmlMap.put(    "urlappend",      HtmlifyingDelegatingStringifier.create( AppendToUrlStringifier.create() ));
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "urlArgument",    ToValidUrlArgumentStringifier.create( StringStringifier.create() ) );
        htmlMap.put(    "urlArgument",    HtmlifyingDelegatingStringifier.create( ToValidUrlArgumentStringifier.create( StringStringifier.create() )));
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "id",             IdentifierStringifier.create() );
        htmlMap.put(    "id",             HtmlifyingDelegatingStringifier.create( IdentifierStringifier.create() ));
        // url:  same as plain

        plainMap.put(   "idAsUrlArgument", ToValidUrlArgumentStringifier.create( IdentifierStringifier.create() ));
        htmlMap.put(    "idAsUrlArgument", HtmlifyingDelegatingStringifier.create( ToValidUrlArgumentStringifier.create( IdentifierStringifier.create() )));
        // url:  same as plain

        plainMap.put(   "hasIdAsUrlArgument", ToValidUrlArgumentStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() )));
        htmlMap.put(    "hasIdAsUrlArgument", HtmlifyingDelegatingStringifier.create( ToValidUrlArgumentStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() ))));
        // url:  same as plain

        plainMap.put(   "stringarray",    ArrayStringifier.create( StringStringifier.create(), ", " ));
        htmlMap.put(    "stringarray",    ArrayStringifier.create( HtmlifyingDelegatingStringifier.create( StringStringifier.create() ), ", " ));
        // url:  same as plain

        plainMap.put(   "idarray",        ArrayStringifier.create( IdentifierStringifier.create(), ", " ));
        htmlMap.put(    "idarray",        ArrayStringifier.create( HtmlifyingDelegatingStringifier.create( IdentifierStringifier.create() ), ", " ));
        // url:  same as plain

        plainMap.put(   "hasid",          HasIdentifierStringifier.create( IdentifierStringifier.create() ));
        htmlMap.put(    "hasid",          HtmlifyingDelegatingStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() )));
        // url:  same as plain

        plainMap.put(   "hasidarray",     ArrayStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() ), ", " ));
        htmlMap.put(    "hasidarray",     HtmlifyingDelegatingStringifier.create( ArrayStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() ), ", " )));
        // url:  same as plain

        plainMap.put(   "class",          ClassStringifier.create() );
        // html: same as plain
        // url:  same as plain

        plainMap.put(   "list",           ListStringifier.create( ", ", StringStringifier.create() ));
        htmlMap.put(    "list",           ListStringifier.create( "<li>", "</li>\n<li>", "</li>", "", StringStringifier.create() ));
        // url:  same as plain

        plainMap.put(   "javascriptescaped", JavaScriptStringStringifier.create() );

        plainMap.put(   "as-entered",     AsEnteredStringifier.create() );
        htmlMap.put(    "as-entered",     HtmlifyingDelegatingStringifier.create( AsEnteredStringifier.create() ));
        // url:  same as plain

        plainMap.put(   "as-entered-array", ArrayStringifier.create( AsEnteredStringifier.create(), ", " ));
        htmlMap.put(    "as-entered-array", HtmlifyingDelegatingStringifier.create( ArrayStringifier.create( AsEnteredStringifier.create(), ", " )));

        // html: same as plain
        // url:  same as plain
        

        theSingleton = new StringRepresentationDirectorySingleton(); // not the factory method here

        SimpleStringRepresentation plain = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_PLAIN_NAME,
                plainMap );

        @SuppressWarnings("unchecked")
        SimpleStringRepresentation editPlain = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.EDIT_TEXT_PLAIN_NAME,
                (HashMap<String,Stringifier<? extends Object>>) plainMap.clone() );

        SimpleStringRepresentation html = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_HTML_NAME,
                htmlMap,
                plain );

        @SuppressWarnings("unchecked")
        SimpleStringRepresentation editHtml = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.EDIT_TEXT_HTML_NAME,
                (HashMap<String,Stringifier<? extends Object>>) htmlMap.clone(),
                plain );

        SimpleStringRepresentation url = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_URL_NAME,
                urlMap,
                plain );

        theSingleton.put(     plain.getName(), plain );
        theSingleton.put( editPlain.getName(), editPlain );
        theSingleton.put(      html.getName(), html );
        theSingleton.put(  editHtml.getName(), editHtml );
        theSingleton.put(       url.getName(), url );

        theSingleton.setFallback( plain );

        return theSingleton;
    }

    /**
     * The current singleton, initialized to a minimum set of defaults when used for the first time.
     */
    protected static StringRepresentationDirectorySingleton theSingleton;
}
