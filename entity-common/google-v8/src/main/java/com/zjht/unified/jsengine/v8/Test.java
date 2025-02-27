package com.zjht.unified.jsengine.v8;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueProxy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

public class Test {

    static void test(
            V8Runtime v8Runtime,
            String testName,
            Object jsonNode,
            int rounds)
            throws JavetException {
        System.out.println("--- " + testName + " ---");
        final long startTime = System.currentTimeMillis();

        V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
//        v8Runtime.getGlobalObject().invokeVoid(testName, jsonNode, rounds);
        final long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) + "ms");
        System.out.println(jsonNode);
        System.out.println();
    }

    public static void main(String[] args) {
        final int rounds = 100_000;

// Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {

            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());
            // Set converter to proxy based one to unlock the interoperability.
            v8Runtime.setConverter(new JavetProxyConverter());
//            v8Runtime.getExecutor(testScriptJavaFlavor).executeVoid();
            v8Runtime.getExecutor(
                    "function testJSFlavor(node, rounds) {\n" +
                    "  for (let i = 0; i < rounds; i++) {\n" +
                    "    const b = node?.a?.b;\n" +
                    "    if (b?.c !== undefined) {\n" +
                    "      b.c = b.c + 1;\n" +
                    "    }\n" +
                    "  }\n" +
                    "}").executeVoid();

            // Test the Java Flavor.

            ObjectMapper objectMapper = new ObjectMapper();

            V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
            JsonNodeWrapper jsonNodeWrapper = new JsonNodeWrapper(v8Runtime, null);
            v8ValueObject.bind(jsonNodeWrapper);


            V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy(v8ValueObject);
            v8Runtime.getGlobalObject().set("a",v8ValueProxy);


            v8Runtime.getExecutor("console.log(a.name)").executeVoid();


            // Test the JS Flavor.
//            test(v8Runtime,
//                    "testJSFlavor",
//                    new JsonNodeWrapper(v8Runtime, objectMapper.readTree("{\"a\":{\"b\":{\"c\":100000}}}")),
//                    rounds);

            // Notify V8 to perform GC. (Optional)
            v8Runtime.lowMemoryNotification();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    static class JsonNodeWrapper implements IJavetDirectProxyHandler<Exception> {
        private final JsonNode jsonNode;
        private V8Runtime v8Runtime;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public JsonNodeWrapper(V8Runtime v8Runtime, JsonNode jsonNode) {
            this.v8Runtime = Objects.requireNonNull(v8Runtime);
            this.jsonNode = jsonNode;
        }

        public JsonNode getJsonNode() {
            return jsonNode;
        }

        @Override
        public V8Runtime getV8Runtime() {
            return v8Runtime;
        }

        @Override
        public V8Value proxyGet(
                V8Value target,
                V8Value property,
                V8Value receiver)
                throws JavetException, Exception {
            System.out.println("proxyGet");
            if (property instanceof V8ValueString) {
                String name = ((V8ValueString) property).getValue();
                if (jsonNode.has(name)) {
                    JsonNode childJsonNode = jsonNode.get(name);
                    if (childJsonNode.isInt()) {
                        return v8Runtime.createV8ValueInteger(childJsonNode.asInt());
                    } else {
                        return v8Runtime.toV8Value(new JsonNodeWrapper(v8Runtime, childJsonNode));
                    }
                }
            }
            return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
        }

        @Override
        public V8ValueBoolean proxySet(
                V8Value target,
                V8Value propertyKey,
                V8Value propertyValue,
                V8Value receiver)
                throws JavetException, Exception {
            System.out.println("proxySet");

            if (propertyKey instanceof V8ValueString && propertyValue instanceof V8ValueInteger) {
                String name = ((V8ValueString) propertyKey).getValue();
                int value = ((V8ValueInteger) propertyValue).getValue();
                if (jsonNode.isObject()) {
                    ((ObjectNode) jsonNode).put(name, value);
                    return v8Runtime.createV8ValueBoolean(true);
                }
            }
            return IJavetDirectProxyHandler.super.proxySet(target, propertyKey, propertyValue, receiver);
        }

        @Override
        public String toString() {
            return jsonNode.toString();
        }
    }
}
