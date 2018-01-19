package org.nomadic.test.daotest;

import org.mybatis.spring.annotation.MapperScan;
import org.nomadic.test.domain.WordDO;

@MapperScan
public interface OneTestMapper {
    int insert(WordDO wordDO);

    int update(WordDO wordDO);
}
