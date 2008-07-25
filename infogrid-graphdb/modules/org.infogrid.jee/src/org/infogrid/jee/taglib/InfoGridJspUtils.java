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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.taglib;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.ModelPrimitivesStringRepresentation;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.modelbase.MeshTypeNotFoundException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.text.StringRepresentation;

/**
 * Collection of JSP utility methods similar to those provided by Apache Struts,
 * for use with InfoGrid.
 */
public abstract class InfoGridJspUtils
{
    /**
     * Private useless constructor to keep JavaDoc straight.
     */
    protected InfoGridJspUtils() {}

    /**
     * Locate and return the specified Object, from an optionally specified
     * scope, in the specified page context. If no such Object is found,
     * return <code>null</code> instead.
     *
     * @param pageContext the PageContext to be searched
     * @param name name of the bean to be retrieved
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>PageContext.findAttribute()</code> instead
     * @return Object in the specified page context
     * @exception JspException if an invalid scope name is requested
     */
    public static Object simpleLookup(
            PageContext pageContext,
            String      name,
            String      scopeName )
        throws
            JspException
    {
        if( scopeName == null ) {
            return pageContext.findAttribute( name );
        } else {
            int scope = getScope( scopeName );
            return pageContext.getAttribute( name, scope );
        }
    }

    /**
     * Locate and return the specified property of the specified Object, from
     * an optionally specified scope, in the specified page context. If no such Object
     * is found, or the found Object does not have such a property,
     * return <code>null</code> instead.
     *
     * @param pageContext the PageContext to be searched
     * @param name Name of the bean to be retrieved
     * @param propertyName Name of the property to be retrieved, or
     *        <code>null</code> to retrieve the bean itself
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>PageContext.findAttribute()</code> instead
     * @return property of specified JavaBean
     * @exception JspException if an invalid scope name is requested
     */
    public static Object simpleLookup(
            PageContext pageContext,
            String      name,
            String      propertyName,
            String      scopeName )
        throws
            JspException
    {
        Object obj = simpleLookup( pageContext, name, scopeName );
        if( obj == null ) {
            return null;
        }
        Object ret = getSimpleProperty( obj, propertyName );
        return ret;
    }
    
    /**
     * Locate and return the specified Object, from an optionally specified
     * scope, in the specified page context. If no such Object is found,
     * throw a <code>JspException</code>.
     *
     * @param pageContext the PageContext to be searched
     * @param name name of the bean to be retrieved
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>PageContext.findAttribute()</code> instead
     * @return Object in the specified page context
     * @exception JspException if an invalid scope name is requested, or the Object could not be found
     */
    public static Object simpleLookupOrThrow(
            PageContext pageContext,
            String      name,
            String      scopeName )
        throws
            JspException
    {
        Object obj = simpleLookup( pageContext, name, scopeName );
        if( obj == null ) {
            throw new JspException( "Could not find bean " + name + " in scope " + scopeName );
        }
        return obj;
    }
    
    /**
     * Locate and return the specified property of the specified Object, from
     * an optionally specified scope, in the specified page context. If no such Object
     * is found, or the found Object does not have such a property,
     * throw a <code>JspException</code>.
     *
     * @param pageContext the PageContext to be searched
     * @param name Name of the bean to be retrieved
     * @param propertyName Name of the property to be retrieved, or
     *        <code>null</code> to retrieve the bean itself
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>PageContext.findAttribute()</code> instead
     * @return property of specified JavaBean
     * @exception JspException if an invalid scope name is requested, the Object could not be found, or the property could not be found
     */
    public static Object simpleLookupOrThrow(
            PageContext pageContext,
            String      name,
            String      propertyName,
            String      scopeName )
        throws
            JspException
    {
        Object obj = simpleLookup( pageContext, name, scopeName );
        if( obj == null ) {
            throw new JspException( "Could not find bean " + name + " in scope " + scopeName );
        }
        Object ret = getSimplePropertyOrThrow( obj, propertyName );  // may throw
        
        return ret;
    }
    
    /**
     * Locate and return the specified Object, from an optionally specified
     * scope, in the specified page context.  If no such object is found,
     * return <code>null</code> instead. Unlike {@link #simpleLookup simpleLookup},
     * this method will attempt to parse <code>x.y.z</code> expressions.
     *
     * @param pageContext the PageContext to be searched
     * @param name name of the bean to be retrieved
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>PageContext.findAttribute()</code> instead
     * @return found Object, or null
     * @exception JspException if an invalid scope name is requested
     */
    public static Object nestedLookup(
            PageContext pageContext,
            String      name,
            String      scopeName )
        throws
            JspException
    {
        int index = name.indexOf( "." );
        String firstPart;
        String remainder;
        
        if( index >=0 ) {
            firstPart = name.substring( 0, index );
            remainder = name.substring( index+1 );
        } else {
            firstPart = name;
            remainder = null;
        }

        Object obj;
        if( scopeName == null ) {
            obj = pageContext.findAttribute( firstPart );
        } else {
            int scope = getScope( scopeName );
            obj = pageContext.getAttribute( firstPart, scope );
        }
        
        if( remainder == null || remainder.length() == 0 ) {
            return obj;
        } else {
            return getNestedProperty( obj, remainder );
        }
    }

