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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.templates;

import org.infogrid.util.Factory;
import org.infogrid.util.http.SaneRequest;

/**
 * Marks classes that know how to create StructuredResponseTemplates.
 */
public interface StructuredResponseTemplateFactory
    extends
        Factory<SaneRequest,StructuredResponseTemplate,StructuredResponse>
{
    // no op
}
