/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.torquebox.web.rack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.torquebox.core.TorqueBoxYamlParsingProcessor;
import org.torquebox.core.pool.PoolingYamlParsingProcessor;
import org.torquebox.core.runtime.PoolMetaData;
import org.torquebox.test.as.AbstractDeploymentProcessorTestCase;
import org.torquebox.test.as.MockDeploymentPhaseContext;
import org.torquebox.test.as.MockDeploymentUnit;

public class WebRuntimePoolProcessorTest extends AbstractDeploymentProcessorTestCase {

    @Before
    public void setUp() {
        appendDeployer( new WebRuntimePoolProcessor() );
    }

    @Test
    public void testPoolMetaDataAttaching() throws Throwable {
        MockDeploymentPhaseContext phaseContext = createPhaseContext();
        MockDeploymentUnit unit = phaseContext.getMockDeploymentUnit();

        unit.putAttachment( RackApplicationMetaData.ATTACHMENT_KEY, new RackApplicationMetaData() );

        deploy( phaseContext );

        List<PoolMetaData> allMetaData = unit.getAttachmentList( PoolMetaData.ATTACHMENTS_KEY );

        assertEquals( 1, allMetaData.size() );

        PoolMetaData poolMetaData = allMetaData.get( 0 );

        assertNotNull( poolMetaData );
        assertTrue( poolMetaData.isShared() );

        unit.removeAttachment( PoolMetaData.ATTACHMENTS_KEY );

        unit.addToAttachmentList( PoolMetaData.ATTACHMENTS_KEY, new PoolMetaData( "web", 2, 4 ) );

        deploy( phaseContext );

        allMetaData = unit.getAttachmentList( PoolMetaData.ATTACHMENTS_KEY );

        assertEquals( 1, allMetaData.size() );

        poolMetaData = allMetaData.get( 0 );

        assertNotNull( poolMetaData );
        assertFalse( poolMetaData.isShared() );
        assertEquals( 2, poolMetaData.getMinimumSize() );
        assertEquals( 4, poolMetaData.getMaximumSize() );
    }

    @Test
    public void testPoolingYamlOverride() throws Throwable {
        prependDeployer( new PoolingYamlParsingProcessor() );
        prependDeployer( new TorqueBoxYamlParsingProcessor() );
        
        URL poolingYml = getClass().getResource( "pooling.yml" );

        MockDeploymentPhaseContext phaseContext = createPhaseContext( "torquebox.yml", poolingYml );
        MockDeploymentUnit unit = phaseContext.getMockDeploymentUnit();

        unit.putAttachment( RackApplicationMetaData.ATTACHMENT_KEY, new RackApplicationMetaData() );

        deploy( phaseContext );
        List<PoolMetaData> allMetaData = unit.getAttachmentList( PoolMetaData.ATTACHMENTS_KEY );

        assertEquals( 1, allMetaData.size() );

        PoolMetaData poolMetaData = allMetaData.get( 0 );
        
        assertNotNull( poolMetaData );
        assertFalse( poolMetaData.isShared() );
        assertEquals( 2, poolMetaData.getMinimumSize() );
        assertEquals( 4, poolMetaData.getMaximumSize() );
    }

}