    /**
     * Locate the specified Object, from an optionally specified
     * scope, in the specified page context, and return the value of the specified
     * property. If no such object is found,
     * return <code>null</code> instead. Unlike {@link #simpleLookup simpleLookup},
     * this method will attempt to parse <code>x.y.z</code> expressions.
     *
     * @param pageContext the PageContext to be searched
     * @param name name of the bean to be retrieved
     * @param propertyName name of the property to be retrieved
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>PageContext.findAttribute()</code> instead
     * @return found Object, or null
     * @exception JspException if an invalid scope name is requested
     */
    public static Object nestedLookup(
            PageContext pageContext,
            String      name,
            String      propertyName,
            String      scopeName )
        throws
            JspException
    {
        Object obj = nestedLookup( pageContext, name, scopeName );
        if( obj == null ) {
            return obj;
        }
        obj = getNestedProperty( obj, propertyName );
        return obj;
    }

    /**
     * Locate and return the specified Object, from an optionally specified
     * scope, in the specified page context. If no such object is found,
     * throw a <code>JspException</code>. Unlike {@link #simpleLookupOrThrow simpleLookupOrThrow},
     * this method will attempt to parse <code>x.y.z</code> expressions.
     *
     * @param pageContext the PageContext to be searched
     * @param name Name of the bean to be retrieved
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>findAttribute()</code> instead
     * @return found Object
     * @exception JspException if an invalid scope name is requested, or the Object could not be found
     */
    public static Object nestedLookupOrThrow(
            PageContext pageContext,
            String      name,
            String      scopeName )
        throws
            JspException
    {
        Object obj = nestedLookup( pageContext, name, scopeName );
        if( obj == null ) {
            throw new JspException( "Could not find bean " + name + " in scope " + scopeName );
        }
        
        return obj;
    }
    
    /**
     * Locate the specified Object, from an optionally specified
     * scope, in the specified page context, and return the value of the specified
     * property. If no such object is found,
     * throw a <code>JspException</code>. Unlike {@link #simpleLookup simpleLookup},
     * this method will attempt to parse <code>x.y.z</code> expressions.
     *
     * @param pageContext the PageContext to be searched
     * @param name name of the bean to be retrieved
     * @param propertyName name of the property to be retrieved
     * @param scopeName scope to be searched (page, request, session, application)
     *        or <code>null</code> to use <code>PageContext.findAttribute()</code> instead
     * @return found Object, or null
     * @exception JspException if an invalid scope name is requested
     */
    public static Object nestedLookupOrThrow(
            PageContext pageContext,
            String      name,
            String      propertyName,
            String      scopeName )
        throws
            JspException
    {
        Object obj = nestedLookup( pageContext, name, scopeName );
        if( obj == null ) {
            throw new JspException( "Could not find bean " + name + " in scope " + scopeName );
        }
        obj = getNestedProperty( obj, propertyName );
        return obj;
    }
    /**
     * <p>Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions. If no such object is found,
     * return <code>null</code> instead.</p>
     * 
     * <p>Unlike Apache's version of this method, this does not (yet?) support mapped or indexed properties.</p>
     *
     * @param obj object whose property is to be extracted
     * @param propertyName possibly nested name of the property to be extracted
     * @return the found value, or null
     * @throws IllegalArgumentException thrown if an illegal propertyName was given
     * @throws JspException thrown if the object or the object's property could not be found
     */
    public static Object getNestedProperty(
            Object obj,
            String propertyName )
        throws
            JspException
    {
        if( propertyName == null ) {
            throw new IllegalArgumentException( "Property name in property expression cannot be null" );
        }

        Object currentObj    = obj;
        String remainingName = propertyName;
        int index            = -1;

        while( true ) {
            if( currentObj == null ) {
                throw new IllegalArgumentException( "Object in property expression cannot be null" );
            }

            index = remainingName.indexOf( "." );

            String currentName;
            if( index > 0 ) {
                currentName = remainingName.substring( 0, index );
            } else {
                currentName = remainingName;
            }

            if( currentName.length() == 0 ) {
                throw new IllegalArgumentException( "Property in nested property expression cannot be null" );
            }

            currentObj = getSimpleProperty( currentObj, currentName );
            if( currentObj == null ) {
                return null;
            }

            if( index < 0 ) {
                break;
            }

            remainingName = remainingName.substring( index + 1 );
        }
        return currentObj;
    }

