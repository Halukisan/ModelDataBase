package org.fusion.model;


import lombok.Data;

import java.util.List;

@Data
public class Milvus {
    private Long id;
    private String title;
    private String question;
    private List<Double> embedding;
    private String content;
}
