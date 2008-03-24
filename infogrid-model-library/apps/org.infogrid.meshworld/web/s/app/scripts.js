function include( context, src ) {
    var s = document.createElement('script');
    s.setAttribute('type', 'text/javascript');
    s.setAttribute('src', context + src );
    document.getElementsByTagName('head')[0].appendChild(s);
}
function includeAll( context ) {
    include( context, "/v/org/infogrid/jee/taglib/candy/OverlayTag.js" );
    include( context, "/v/org/infogrid/jee/taglib/viewlet/ViewletAlternativesTag.js" );
}
