package org.lappsgrid.galaxy

import org.junit.Ignore
import org.junit.Test

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
        app.useGroovy = true
//        app.outputPath = '/usr/local/galaxy/mods/tools/gate'
        app.outputPath = '/tmp/gate'
        app.run()
    }

    @Test
    void testGateXmlTemplate() {
        ToolWrapper app = new ToolWrapper()
        app.query = ToolWrapper.VASSAR + "?serviceName=gate&serviceId=2.2.0"
        app.templatePath = 'gate-template.jsp'
        app.useGroovy = false
//        app.outputPath = '/usr/local/galaxy/mods/tools/gate'
//        app.outputPath = '/tmp/gate'
        app.run()
    }
}
