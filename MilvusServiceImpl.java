package org.fusion.service.Impl;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.*;

import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.index.CreateIndexParam;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.fusion.service.MilvusService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MilvusServiceImpl implements MilvusService {
    public Map<String, String> fileMap = new HashMap<>();

    static AtomicInteger atomicInteger = new AtomicInteger(1);
    List<String> titleList = new ArrayList<>();
    List<String> questionList = new ArrayList<>();
    List<String> contentList = new ArrayList<>();

    List<Double> embedding = new ArrayList<>();

    @Override
    public Integer createMilvus() {
        final MilvusServiceClient milvusServiceClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost("70.182.56.3")
                        .withPort(19530)
                        .build()
        );
        FieldType fieldType1 = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withDescription("int64")
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
        FieldType fieldType2 = FieldType.newBuilder()
                .withName("title")
                .withDataType(DataType.VarChar)
                .withDescription("float vector")
                .withPrimaryKey(false)
                .withMaxLength(65530)
                .withDimension(128)  // 指定向量的维度
                .build();

        FieldType fieldType3 = FieldType.newBuilder()
                .withName("embedding")
                .withDataType(DataType.FloatVector)
                .withDescription("embedding")
                .withPrimaryKey(false)
                .withDimension(1024)
                .build();
        FieldType fieldType4 = FieldType.newBuilder()
                .withName("question")
                .withDataType(DataType.VarChar)
                .withDescription("question")
                .withPrimaryKey(false)
                .withMaxLength(65530)
                .withDimension(128)  // 指定向量的维度
                .build();

        FieldType fieldType = FieldType.newBuilder()
                .withName("content")
                .withDataType(DataType.VarChar)
                .withDescription("content")
                .withPrimaryKey(false)
                .withMaxLength(65530)
                .withDimension(128)  // 指定向量的维度
                .build();


        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName("TestByHalu")
                .withDescription("testOne")
                .withShardsNum(2)//要与集合一起创建的分片数量
                .addFieldType(fieldType1)
                .addFieldType(fieldType2)
                .addFieldType(fieldType3)
                .addFieldType(fieldType4)
                .addFieldType(fieldType)
                .withEnableDynamicField(true)//是否为此集合启用动态字段
                .build();

        R<RpcStatus> collection = milvusServiceClient.createCollection(createCollectionReq);

        milvusServiceClient.createIndex(
                CreateIndexParam.newBuilder()
                        .withCollectionName("TestByHalu")
                        .withFieldName("embedding")
                        .withIndexType(IndexType.HNSW)
                        .withMetricType(MetricType.L2)//度量类型，L2是欧式距离
                        .withExtraParam("{\"M\": \"16\", \"efConstruction\": \"200\"}")
                        .withSyncMode(Boolean.FALSE)//异步索引构建，后台构建索引，便于其他操作的进行
                        .build()
        );

        milvusServiceClient.createIndex(
                CreateIndexParam.newBuilder()
                        .withCollectionName("TestByHalu")
                        .withFieldName("question")
                        .withIndexType(IndexType.FLAT)
                        .withMetricType(MetricType.L2)//度量类型，L2是欧式距离
                        .withExtraParam("{\"M\": \"16\", \"efConstruction\": \"200\"}")
                        .withSyncMode(Boolean.FALSE)//异步索引构建，后台构建索引，便于其他操作的进行
                        .build()
        );
        return collection.getStatus();
    }

    @Override
    public boolean insert(Map<String, String> map) throws Exception {
        Map<String, String> stringMap = map;
        final MilvusServiceClient milvusServiceClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost("70.182.56.3")
                        .withPort(19530)
                        .build()
        );



//        System.out.println("标题:"+readMap.get("标题"));
//        System.out.println("key_word："+readMap.get("key_word"));
//        System.out.println("问题描述:"+readMap.get("问题描述"));
//        System.out.println("告警信息："+readMap.get("告警信息"));
//        System.out.println("处理过程："+readMap.get("处理过程"));
//        System.out.println("根因："+readMap.get("根因"));
//        System.out.println("解决方案："+readMap.get("解决方案"));
//        System.out.println("建议和总结："+readMap.get("建议和总结"));
        titleList.add(fileMap.get("标题").trim());
        String s = fileMap.get("标题");


        String des = fileMap.get("问题描述");
        questionList.add(des.trim());

        String contenr = "key_word:" + fileMap.get("key_word") + "。告警信息:" + fileMap.get("告警信息") + "。处理过程：" + fileMap.get("处理过程") + "。根因:" + fileMap.get("根因") + "。解决方案:" + fileMap.get("解决方案") + "。建议和总结" + fileMap.get("建议和总结");
        System.out.println(contenr.trim());
        contentList.add(contenr.trim());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://127.0.0.1:5001/embedding?sentence=" + des)
                .build();

        try (Response response = client.newCall(request).execute()) {
            embedding = getData(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //---------------------------------------------------------List<FloatVector> floatVectorList = new ArrayList<>();

        for (Double d : embedding) {
            int size = d.intValue(); // 假设将Double转换为int作为数组大小
            float[] floatArray = new float[size];
            // 假设有一些转换过程来从Double值填充floatArray
            for (int i = 0; i < size; i++) {
                floatArray[i] = d.floatValue();
            }

//            FloatVector floatVector = FloatVector.fromArray(FloatVector.SPECIES_MAX, floatArray, 0);
//            floatVectorList.add(floatVector);
        }

        List<InsertParam.Field> fieldss = new ArrayList<>();

        System.out.println("------------------------------------");
        fieldss.add(new InsertParam.Field("id", List.of(atomicInteger)));
        System.out.println(List.of(atomicInteger));
        fieldss.add(new InsertParam.Field("title", titleList));
        System.out.println(titleList);
        //-----------------------------------------------------------------fieldss.add(new InsertParam.Field("embedding", floatVectorList));
        System.out.println(embedding);
        fieldss.add(new InsertParam.Field("question", questionList));
        System.out.println(questionList);
        fieldss.add(new InsertParam.Field("content", contentList));
        System.out.println(contentList);
        System.out.println("------------------------------------");

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName("TestByHalu")
                .withFields(fieldss)
                .build();
        milvusServiceClient.insert(insertParam);


        return true;
    }


    @Override
    public void test() throws Exception {

//        System.out.println("标题:"+readMap.get("标题"));
//        System.out.println("key_word："+readMap.get("key_word"));
//        System.out.println("问题描述:"+readMap.get("问题描述"));
//        System.out.println("告警信息："+readMap.get("告警信息"));
//        System.out.println("处理过程："+readMap.get("处理过程"));
//        System.out.println("根因："+readMap.get("根因"));
//        System.out.println("解决方案："+readMap.get("解决方案"));
//        System.out.println("建议和总结："+readMap.get("建议和总结"));

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
        insert(fileMap);
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