    /**
     * <p>Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions. If no such object is found,
     * throw a <code>JspException</code>.</p>
     * 
     * <p>Unlike Apache's version of this method, this does not (yet?) support mapped or indexed properties.</p>
     *
     * @param obj object whose property is to be extracted
     * @param propertyName possibly nested name of the property to be extracted
     * @return the found value
     * @throws IllegalArgumentException thrown if an illegal propertyName was given
     * @throws JspException thrown if the object or the object's property could not be found
     */
    public static Object getNestedPropertyOrThrow(
            Object obj,
            String propertyName )
        throws
            JspException
    {
        // this implementation copies the code from getNestedProperty() because
        // Exception stacktraces are more meaningful if they are passed on from subroutines

        if( propertyName == null ) {
            throw new IllegalArgumentException( "Property name in property expression cannot be null" );
        }

        Object currentObj    = obj;
        String remainingName = propertyName;
        int index            = -1;

        while( true ) {
            if( currentObj == null ) {
                throw new IllegalArgumentException( "Object in property expression cannot be null" );
            }

            index = remainingName.indexOf( "." );

            String currentName;
            if( index > 0 ) {
                currentName = remainingName.substring( 0, index );
            } else {
                currentName = remainingName;
            }

            if( currentName.length() == 0 ) {
                throw new IllegalArgumentException( "Property in nested property expression cannot be null" );
            }

            currentObj = getSimplePropertyOrThrow( currentObj, currentName );

            if( index < 0 ) {
                break;
            }

            remainingName = remainingName.substring( index + 1 );
        }
        return currentObj;
    }

    /**
     * <p>Return the value of the (non-nested) property of the specified
     * name, for the specified bean, with no type conversions. If no such object is found,
     * return <code>null</code> instead.</p>
     * 
     * @param obj object whose property is to be extracted
     * @param propertyName possibly nested name of the property to be extracted
     * @return the found value, or null
     * @throws NullPointerException if the given object was null
     * @throws IllegalArgumentException thrown if an illegal propertyName was given
     */
    public static Object getSimpleProperty(
            Object obj,
            String propertyName )
    {
        if( obj == null ) {
            throw new NullPointerException( "Object cannot be null" );
        }

        if( propertyName == null ) {
            throw new IllegalArgumentException( "Property name cannot be null" );
        }
        if( propertyName.length() == 0 ) {
            throw new IllegalArgumentException( "Property name cannot be empty" );
        }
        
        // try the getter method first. if that fails, try MeshObject-specific methods.
        
        String getterName = "get" + Character.toUpperCase( propertyName.charAt( 0 )) + propertyName.substring( 1 );

        try {
            Method getterMethod = obj.getClass().getMethod( getterName, (Class []) null );
            Object ret          = getterMethod.invoke( obj, (Object []) null );

            return ret;

        } catch( Throwable ex ) {
            // ignore
        }
        
        // If this is a MeshObject, attempt to do PropertyType simpleLookup
        if( obj instanceof MeshObject ) {
            MeshObject realObj = (MeshObject) obj;
            
            if( "timeCreated".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeCreated();
            } else if( "timeUpdated".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeUpdated();
            } else if( "timeRead".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeRead();
            } else if( "timeExpires".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeExpires();
            }
            
            try {
                Object ret = realObj.getPropertyValueByName( propertyName );
                return ret;

            } catch( MeshTypeNotFoundException ex ) {
                // this didn't work. Ignore and proceed as normal
            } catch( NotPermittedException ex ) {
                return "[access denied]";
            }
        }
        return null;
    }

