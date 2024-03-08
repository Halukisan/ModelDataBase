package org.fusion.constants;

public class MilvusIndexConstants {
    public static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"title\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"standard\",\n" +
            "        \"index\": true,\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"question\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"index\": true,\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"embedding\":{\n" +
            "        \"type\": \"dense_vector\",\n" +
            "        \"dims\":1024\n" +
            "      },\n" +
            "      \"content\":{\n" +
            "        \"type\": \"text\"\n" +
            "      },\n" +
            "      \"all\":{\n" +
            "        \"type\": \"text\"\n" +
            "        , \"analyzer\": \"standard\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
