function toggle_viewlet_alternatives() {
    var div = document.getElementById( 'org-infogrid-jee-taglib-viewlet-ViewletAlternativesTag' );
    if( div.getAttribute( 'class' ) == 'org-infogrid-jee-taglib-viewlet-ViewletAlternativesTag' ) {
        div.setAttribute( 'class', 'org-infogrid-jee-taglib-viewlet-ViewletAlternativesTag-expanded' );
    } else {
        div.setAttribute( 'class', 'org-infogrid-jee-taglib-viewlet-ViewletAlternativesTag' );
    }
}