    /**
     * <p>Return the value of the (non-nested) property of the specified
     * name, for the specified bean, with no type conversions. If no such object is found,
     * throw a <code>JspException</code>.</p>
     * 
     * @param obj object whose property is to be extracted
     * @param propertyName possibly nested name of the property to be extracted
     * @return the found value
     * @throws NullPointerException if the given object was null
     * @throws IllegalArgumentException thrown if an illegal propertyName was given
     * @throws JspException thrown if the object's property could not be found
     */
    public static Object getSimplePropertyOrThrow(
            Object obj,
            String propertyName )
        throws
            JspException
    {
        if( obj == null ) {
            throw new NullPointerException( "Object cannot be null" );
        }

        if( propertyName == null ) {
            throw new IllegalArgumentException( "Property name cannot be null" );
        }
        if( propertyName.length() == 0 ) {
            throw new IllegalArgumentException( "Property name cannot be empty" );
        }

        // If this is a MeshObject, attempt to do PropertyType simpleLookup
        if( obj instanceof MeshObject ) {
            MeshObject realObj = (MeshObject) obj;
            
            if( "timeCreated".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeCreated();
            } else if( "timeUpdated".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeUpdated();
            } else if( "timeRead".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeRead();
            } else if( "timeExpires".equalsIgnoreCase( propertyName )) {
                return realObj.getTimeExpires();
            }
            
            try {
                Object ret = realObj.getPropertyValueByName( propertyName );
                return ret;

            } catch( MeshTypeNotFoundException ex ) {
                // this didn't work. Ignore and proceed as normal
            } catch( NotPermittedException ex ) {
                return "[access denied]";
            }
        }
        
        // construct getter method name
        String getterName = "get" + Character.toUpperCase( propertyName.charAt( 0 )) + propertyName.substring( 1 );

        try {
            Method getterMethod = obj.getClass().getMethod( getterName, (Class []) null );
            Object ret          = getterMethod.invoke( obj, (Object []) null );

            return ret;

        } catch( Throwable ex ) {
            throw new JspException( "Cannot call getter method for property " + propertyName + " on object " + obj );
        }
    }
    
    /**
     * Find a PropertyType on a MeshObject by name.
     *
     * @param obj the MeshObject
     * @param name the name of the PropertyType
     * @return the found PropertyType
     * @throws NullPointerException thrown if the MeshObject or the name of the PropertyType were null
     */
    protected static PropertyType findPropertyType(
            MeshObject obj,
            String     name )
    {
        // tolerate lowercase first characters.
        
        if( obj == null ) {
            throw new NullPointerException( "MeshObject cannot be null" );
        } 
        if( name == null || name.length() == 0 ) {
            throw new NullPointerException( "PropertyType name cannot be empty" );
        } 
        char firstChar = name.charAt( 0 );
        String capitalizedName;
        if( Character.isUpperCase( firstChar )) {
            capitalizedName = name;
        } else {
            capitalizedName = new StringBuilder( name.length() ).append( Character.toUpperCase( firstChar )).append( name.substring( 1 )).toString();
        }

        PropertyType [] allTypes = obj.getAllPropertyTypes();
        for( PropertyType current : allTypes ) {
            if( current.getName().equals( capitalizedName )) {
                return current;
            }
        }
        return null;
    }

    /**
     * Find a PropertyType on a MeshObject by name.
     *
     * @param obj the MeshObject
     * @param name the name of the PropertyType
     * @return the found PropertyType
     * @throws NullPointerException thrown if the MeshObject or the name of the PropertyType were null
     * @throws JspException thrown if the PropertyType could not be found
     */
    protected static PropertyType findPropertyTypeOrThrow(
            MeshObject obj,
            String     name )
        throws
            JspException
    {
        PropertyType ret = findPropertyType( obj, name );
        if( ret == null ) {
            throw new JspException( "Could not find property " + name + " on MeshObject " + obj );
        }
        return ret;
    }

    /**
     * Converts the scope name into its corresponding PageContext constant value.
     *
     * @param scopeName Can be "page", "request", "session", or "application" in any
     * case.
     * @return The constant representing the scope (ie. PageContext.REQUEST_SCOPE).
     * @throws JspException thrown if the scopeName is not a valid name.
     */
    public static int getScope(
            String scopeName )
        throws
            JspException
    {
        Integer scope = scopes.get( scopeName.toLowerCase() );

        if( scope == null ) {
            throw new JspException( "Cannot find scope: " + scopeName );
        }

        return scope.intValue();
    }

    /**
     * Filter the specified string for characters that are senstive to
     * HTML interpreters, returning the string with these characters replaced
     * by the corresponding character entities.
     *
     * @param value The string to be filtered and returned
     * @return the filtered value.
     */
    public static String filter(
            String value )
    {
        if( value == null ) {
            return null;
        }

        char content[] = new char[ value.length() ];
        value.getChars( 0, value.length(), content, 0 );
        StringBuffer ret = new StringBuffer( content.length + content.length/5 + 5 );

        for( int i=0 ; i<content.length ; ++i ) {
            switch( content[i] ) {
                case '<':
                    ret.append("&lt;");
                    break;
                case '>':
                    ret.append("&gt;");
                    break;
                case '&':
                    ret.append("&amp;");
                    break;
                case '"':
                    ret.append("&quot;");
                    break;
                case '\'':
                    ret.append("&#39;");
                    break;
                default:
                    ret.append( content[i] );
            }
        }
        return ret.toString();
    }

