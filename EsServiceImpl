package org.fusion.service.Impl;

import com.alibaba.fastjson.JSON;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.fusion.model.Milvus;
import org.fusion.service.EsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fusion.constants.MilvusIndexConstants.MAPPING_TEMPLATE;

@Service
public class EsServiceImpl implements EsService {
    public Map<String, String> fileMap = new HashMap<>();
    static AtomicLong atomicLong = new AtomicLong(1);
    @Autowired
    private RestHighLevelClient Restclient;
    @Override
    public void createEs() throws IOException {
// 1.准备Request      PUT
        CreateIndexRequest request = new CreateIndexRequest("milvus");
        // 2.准备请求参数
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        // 3.发送请求
        Restclient.indices().create(request, RequestOptions.DEFAULT);
    }
    @Override
    public void deleteEs() throws IOException {
        // 1.准备Request
        DeleteIndexRequest request = new DeleteIndexRequest("milvus");
        // 3.发送请求
        Restclient.indices().delete(request, RequestOptions.DEFAULT);
    }
    @Override
    public void addDocumemtToEs() throws IOException {
        read();
    }
    //读取文件内容
    private void read() {

        File folder = new File("C:\\Users\\l50011273\\Desktop\\Code\\Milvus\\案例文本域导出");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    processFile(file);
                }
            }
        }
    }
    private void insert() throws IOException {
        String title = fileMap.get("标题");
        String des = fileMap.get("问题描述");
        List<Double> embedding = null;
        String contenr = "key_word:" + fileMap.get("key_word") + "。告警信息:" + fileMap.get("告警信息") + "。处理过程：" + fileMap.get("处理过程") + "。根因:" + fileMap.get("根因") + "。解决方案:" + fileMap.get("解决方案") + "。建议和总结" + fileMap.get("建议和总结");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://127.0.0.1:5001/embedding?sentence=" + des)
                .build();


        try (Response response = client.newCall(request).execute()) {
            embedding = getData(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Milvus milvus = new Milvus();
        milvus.setId(atomicLong.incrementAndGet());
        milvus.setTitle(title);
        milvus.setQuestion(des);
        milvus.setEmbedding(embedding);
        milvus.setContent(contenr);
        // 3.转JSON
        String json = JSON.toJSONString(milvus);

        // 1.准备Request
        IndexRequest Indexrequest = new IndexRequest("milvus").id(milvus.getId().toString());
        // 2.准备请求参数DSL，其实就是文档的JSON字符串
        Indexrequest.source(json, XContentType.JSON);
        // 3.发送请求
        Restclient.index(Indexrequest, RequestOptions.DEFAULT);
    }

    private void processFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder block = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    processBlock(block.toString());
                    block.setLength(0);
                } else {
                    block.append(line).append("\n");
                }
            }
            // Process the last block
            if (block.length() > 0) {
                processBlock(block.toString());

            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processBlock(String block) throws Exception {

        Pattern pattern = Pattern.compile("【(.*?)】\\s*\n(.*?)\\s*\n", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(block);
        while (matcher.find()) {
            String field = matcher.group(1);
            String content = matcher.group(2);
            //System.out.println("Field: " + field + ", Content: " + content);
            fileMap.put(field, content);
            System.out.println(field+fileMap.get(field));
            //一个txt文件全部读取完毕后，插入一次
        }
        insert();
    }

    @NotNull
    private static List<Double> getData(Response response) throws IOException {
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Optional<ResponseBody> responseBody = Optional.ofNullable(response.body());

        String str = responseBody.map(rb -> {
            try {
                return rb.string();
            } catch (IOException e) {
                return null;
            }
        }).orElse("");

        str = str.substring(1, str.length() - 2);
        String[] strArray = str.split(",");

        return Arrays.stream(strArray).map(Double::parseDouble).toList();
    }

}
