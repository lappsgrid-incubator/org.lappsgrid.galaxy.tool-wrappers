package org.lappsgrid.galaxy

import org.anc.template.*
import org.lappsgrid.client.ServiceClient
import groovy.json.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static org.lappsgrid.discriminator.Discriminators.*

class ToolWrapper {

    static final String VASSAR = 'http://api.lappsgrid.org/services/vassar'
    static final String BRANDEIS = 'http://api.lappsgrid.org/services/brandeis'

    /**
     * The full URL used to query the Service Manager at one of the above servers.
     */
    String query

    /**
     * The path to the template file used to generate the tool XML wrappers.
     */
    String templatePath

    /**
     * Where the tool XML wrapper files should be written.
     */
    String outputPath

    /**
     * Value to be used as the @version attribute of the outer <tool/> element. Default
     * is '1.0.0'
     */
    String toolVersion

    /**
     * Which template engine to use.  If true the MarkupBuilderTemplateEngine (groovy.xml.MarkupBuilder)
     * will be used, otherwise the HtmlTemplateEngine (groovy.text.SimpleTemplateEngine) will
     * be used.
     */
    boolean useGroovy

	void run() {
		Path dest
        if (outputPath) {
            dest = Paths.get(outputPath)
            if (!Files.exists(dest)) {
                if (!Files.createDirectories(dest)) {
                    println "Unable to create ${dest}"
                    return
                }
            }
        }

		String template = new File(templatePath).text
		TemplateEngine engine
        if (useGroovy) {
            engine = new MarkupBuilderTemplateEngine(template)
        }
        else {
            engine = new HtmlTemplateEngine(template)
        }

        String servicesJson = new URL(query).text
		def parser = new JsonSlurper()
		def object = parser.parseText(servicesJson)

		object.elements.each { e ->
			Map binding = [:]
            def ids = e.serviceId.tokenize(':')
            binding.gridId = ids[0]
			binding.serviceId = ids[1]
			binding.serviceName = e.serviceName
			binding.serviceDescription = e.serviceDescription
			binding.toolVersion = toolVersion ?: '1.0.0'

			ServiceClient client = new ServiceClient(e.endpointUrl, 'tester', 'tester')
			def d = parser.parseText(client.getMetadata())
			if (d.discriminator != Uri.META) {
				throw IOException("Invalid metadata returned from ${e.serviceName}")
			}
			def metadata = d.payload

			binding.inputFormats = getFormat(metadata.requires.format)
			binding.requires = metadata.requires.annotations
            binding.produces = metadata.produces.annotations

			binding.outputFormat
			if (metadata.format) {
				binding.outputFormat = getFormat(metadata.format)
			}
			else {
				binding.outputFormat = getFormat(metadata.produces.format)
			}

            String toolXml = engine.generate(binding)
            if (dest) {
                String filename = binding.serviceId + '.xml'
                Path output = dest.resolve(filename)
                output.text = toolXml
//                new File(dest, filename).text = engine.generate(binding)
                println "Wrote $output"
            }
            else {
                println toolXml
            }
		}
	}

	protected String getFormat(List list) {
		if (!list) return null
		list.collect{ getFormat(it) }.join(',')
	}

	protected String getFormat(String url) {
		int index = url.indexOf('#')
		if (index < 0) {
			index = url.lastIndexOf('/')
		}
		String format = url.substring(index+1)
        if ('text' == format) {
            return 'txt'
        }
        return format
	}

	static void main(String[] args) {
        def cli = new CliBuilder()
        cli.usage = "java -jar tool-wrapper-${Version.version}.jar -s (vassar|brandeis) [-e (jsp|groovy)]  [-q <query>] [-o <output directory>] <template>"
        cli.header = "\nGenerates tool wrapper XML files for Galaxy.\n"
        cli.footer = '''
If an output director is not specified the output will be written to STDOUT.

Copyright 2017 The Language Application Grid

'''
        cli.q(longOpt:'query', args:1, 'Service Manager query used to filter services.')
        cli.o(longOpt:'output', args:1, 'Location to write tool xml wrappers.')
        cli.e(longOpt:'engine', args:1, 'Template engine to use. Default is groovy.')
        cli.s(longOpt:'server', args:1, 'Server to query for services. One of vassar or brandeis.')
        cli.v(longOpt:'version', args:1, 'The string to use in the version attribute of the tool element.')
        cli.h(longOpt:'help', 'Displays this help message.')

        def params = cli.parse(args)
        if (params.h) {
            println "LAPPS/Galaxy Tool XML Generator. Version: ${Version.version}"
            cli.usage()
            return
        }

        if (!params.s) {
            println "ERROR: No server specified. Must be one of 'vassar' or 'brandeis'"
            return
        }
        String server
        if (params.s == 'vassar') {
            server = VASSAR
        }
        else if (params.s == 'brandeis') {
            server = BRANDEIS
        }
        else {
            println "ERROR: Invalid server name. Must be one of 'vassar' or 'brandeis'"
            return
        }
        ToolWrapper app = new ToolWrapper()
        if (params.e == 'jsp') {
            app.useGroovy = false
        }
        else if (params.e == 'groovy') {
            app.useGroovy = true
        }
        else {
            println "ERROR: Invalid engine type.  Must be one of 'jsp' or 'groovy'."
            return
        }

        app.templatePath = params.arguments()[0]
        if (params.o) {
            app.outputPath = params.o
        }
        if (params.v) {
            app.toolVersion = params.v
        }
        app.query = server
        if (params.q) {
            app.query = server + '?' + params.q.tokenize(' ').join('&')
        }

        app.run()
	}
}