    /**
     * Filter a URL so that unsuitable characters are escaped with the %xx syntax.
     *
     * @param value the URL
     * @return the filtered value
     */
    public static String filterUrl(
            String value )
    {
        if( value == null ) {
            return null;
        }

        char content[] = new char[ value.length() ];
        value.getChars( 0, value.length(), content, 0 );
        StringBuffer ret = new StringBuffer( content.length + content.length/5 + 5 );

        for( int i=0 ; i<content.length ; ++i ) {
            switch( content[i] ) {
                // FIXME: is this the right list?
                case '&':
                case '?':
                case '/':
                case ':':
                case '#':
                    ret.append( '%' ).append( Integer.toHexString( (int) content[i]) );
                    break;
                default:
                    ret.append( content[i] );
            }
        }
        return ret.toString();
    }

    /**
     * <p>Print the specified text as the response to the writer associated with
     * this page.</p>
     * 
     * <p><strong>WARNING</strong> - If you are writing body content
     * from the <code>doAfterBody()</code> method of a custom tag class that
     * implements <code>BodyTag</code>, you should be calling
     * <code>printPrevious()</code> instead.</p>
     * 
     * @param pageContext fhe PageContext object for this page
     * @param filter if true, the specified text will first be filtered (see {@link #filter filter()})
     * @param text the text to be written
     * @exception JspException if an input/output error occurs
     */
    public static void print(
            PageContext pageContext,
            boolean     filter,
            String      text )
        throws
            JspException
    {
        if( filter ) {
            text = filter( text );
        }
        JspWriter writer = pageContext.getOut();
        try {
            writer.print( text );

        } catch( IOException ex ) {
            throw new JspException( "I/O error", ex );
        }
    }

    /**
     * <p>Print the specified text as the response to the writer associated with
     * this page, followed by a carriage-return.</p>
     * 
     * <p><strong>WARNING</strong> - If you are writing body content
     * from the <code>doAfterBody()</code> method of a custom tag class that
     * implements <code>BodyTag</code>, you should be calling
     * <code>printPrevious()</code> instead.</p>
     * 
     * @param pageContext fhe PageContext object for this page
     * @param filter if true, the specified text will first be filtered (see {@link #filter filter()})
     * @param text the text to be written
     * @exception JspException if an input/output error occurs
     */
    public static void println(
            PageContext pageContext,
            boolean     filter,
            String      text )
        throws
            JspException
    {
        if( filter ) {
            text = filter( text );
        }
        JspWriter writer = pageContext.getOut();
        try {
            writer.println( text );

        } catch( IOException ex ) {
            throw new JspException( "I/O error", ex );
        }
    }
    
    /**
     * Print the specified text as the response to the writer associated with
     * the body content for the tag within which we are currently nested.
     *
     * @param pageContext the PageContext object for this page
     * @param filter if true, the specified text will first be filtered (see {@link #filter filter()})
     * @param text The text to be written
     *
     * @throws JspException if an input/output error occurs
     */
    public static void printPrevious(
            PageContext pageContext,
            boolean     filter,
            String      text )
        throws
            JspException
    {
        if( filter ) {
            text = filter( text );
        }
        JspWriter writer = pageContext.getOut();

        if( writer instanceof BodyContent ) {
            writer = ((BodyContent) writer).getEnclosingWriter();
        }

        try {
            writer.print( text );

        } catch( IOException ex ) {
            throw new JspException( "I/O error", ex );
        }
    }

    /**
     * Print the specified text as the response to the writer associated with
     * the body content for the tag within which we are currently nested, followed by
     * carriage-return
     *
     * @param pageContext the PageContext object for this page
     * @param filter if true, the specified text will first be filtered (see {@link #filter filter()})
     * @param text The text to be written
     *
     * @throws JspException if an input/output error occurs
     */
    public static void printlnPrevious(
            PageContext pageContext,
            boolean     filter,
            String      text )
        throws
            JspException
    {
        if( filter ) {
            text = filter( text );
        }
        JspWriter writer = pageContext.getOut();

        if( writer instanceof BodyContent ) {
            writer = ((BodyContent) writer).getEnclosingWriter();
        }

        try {
            writer.println( text );

        } catch( IOException ex ) {
            throw new JspException( "I/O error", ex );
        }
    }

    /**
     * Utiliy method to determine whether this field value mean boolean true.
     * Centralizing this convention seems to make sense.
     *
     * @param fieldValue the String value
     * @return true if the fieldValue represents true
     */
    public static boolean isTrue(
            String fieldValue )
    {
        if( fieldValue == null ) {
            return false;
        }
        if( "true".equalsIgnoreCase( fieldValue )) {
            return true;
        }
        if( "yes".equalsIgnoreCase( fieldValue )) {
            return true;
        }
        return false;
    }

