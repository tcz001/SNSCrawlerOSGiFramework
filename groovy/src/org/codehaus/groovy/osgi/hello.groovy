package org.codehaus.groovy.osgi

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

class Activator implements BundleActivator {

    void start(BundleContext context) {
        println "Groovy BundleActivator started"
    }

    void stop(BundleContext context) {
        println "Groovy BundleActivator stopped"
    }
}
