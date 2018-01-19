package org.nomadic.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Data;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by peaches on 2017/7/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJackson {
    static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    static XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    @Test
    public void test1WriteJson() throws IOException {
        MainBodyDTO mainBodyDTO = new MainBodyDTO();
        mainBodyDTO.setMainBody("Angel");
        mainBodyDTO.setGmtCreate(new Date());
        mainBodyDTO.setMap(new LinkedHashMap<String, String>() {{
            put("5", "2");
            put("2", "3");
            put("3", "4");
        }});
        String jsonString = objectMapper.writeValueAsString(mainBodyDTO);
        System.out.println(jsonString);
        MainBodyDTO mainBodyDTO1 = objectMapper.readValue(jsonString, MainBodyDTO.class);
        System.out.println(mainBodyDTO1);
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        System.out.println(jsonNode);
    }

    @Test
    public void test2WriteXml() throws JsonProcessingException {
        MainBodyDTO mainBodyDTO = new MainBodyDTO();
        mainBodyDTO.setMainBody("Angel");
        mainBodyDTO.setGmtCreate(new Date());
        String xmlString = xmlMapper.writeValueAsString(mainBodyDTO);
        System.out.println(xmlString);
    }

    @Data
    public static class MainBodyDTO {
        private String mainBody;
        private Date gmtCreate;
        private Integer status;
        private Map<String, String> map;
    }
}