    /**
     * Utiliy method to determine whether this value means boolean false.
     * Centralizing this convention seems to make sense.
     *
     * @param fieldValue the String value
     * @return true if the fieldValue represents false
     */
    public static boolean isFalse(
            String fieldValue )
    {
        if( fieldValue == null ) {
            return false;
        }
        if( "false".equalsIgnoreCase( fieldValue )) {
            return true;
        }
        if( "no".equalsIgnoreCase( fieldValue )) {
            return true;
        }
        return false;
    }

    /**
     * Construct a new URL from an old URL, by appending or replacing one or more arguments to the URL.
     *
     * @param oldUrl the old URL
     * @param args the URL arguments to append or replace
     * @return the new URL
     */
    public static String constructHrefWithDifferentArguments(
            String             oldUrl,
            Map<String,String> args )
    {
        int question = oldUrl.indexOf( '?' );
        if( question < 0 ) {
            // URL has no argument yet
            StringBuilder ret = new StringBuilder();
            ret.append( oldUrl );
            char sep = '?';
            for( String key : args.keySet() ) {
                String value = args.get( key );
                ret.append( sep ).append( HTTP.encodeToValidUrlArgument( key ) ).append( '=' ).append( HTTP.encodeToValidUrlArgument( value ));
                sep = '&';
            }
            return ret.toString();

        } else {
            StringBuilder append  = new StringBuilder();
            StringBuilder urlArgs = new StringBuilder();
            urlArgs.append( "&" ).append( oldUrl.substring( question+1 ) ); // this makes replacing easier

            for( String key : args.keySet() ) {
                String value        = args.get( key );
                String escapedKey   = HTTP.encodeToValidUrlArgument( key );
                String escapedValue = HTTP.encodeToValidUrlArgument( value );

                String pattern = "&" + escapedKey + "=";
                
                int found = urlArgs.indexOf( pattern );
                if( found >= 0 ) {
                    int found2 = urlArgs.indexOf( "&", found+1 );
                    if( found2 < 0 ) {
                        found2 = urlArgs.length();
                    }
                    StringBuilder newUrlArgs = new StringBuilder();
                    newUrlArgs.append( urlArgs.substring( 0, found+1 ));
                    newUrlArgs.append( escapedKey ).append( '=' ).append( escapedValue );
                    newUrlArgs.append( urlArgs.substring( found2 ));
                    urlArgs = newUrlArgs;
                } else {
                    append.append( '&' ).append( escapedKey ).append( '=' ).append( escapedValue );
                }
            }
            StringBuilder ret = new StringBuilder();
            ret.append( oldUrl.substring( 0, question+1 ));
            ret.append( urlArgs.substring( 1 )).append( append );
            return ret.toString();
        }
    }

    /**
     * Format a PropertyValue.
     *
     * @param pageContext the PageContext object for this page
     * @param value the PropertyValue
     * @param nullString the String to display of the value is null
     * @param stringRepresentation the StringRepresentation for PropertyValues
     * @return the String to display
     */
    public static String formatPropertyValue(
            PageContext        pageContext,
            PropertyValue      value,
            String             nullString,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );
        
