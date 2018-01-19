package org.nomadic.test.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WordDO {
    private Long id;
    private Long gmtCreate;
    private Date gmtModified;
    private String name;
    private String features;
}
