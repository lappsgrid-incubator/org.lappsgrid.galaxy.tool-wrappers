def yield = { s ->
    builder.mkp.yield s
}

tool(id:"${gridId}_${serviceId}", name:serviceName, version:toolVersion) {
    description serviceDescription
    command "lsd \$__tool_directory__/invoke.lsd $serviceId \$input \$output"
    inputs {
        param(name:'input', type:'data', format:inputFormats)
    }
    outputs {
        data(name:'output', format:outputFormat)
    }

    help {
        builder.mkp.yieldUnescaped """<![CDATA[
HELP
====

$serviceDescription

"""
        if (requires && requires.size() > 0) {
//			builder.mkp.yield "Required Annotations\n"
            yield "Required Annotations\n"
            builder.mkp.yield "--------------------\n"
            requires.each {
                builder.mkp.yield "* $it\n"
            }
            builder.mkp.yield '\n'
        }

        if (produces && produces.size() > 0) {
            builder.mkp.yield 'Generated Annotations\n'
            builder.mkp.yield '---------------------\n'
            produces.each {
                builder.mkp.yield "* ${it}\n"
            }
        }

        builder.mkp.yieldUnescaped '''
License
-------

Lingpipe is released under the `GNU Affero General Public License version 3.0 <https://www.gnu.org/licenses/agpl-3.0.en.html>`_

'''

        builder.mkp.yieldUnescaped '\n]]>\n'
    }


}

