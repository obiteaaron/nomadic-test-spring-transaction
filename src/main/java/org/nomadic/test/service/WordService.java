package org.nomadic.test.service;

import org.nomadic.test.dao.OneMapper;
import org.nomadic.test.domain.WordDO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional(value = "transactionManager", rollbackFor = Exception.class)
@Service
public class WordService {

    @Resource
    private OneMapper oneMapper;

    public void insert(String name) {
        WordDO wordDO = new WordDO();
        wordDO.setName(name);
        oneMapper.insert(wordDO);
        throw new RuntimeException("用于rollback");
    }

    public void update(Long id, String name) {
        WordDO wordDO = new WordDO();
        wordDO.setId(id);
        wordDO.setName(name);
        oneMapper.update(wordDO);
    }
}
