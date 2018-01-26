@Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.9.3')

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature

def json1 = '{"name": "John", "data": [{"id": 1},{"id": 2},]}'
def json2 = '{"name": "John", "data": [{"id": 1},{"id": 2}],}'
def json3 = '{"name": "John", "data": [{"id": 1},{"id": 2}]},'
def json4 = '{"name": "John", "data": [{"id": 1},{"id": 2}]}... abc'
def json5 = '{"name": "John", "data": [{"id": 1},{"id": 2}]}'

def test1 = { String json ->
    try {
        JsonOutput.prettyPrint(json)
        return "VALID"
    } catch (ignored) {
        return "INVALID"
    }
}

def test2 = { String json ->
    try {
        new JsonSlurper().parseText(json)
        return "VALID"
    } catch (ignored) {
        return "INVALID"
    }
}

ObjectMapper mapper = new ObjectMapper()
mapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true)

def test3 = { String json ->
    try {
        mapper.readValue(json, Map)
        return "VALID"
    } catch (ignored) {
        return "INVALID"
    }
}

def jsons = [json1, json2, json3, json4, json5]
def tests = ['JsonOutput': test1, 'JsonSlurper': test2, 'ObjectMapper': test3]

def result = tests.collectEntries { name, test ->
    [(name): jsons.collect { json ->
        [json: json, status: test(json)]
    }]
}

result.each {
    println "${it.key}:"
    it.value.each {
        println " ${it.status}: ${it.json}"
    }
    println ""
}