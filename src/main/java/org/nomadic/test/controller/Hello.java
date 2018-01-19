package org.nomadic.test.controller;

import org.nomadic.test.service.WordService;
import org.nomadic.test.service.WordTestService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("hello")
public class Hello {

    @Resource
    private WordService wordService;

    @Resource
    private WordTestService wordTestService;

    private static final String ACTION_INSERT = "insert";
    private static final String ACTION_UPDATE = "update";

    @RequestMapping("testDb1/{action}/{name}/{id}")
    public Object testDb1(@PathVariable("action") String action, @PathVariable("name") String name, @PathVariable(required = false, value = "id") Long id) {
        if (ACTION_INSERT.equals(action)) {
            wordService.insert(name);
            return ACTION_INSERT;
        } else if (ACTION_UPDATE.equals(action)) {
            wordService.update(id, name);
            return ACTION_UPDATE;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @RequestMapping("testDb2/{action}/{name}/{id}")
    public Object testDb2(@PathVariable("action") String action, @PathVariable("name") String name, @PathVariable(required = false, value = "id") Long id) {
        if (ACTION_INSERT.equals(action)) {
            wordTestService.insert(name);
            return ACTION_INSERT;
        } else if (ACTION_UPDATE.equals(action)) {
            wordTestService.update(id, name);
            return ACTION_UPDATE;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
