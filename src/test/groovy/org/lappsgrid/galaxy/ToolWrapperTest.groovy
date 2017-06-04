package org.lappsgrid.galaxy

import org.junit.Ignore
import org.junit.Test
import groovyx.net.http.HttpBuilder

/**
 * @author Keith Suderman
 */
@Ignore
class ToolWrapperTest {

    @Test
    void testGateGroovyTemplate() {
        ToolWrapper app = new ToolWrapper()
        app.query = ToolWrapper.VASSAR + "?serviceName=gate&serviceId=2.2.0"
        app.templatePath = 'gate-template.groovy'
        app.toolVersion = '1.0.1'
        app.useGroovy = true
//        app.outputPath = '/usr/local/galaxy/mods/tools/gate'
//        app.outputPath = '/tmp/gate'
        app.run()
    }

    @Test
    void testGateXmlTemplate() {
        ToolWrapper app = new ToolWrapper()
        app.query = ToolWrapper.VASSAR + "?serviceName=gate&serviceId=2.2.0"
        app.templatePath = 'gate-template.xml'
        app.toolVersion = '1.0.1'
        app.useGroovy = false
//        app.outputPath = '/usr/local/galaxy/mods/tools/gate'
//        app.outputPath = '/tmp/gate'
        app.run()
    }

    @Test
    void queryTest() {
        HttpBuilder http = HttpBuilder.configure {
            request.uri = "http://api.lappsgrid.org/services/vassar?serviceId=gate&serviceName=2.2.0"
            request.headers.Accept = "application/json"
        }
        Map response = http.get()
        println new groovy.json.JsonBuilder(response).toPrettyString()
        //println new URL("http://api.lappsgrid.org/services/vassar?serviceId=gate&serviceName=2.2.0").text
    }
}
