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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util.text;

import java.util.HashMap;

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
        HashMap<String,Stringifier<? extends Object>> plainMap            = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> editPlainMap        = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> htmlMap             = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> editHtmlMap         = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> htmlPasswordMap     = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> editHtmlPasswordMap = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> urlMap              = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> svgMap              = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> httpPostMap         = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> javaMap             = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> javascriptMap       = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> javadocMap          = new HashMap<String,Stringifier<? extends Object>>();
        HashMap<String,Stringifier<? extends Object>> jsonMap             = new HashMap<String,Stringifier<? extends Object>>();

        plainMap.put(   "int",            LongStringifier.create() );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "int2",           LongStringifier.create( 2 ) );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "int3",           LongStringifier.create( 3 ) );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "int4",           LongStringifier.create( 4 ) );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "hex",            LongStringifier.create( -1, 16 ) );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "hex2",           LongStringifier.create( 2, 16 ) );
        // html:       same as plain
        // url:        same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "hex4",           LongStringifier.create( 4, 16 ) );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "intwithmultiplier", LongStringifierWithMultiplier.create() );
        
        plainMap.put(   "float",          FloatStringifier.create() );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        javaMap.put(    "float",          JavaFloatStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "double",         DoubleStringifier.create() );
        // html:       same as plain
        // url:        same as plain
        // httpPost:   same as plain
        javaMap.put(    "double",         JavaDoubleStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "string",         StringStringifier.create() );
        htmlMap.put(    "string",         HtmlifyingDelegatingStringifier.create( StringStringifier.create() ));
        // url:  same as plain
        // httpPost:   same as plain
        javaMap.put(    "string",         JavaStringStringifier.create() );
        // javascript: same as java
        javadocMap.put( "string",         JavadocDelegatingStringifier.create( StringStringifier.create() ));
        // json:       same as javascript

        plainMap.put(   "verbatim",       StringStringifier.create() );
        // html:       same as plain -- don't Htmlify
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "htmlsource",     StringStringifier.create() );
        htmlMap.put(    "htmlsource",     HtmlifyingDelegatingStringifier.create( StringStringifier.create() ));
        editHtmlMap.put("htmlsource",     HtmlifyingDelegatingStringifier.create( StringStringifier.create() ));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "htmltextarea",     StringStringifier.create() );
        htmlMap.put(    "htmltextarea",     HtmlifyingDelegatingStringifier.create( StringStringifier.create(), "textarea" ));
        editHtmlMap.put("htmltextarea",     HtmlifyingDelegatingStringifier.create( StringStringifier.create(), "textarea" ));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        
                
        plainMap.put(   "stacktrace",     StacktraceStringifier.create() );
        htmlMap.put(    "stacktrace",     HtmlifyingDelegatingStringifier.create( StacktraceStringifier.create() ));
        urlMap.put(     "stacktrace",     InvalidStringifier.create() );
        // httpPost:   same as plain
        javaMap.put(    "stacktrace",     InvalidStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "urlappend",      AppendToUrlStringifier.create() );
        htmlMap.put(    "urlappend",      HtmlifyingDelegatingStringifier.create( AppendToUrlStringifier.create() ));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "urlArgument",    ToValidUrlArgumentStringifier.create( StringStringifier.create() ) );
        htmlMap.put(    "urlArgument",    HtmlifyingDelegatingStringifier.create( ToValidUrlArgumentStringifier.create( StringStringifier.create() )));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "id",             IdentifierStringifier.create() );
        htmlMap.put(    "id",             HtmlifyingDelegatingStringifier.create( IdentifierStringifier.create() ));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "fullid",         IdentifierStringifier.create( false ));
        htmlMap.put(    "fullid",         HtmlifyingDelegatingStringifier.create( IdentifierStringifier.create( false ) ));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "idAsUrlArgument", ToValidUrlArgumentStringifier.create( IdentifierStringifier.create() ));
        htmlMap.put(    "idAsUrlArgument", HtmlifyingDelegatingStringifier.create( ToValidUrlArgumentStringifier.create( IdentifierStringifier.create() )));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "hasIdAsUrlArgument", ToValidUrlArgumentStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() )));
        htmlMap.put(    "hasIdAsUrlArgument", HtmlifyingDelegatingStringifier.create( ToValidUrlArgumentStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() ))));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "stringarray",    ArrayStringifier.create( ", ", StringStringifier.create() ));
        htmlMap.put(    "stringarray",    ArrayStringifier.create( "<li>", "</li>\n<li>", "</li>", "", HtmlifyingDelegatingStringifier.create( StringStringifier.create() ) ));
        urlMap.put(     "stringarray",    InvalidStringifier.create() );
        // httpPost:   same as plain
        javaMap.put(    "stringarray",    InvalidStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "idarray",        ArrayStringifier.create( ", ", IdentifierStringifier.create() ));
        htmlMap.put(    "idarray",        ArrayStringifier.create( "<li>", "</li>\n<li>", "</li>", "", HtmlifyingDelegatingStringifier.create( IdentifierStringifier.create() ) ));
        urlMap.put(     "idarray",        InvalidStringifier.create() );
        // httpPost:   same as plain
        javaMap.put(    "idarray",        InvalidStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "hasid",          HasIdentifierStringifier.create( IdentifierStringifier.create() ));
        htmlMap.put(    "hasid",          HtmlifyingDelegatingStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() )));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "fullhasid",      HasIdentifierStringifier.create( IdentifierStringifier.create( false ) ));
        htmlMap.put(    "fullhasid",      HtmlifyingDelegatingStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create( false ) )));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "hasidarray",     ArrayStringifier.create( ", ", HasIdentifierStringifier.create( IdentifierStringifier.create() ) ));
        htmlMap.put(    "hasidarray",     ArrayStringifier.create( "<li>", "</li>\n<li>", "</li>", "", HtmlifyingDelegatingStringifier.create( HasIdentifierStringifier.create( IdentifierStringifier.create() ) )));
        urlMap.put(     "hasidarray",     InvalidStringifier.create() );
        // httpPost:   same as plain
        javaMap.put(    "hasidarray",     InvalidStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "class",          ClassStringifier.create() );
        htmlMap.put(    "class",          PrePostfixDelegatingStringifier.create( "<code>", "</code>", ClassStringifier.create() ));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "list",           ListStringifier.create( ", ", StringStringifier.create() ));
        htmlMap.put(    "list",           ListStringifier.create( "<li>", "</li>\n<li>", "</li>", "", StringStringifier.create() ));
        urlMap.put(     "idarray",        InvalidStringifier.create() );
        // httpPost:   same as plain
        javaMap.put(    "idarray",        InvalidStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "as-entered",     AsEnteredStringifier.create() );
        htmlMap.put(    "as-entered",     HtmlifyingDelegatingStringifier.create( AsEnteredStringifier.create() ));
        // url:        same as plain
        // httpPost:   same as plain
        // java:       same as plain
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        plainMap.put(   "as-entered-array", ArrayStringifier.create( ", ", AsEnteredStringifier.create() ));
        htmlMap.put(    "as-entered-array", HtmlifyingDelegatingStringifier.create( ArrayStringifier.create( ", ", AsEnteredStringifier.create() )));
        urlMap.put(     "as-entered-array", InvalidStringifier.create() );
        // httpPost:   same as plain
        javaMap.put(    "as-entered-array", InvalidStringifier.create() );
        // javascript: same as java
        // javadoc:    same as java
        // json:       same as javascript

        editHtmlMap.put( "escapequotestring", EscapeQuoteStringStringifier.create() );
        
        theSingleton = new StringRepresentationDirectorySingleton(); // not the factory method here

        SimpleStringRepresentation plain = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_PLAIN_NAME,
                plainMap );

        SimpleStringRepresentation editPlain = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.EDIT_TEXT_PLAIN_NAME,
                editPlainMap,
                plain );

        SimpleStringRepresentation html = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_HTML_NAME,
                htmlMap,
                plain );

        SimpleStringRepresentation editHtml = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.EDIT_TEXT_HTML_NAME,
                editHtmlMap,
                editPlain );

        SimpleStringRepresentation htmlPassword = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_HTML_PASSWORD_NAME,
                htmlPasswordMap,
                html );

        SimpleStringRepresentation editHtmlPassword = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.EDIT_TEXT_HTML_PASSWORD_NAME,
                editHtmlPasswordMap,
                editHtml );

        SimpleStringRepresentation url = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_URL_NAME,
                urlMap,
                plain );

        SimpleStringRepresentation svg = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.SVG_PLUS_XML_NAME,
                svgMap,
                html );

        SimpleStringRepresentation httpPost = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_HTTP_POST_NAME,
                httpPostMap,
                plain );

        SimpleStringRepresentation java = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_JAVA_NAME,
                javaMap,
                plain );

        SimpleStringRepresentation javascript = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_JAVASCRIPT_NAME,
                javascriptMap,
                java );

        SimpleStringRepresentation javadoc = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_JAVADOC_NAME,
                javaMap,
                java );

        SimpleStringRepresentation json = SimpleStringRepresentation.create(
                theSingleton,
                StringRepresentationDirectory.TEXT_JSON_NAME,
                jsonMap,
                javascript );

        theSingleton.put(              plain.getName(), plain );
        theSingleton.put(          editPlain.getName(), editPlain );
        theSingleton.put(               html.getName(), html );
        theSingleton.put(           editHtml.getName(), editHtml );
        theSingleton.put(       htmlPassword.getName(), htmlPassword );
        theSingleton.put(   editHtmlPassword.getName(), editHtmlPassword );
        theSingleton.put(                url.getName(), url );
        theSingleton.put(                svg.getName(), svg );
        theSingleton.put(           httpPost.getName(), httpPost );
        theSingleton.put(               java.getName(), java );
        theSingleton.put(         javascript.getName(), javascript );
        theSingleton.put(            javadoc.getName(), javadoc );
        theSingleton.put(               json.getName(), json );

        theSingleton.setFallback( plain );

        return theSingleton;
    }

    /**
     * The current singleton, initialized to a minimum set of defaults when used for the first time.
     */
    protected static StringRepresentationDirectorySingleton theSingleton;
}
