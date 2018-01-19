package org.nomadic.test.service;

import org.nomadic.test.daotest.OneTestMapper;
import org.nomadic.test.domain.WordDO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional(value = "transactionManagerTest", rollbackFor = Exception.class)
@Service
public class WordTestService {

    @Resource
    private OneTestMapper oneTestMapper;

    public void insert(String name) {
        WordDO wordDO = new WordDO();
        wordDO.setName(name);
        oneTestMapper.insert(wordDO);
        throw new RuntimeException("用于rollback");
    }

    public void update(Long id, String name) {
        WordDO wordDO = new WordDO();
        wordDO.setId(id);
        wordDO.setName(name);
        oneTestMapper.update(wordDO);
    }
}