        String ret = PropertyValue.toStringRepresentationOrNull( value, rep );
        if( ret != null ) {
            return ret;
        } else {
            return nullString;
        }
    }

    /**
     * Format an Identifier.
     *
     * @param pageContext the PageContext object for this page
     * @param identifier the Identifier to format
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshTypeIdentifier(
            PageContext        pageContext,
            MeshTypeIdentifier identifier,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );

        String ret = identifier.toStringRepresentation( rep );
        
        return ret;
    }

    /**
     * Format the start of the identifier of a MeshObject.
     *
     * @param pageContext the PageContext object for this page
     * @param mesh the MeshObject whose identifier is to be formatted
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshObjectIdentifierStart(
            PageContext        pageContext,
            MeshObject         mesh,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );

        RestfulRequest req         = (RestfulRequest) ((HttpServletRequest)pageContext.getRequest()).getAttribute( RestfulRequest.class.getName());
        String         contextPath = req.getContextPath();
        boolean isDefaultMeshBase  = isDefaultMeshBase( mesh.getMeshBase() );

        String ret = mesh.toStringRepresentation( rep, contextPath, isDefaultMeshBase );
        return ret;
    }

    /**
     * Format the end of the identifier of a MeshObject.
     *
     * @param pageContext the PageContext object for this page
     * @param mesh the MeshObject whose identifier is to be formatted
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshObjectIdentifierEnd(
            PageContext        pageContext,
            MeshObject         mesh,
            String             stringRepresentation )
    {
        return ""; // nothing
    }

    /**
     * Format the start of the link to a MeshObject.
     *
     * @param pageContext the PageContext object for this page
     * @param mesh the MeshObject whose identifier is to be formatted
     * @param rootPath alternate root path to use, if any
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshObjectLinkStart(
            PageContext        pageContext,
            MeshObject         mesh,
            String             rootPath,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );

        String contextPath;
        if( rootPath != null ) {
            contextPath = rootPath;

        } else {
            RestfulRequest req = (RestfulRequest) ((HttpServletRequest)pageContext.getRequest()).getAttribute( RestfulRequest.class.getName());
            contextPath        = req.getContextPath();
        }

        boolean isDefaultMeshBase = isDefaultMeshBase( mesh.getMeshBase() );

        String ret = mesh.toStringRepresentationLinkStart( rep, contextPath, isDefaultMeshBase );
        return ret;
    }

    /**
     * Format the end of the link to a MeshObject.
     *
     * @param pageContext the PageContext object for this page
     * @param mesh the MeshObject whose identifier is to be formatted
     * @param rootPath alternate root path to use, if any
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshObjectLinkEnd(
            PageContext        pageContext,
            MeshObject         mesh,
            String             rootPath,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );

        String contextPath;
        if( rootPath != null ) {
            contextPath = rootPath;
        } else {
            contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath() + "/";
        }

        boolean isDefaultMeshBase = isDefaultMeshBase( mesh.getMeshBase() );

        String ret = mesh.toStringRepresentationLinkEnd( rep, contextPath, isDefaultMeshBase );
        return ret;
}

    /**
     * Recreate an MeshObjectIdentifier from a String.
     * 
     * @param factory the MeshObjectIdentifierFactory to use
     * @param representation the StringRepresentation of the to-be-parsed String
     * @param s the String
     * @return the MeshObjectIdentifier
     * @throws URISyntaxException thrown if a syntax error occurred
     */
    public static MeshObjectIdentifier fromMeshObjectIdentifier(
            MeshObjectIdentifierFactory factory,
            StringRepresentation        representation,
            String                      s )
        throws
            URISyntaxException
    {
        if( s == null ) {
            return null;
        }
        MeshObjectIdentifier ret = factory.fromStringRepresentation( representation, s );

        return ret;
    }

    /**
     * Format the start of the identifier of a MeshBase.
     *
     * @param pageContext the PageContext object for this page
     * @param base the MeshBase whose identifier is to be formatted
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshBaseIdentifierStart(
            PageContext        pageContext,
            MeshBase           base,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );

        boolean contextImpliesThisMeshBase = isDefaultMeshBase( base );

        String ret = base.toStringRepresentation( rep, contextImpliesThisMeshBase );
        return ret;
    }

    /**
     * Format the end of the identifier of a MeshBase.
     *
     * @param pageContext the PageContext object for this page
     * @param base the MeshBase whose identifier is to be formatted
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshBaseIdentifierEnd(
            PageContext        pageContext,
            MeshBase           base,
            String             stringRepresentation )
    {
        return ""; // nothing
    }

    /**
     * Format the start of a link to a MeshBase.
     *
     * @param pageContext the PageContext object for this page
     * @param base the MeshBase whose identifier is to be formatted
     * @param rootPath alternate root path to use, if any
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshBaseLinkStart(
            PageContext        pageContext,
            MeshBase           base,
            String             rootPath,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );

        String contextPath;
        if( rootPath != null ) {
            contextPath = rootPath;
        } else {
            contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath() + "/";
        }

        boolean isDefaultMeshBase = isDefaultMeshBase( base );

        String ret = base.toStringRepresentationLinkStart( rep, contextPath, isDefaultMeshBase );
        return ret;
    }

    /**
     * Format the end of a link to a MeshBase.
     *
     * @param pageContext the PageContext object for this page
     * @param base the MeshBase whose identifier is to be formatted
     * @param rootPath alternate root path to use, if any
     * @param stringRepresentation the StringRepresentation to use
     * @return the String to display
     */
    public static String formatMeshBaseLinkEnd(
            PageContext        pageContext,
            MeshBase           base,
            String             rootPath,
            String             stringRepresentation )
    {
        StringRepresentation rep = determineStringRepresentation( stringRepresentation );
        
        String contextPath;
        if( rootPath != null ) {
            contextPath = rootPath;
        } else {
            contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath() + "/";
        }

        boolean isDefaultMeshBase = isDefaultMeshBase( base );

        String ret = base.toStringRepresentationLinkEnd( rep, contextPath, isDefaultMeshBase );
        return ret;
    }

    /**
     * Determine the correct StringRepresentation, by correcting any supplied value and/or
     * picking a reasonable default.
     * 
     * @param in the original value
     * @return the StringRepresentation
     */
    public static StringRepresentation determineStringRepresentation(
            String in )
    {
        String sanitized;
        
        if( in == null || in.length() == 0 ) {
            sanitized = "Html";
        } else {
            StringBuilder temp = new StringBuilder( in.length() );
            temp.append( Character.toUpperCase( in.charAt( 0 )));
            temp.append( in.substring( 1 ).toLowerCase() );
            sanitized = temp.toString();
        }
        StringRepresentation ret = ModelPrimitivesStringRepresentation.create( sanitized );
        return ret;
    }

    /**
     * Determine whether the fully-qualified context path that cane be determined from a
     * PageContext identifies this MeshBase as the default MeshBase.
     * 
     * @param base the MeshBase whose identifier is to be formatted
     * @return true if this MeshBase is the default MeshBase
     */
    public static boolean isDefaultMeshBase(
            MeshBase base )
    {
        // FIXME this can be simpler right?
        String mbIdentifier = base.getIdentifier().toExternalForm();
        
        MeshBase defaultMb           = InfoGridWebApp.getSingleton().getApplicationContext().findContextObjectOrThrow( MeshBase.class );
        String   defaultMbIdentifier = defaultMb.getIdentifier().toExternalForm();
        
        if( defaultMbIdentifier.equals( mbIdentifier )) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Make sure a String is not longer than <code>maxLength</code>. This is accomplished
     * by taking out characters in the middle if needed.
     *
     * @param in the input String
     * @param maxLength the maximally allowed length
     * @return the String, potentially shortened
     */
    public static String potentiallyShorten(
            String in,
            int    maxLength )
    {
        if( in == null || in.length() == 0 ) {
            return "";
        }

        final String insert = "...";
        final int    fromEnd = 5; // how many characters we leave at the end
        
        String ret = in;
        if( maxLength > 0 && ret.length() > maxLength ) {
            ret = ret.substring( 0, maxLength-fromEnd-insert.length() ) + insert + ret.substring( ret.length() - fromEnd );
        }
        return ret;
    }

    /**
     * Add <code>&</code>-separated arguments to this URL. If <code>escapeArguments</code> is
     * set to true (see {@link #isTrue isTrue()}, <code>addArguments</code> will first be escaped, and then
     * appended.
     *
     * @param url the URL to which to append the arguments
     * @param addArguments the arguments to be appended
     * @param escapeArguments if true, escape <code>addArguments</code> prior to appending
     */
    public static void appendArguments(
            StringBuilder url,
            String        addArguments,
            String        escapeArguments )
    {
        if( addArguments == null ) {
            return;
        }

        char sep;
        int question = url.indexOf( "?" );
        if( question >= 0 ) {
            sep = '&';
        } else {
            sep = '?';
        }
        url.append( sep );
        
        if( isTrue( escapeArguments )) {

            String [] pairs = addArguments.split( "&" );
            String    sep2 = "";

            for( String pair : pairs ) {
                url.append( sep2 );
                int index = pair.indexOf( '=' );
                String name;
                if( index >= 0 ) {
                    name = pair.substring( 0, index );
                    String value = pair.substring( index+1 );

                    String escapedName  = HTTP.encodeToValidUrlArgument( name );
                    String escapedValue = HTTP.encodeToValidUrlArgument( value );
                    url.append( escapedName );
                    url.append( '=' );
                    url.append( escapedValue );
                } else {
                    String escapedName = HTTP.encodeToValidUrlArgument( pair );
                    url.append( escapedName );
                }
                sep2 = "&";
            }

        } else {
            // this is much simpler
            url.append( addArguments );
        }
    }
    
    /**
     * Determine the specified Locale from a String representation.
     *
     * @param localeString the specified Locale in a String representation
     * @return the Locale
     * @throws JspException thrown if the Locale could not be determined from the String
     */
    public static Locale determineLocale(
            String localeString )
        throws
            JspException
    {
        Locale ret;

        if( localeString != null && localeString.length() > 0 ) {
            String [] split = localeString.split( "-" );
            switch( split.length ) {
                case 1:
                    ret = new Locale( split[0] );
                    break;
                case 2:
                    ret = new Locale( split[0], split[1] );
                    break;
                case 3:
                    ret = new Locale( split[0], split[1], split[2] );
                    break;
                default:
                    throw new JspException( "Locale attribute must not contain more than two hyphens" );
            }
        } else {
            ret = Locale.getDefault();
        }
        return ret;
    }

    /**
     * Maps lowercase JSP scope names to their PageContext integer constant values.
     */
    private static Map<String,Integer> scopes = new HashMap<String,Integer>();

    static {
        scopes.put( "page",        PageContext.PAGE_SCOPE );
        scopes.put( "request",     PageContext.REQUEST_SCOPE );
        scopes.put( "session",     PageContext.SESSION_SCOPE );
        scopes.put( "application", PageContext.APPLICATION_SCOPE );
    }
    
    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( InfoGridJspUtils.class );
}
