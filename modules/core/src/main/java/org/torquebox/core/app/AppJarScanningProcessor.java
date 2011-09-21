package org.torquebox.core.app;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.polyglot.core.app.BaseAppJarScanningProcessor;

public class AppJarScanningProcessor extends BaseAppJarScanningProcessor {

    public AppJarScanningProcessor() {
        super( SCAN_ROOTS );
    }
    
    @SuppressWarnings("serial")
    private static final List<String> SCAN_ROOTS = new ArrayList<String>() {
        {
            add( "lib" );
            add( "vendor/jars" );
            add( "vendor/plugins" );
        }
    };
}
