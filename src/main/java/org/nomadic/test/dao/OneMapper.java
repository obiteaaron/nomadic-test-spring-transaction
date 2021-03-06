package org.nomadic.test.dao;

import org.mybatis.spring.annotation.MapperScan;
import org.nomadic.test.domain.WordDO;

@MapperScan
public interface OneMapper {
    int insert(WordDO wordDO);

    int update(WordDO wordDO);
}
