/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.infogrid.viewlet;

/**
 *
 * @author buildmaster
 */
public interface ViewletMatcher
{
    public ViewletFactoryChoice match(
            MeshObjectsToView toView );
}
