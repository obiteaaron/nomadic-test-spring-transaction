package org.nomadic.test.service;

import org.nomadic.test.dao.OneTestMapper;
import org.nomadic.test.domain.WordDO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional(rollbackFor = Exception.class)
@Service
public class WordTestService {

    @Resource
    private OneTestMapper oneTestMapper;

    public void insert(String name) {
        WordDO wordDO = new WordDO();
        wordDO.setName(name);
        oneTestMapper.insert(wordDO);
    }

    public void update(Long id, String name) {
        WordDO wordDO = new WordDO();
        wordDO.setId(id);
        wordDO.setName(name);
        oneTestMapper.update(wordDO);
    }
}
