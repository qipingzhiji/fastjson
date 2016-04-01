package com.alibaba.fastjson.parser.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParseContext;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.FieldInfo;

public class DefaultFieldDeserializer extends FieldDeserializer {

    private ObjectDeserializer fieldValueDeserilizer;

    public DefaultFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo){
        super(clazz, fieldInfo, JSONToken.LITERAL_INT);
    }

    @Override
    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        if (fieldValueDeserilizer == null) {
            fieldValueDeserilizer = parser.config.getDeserializer(fieldInfo);
            fastMatchToken = fieldValueDeserilizer.getFastMatchToken();
        }

        if (objectType instanceof ParameterizedType) {
            ParseContext objContext = parser.getContext();
            objContext.type = objectType;
        }

        Object value = fieldValueDeserilizer.deserialze(parser, fieldInfo.fieldType, fieldInfo.name);
        if (parser.resolveStatus == DefaultJSONParser.NeedToResolve) {
            ResolveTask task = parser.getLastResolveTask();
            task.fieldDeserializer = this;
            task.ownerContext = parser.getContext();
            parser.resolveStatus = DefaultJSONParser.NONE;
        } else {
            if (object == null) {
                fieldValues.put(fieldInfo.name, value);
            } else {
                setValue(object, value);
            }
        }
    }
}
